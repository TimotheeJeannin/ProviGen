package com.tjeannin.provigen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.exceptions.IndexException;
import com.tjeannin.provigen.utils.DataBaseHelper;

final class IndexUtils {
	/**
	 * Logging tag.
	 */
	public static final String TAG = "PROVIGEN_INDEX";
	/**
	 * Prefix for autmatic created index names.
	 */
	private static final String PROVIGEN_INDEX_PREFIX = "provigen_index_";

	private IndexUtils() {
	}

	static void addConstraints(final SQLiteDatabase database, final ContractHolder holder) throws IndexException {
		final Map<IndexType, Map<String, List<DatabaseField>>> allConstraints = getIndexList(holder);
		for (final IndexType type : IndexType.values()) {
			if (allConstraints.containsKey(type)) {
				for (final Map.Entry<String, List<DatabaseField>> constraint : allConstraints.get(type).entrySet()) {
					final StringBuilder builder = new StringBuilder("CREATE ").append(type.getSqlPart()).append(' ');
					builder.append(getConstraintName(database, constraint.getKey().trim()));
					builder.append(" ON ").append(holder.getTable()).append('(');
					final List<DatabaseField> columns = constraint.getValue();
					if (columns.size() > 1) {
						Collections.sort(columns, DatabaseField.INDEX_COMPARATOR);
					}
					final Collection<String> expressions = new ArrayList<String>(0);
					for (final Iterator<DatabaseField> iterator = columns.iterator(); iterator.hasNext(); ) {
						final DatabaseField field = iterator.next();
						builder.append(field.getName());
						final String expr = field.getIndex().expr().trim();
						if (!TextUtils.isEmpty(expr)) {
							expressions.add(expr.trim());
						}
						if (iterator.hasNext()) {
							builder.append(", ");
						}
					}
					builder.append(')');
					if (!expressions.isEmpty() && DataBaseHelper.sqLiteVersionGreaterOrEqual(database, "3.8.0")) {
						builder.append(" WHERE ");
						for (final Iterator<String> iterator = expressions.iterator(); iterator.hasNext(); ) {
							final String expr = iterator.next();
							builder.append('(').append(expr).append(')');
							if (iterator.hasNext()) {
								builder.append(" OR ");
							}
						}
					} else {
						Log.i(TAG, "Database doesn't support partial index.");
					}
					Log.v(TAG, builder.toString());
					database.execSQL(builder.toString());
				}
			}
		}
	}

	private static String getConstraintName(final SQLiteDatabase database, final String constraintName) throws IndexException {
		if (!TextUtils.isEmpty(constraintName)) {
			final Cursor cursor = database.rawQuery("SELECT type, tbl_name FROM sqlite_master WHERE name = ?", new String[] { constraintName.trim() });
			final boolean exists = cursor.getCount() != 0;
			if (exists) {
				cursor.moveToFirst();
				final String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));
				final String type = cursor.getString(cursor.getColumnIndex("type"));
				final String message = String.format("There is allready an object (%s) with the name %s  on table %s in the database", type, constraintName, tableName);
				cursor.close();
				throw new IndexException(message);
			}
			cursor.close();
			return constraintName;
		} else {
			final Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE name like ?", new String[] { PROVIGEN_INDEX_PREFIX + '%' });
			if (cursor.getCount() == 0) {
				return String.format("%s%d", PROVIGEN_INDEX_PREFIX, 1);
			} else {
				cursor.moveToFirst();
				int counter = 0;
				do {
					final String name = cursor.getString(cursor.getColumnIndex("name"));
					final String number = name.replace(PROVIGEN_INDEX_PREFIX, "");
					final int cnt = Integer.parseInt(number);
					if (counter < cnt) {
						counter = cnt;
					}
				} while (cursor.moveToNext());
				return String.format("%s%d", PROVIGEN_INDEX_PREFIX, counter + 1);
			}
		}
	}

	public static Map<IndexType, Map<String, List<DatabaseField>>> getIndexList(final ContractHolder holder) throws IndexException {
		final Map<IndexType, Map<String, List<DatabaseField>>> indexList = new EnumMap<IndexType, Map<String, List<DatabaseField>>>(IndexType.class);
		for (final DatabaseField field : holder.getFields()) {
			final Index index = field.getIndex();
			if (index != null) {
				final IndexType type = index.type();
				final Map<String, List<DatabaseField>> map;
				if (indexList.containsKey(type)) {
					map = indexList.get(type);
				} else {
					map = new HashMap<String, List<DatabaseField>>(1);
					indexList.put(type, map);
				}
				final String indexName = index.name().trim();
				if (!TextUtils.isEmpty(indexName) && isNameInOtherTypesDefined(indexName, type, indexList)) {
					throw new IndexException(String.format("Index with name %s was allready defined with another index type. Actual type: %s", indexName, type));
				}
				if (map.containsKey(indexName)) {
					map.get(indexName).add(field);
				} else {
					final List<DatabaseField> fields = new ArrayList<DatabaseField>(1);
					fields.add(field);
					map.put(indexName, fields);
				}
			}
		}
		return indexList;
	}

	private static boolean isNameInOtherTypesDefined(final String indexName, final IndexType type, final Map<IndexType, Map<String, List<DatabaseField>>> indexList) {
		for (final Map.Entry<IndexType, Map<String, List<DatabaseField>>> entry : indexList.entrySet()) {
			if (entry.getKey() != type && entry.getValue().keySet().contains(indexName)) {
				return true;
			}
		}
		return false;
	}
}
