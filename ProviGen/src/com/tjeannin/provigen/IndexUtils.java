package com.tjeannin.provigen;

import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.tjeannin.provigen.exceptions.IndexException;
import com.tjeannin.provigen.utils.DataBaseHelper;
import com.tjeannin.provigen.utils.IndexInformation;

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
		final List<IndexInformation> indexInformation = holder.getIndexInformation();
		for (final IndexInformation index : indexInformation) {
			final StringBuilder builder = new StringBuilder("CREATE ").append(index.getType().getSqlPart()).append(' ');
			builder.append(getConstraintName(database, index.getIndexName().trim()));
			builder.append(" ON ").append(holder.getTable());
			builder.append('(');
			builder.append(TextUtils.join(", ", index.getIndexColumnNames()));
			builder.append(')');
			if(!index.getExpressions().isEmpty()) {
				if (DataBaseHelper.isRunningSQLiteVersionGreaterOrEqual(database, "3.8.0")) {
					builder.append(" WHERE ");
					builder.append(TextUtils.join(" OR ", index.getExpressions()));
				} else {
					Log.i(TAG, "Database doesn't support partial index.");
				}
			}
			Log.v(TAG, builder.toString());
			database.execSQL(builder.toString());
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
}
