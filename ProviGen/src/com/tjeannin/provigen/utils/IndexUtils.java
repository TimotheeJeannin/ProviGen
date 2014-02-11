package com.tjeannin.provigen.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.exceptions.IndexException;

public final class IndexUtils {
	/**
	 * Logging tag.
	 */
	public static final String TAG = "PROVIGEN_INDEX";

	private IndexUtils() {
	}

	public static void addConstraints(final SQLiteDatabase database, final String tableName, final Collection<IndexInformation> indexInformation) throws IndexException {
		final List<IndexInformation> table = getIndexInformationForTable(database, tableName);
		final Collection<IndexInformation> toAdd = diff(indexInformation, table);
		final Collection<IndexInformation> toRemove = diff(table, indexInformation);

		for (final IndexInformation information : toRemove) {
			final StringBuilder builder = new StringBuilder("DROP INDEX ");
			builder.append(information.getIndexName());
			Log.v(TAG, builder.toString());
			database.execSQL(builder.toString());
		}

		for (final IndexInformation index : toAdd) {
			final StringBuilder builder = new StringBuilder("CREATE ").append(index.getType().getSqlPart()).append(' ');
			builder.append(getIndexName(database, index.getIndexName()));
			builder.append(" ON ").append(tableName);
			builder.append('(');
			builder.append(TextUtils.join(", ", index.getIndexColumnNames()));
			builder.append(')');
			if (!index.getExpressions().isEmpty()) {
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

	private static String getIndexName(final SQLiteDatabase database, final String indexName) throws IndexException {
		if (indexName == null || indexName.trim().length() == 0) {
			throw new IndexException("Index name can not be empty");
		} else {
			final String index = indexName.trim();
			final Cursor cursor = database.rawQuery("SELECT type, tbl_name FROM sqlite_master WHERE name = ?", new String[] { index });
			final boolean exists = cursor.getCount() != 0;
			if (exists) {
				cursor.moveToFirst();
				final String tableName = cursor.getString(cursor.getColumnIndex("tbl_name"));
				final String type = cursor.getString(cursor.getColumnIndex("type"));
				final String message = String.format("There is allready an object (%s) with the name %s  on table %s in the database", type, index, tableName);
				cursor.close();
				throw new IndexException(message);
			}
			cursor.close();
			return index;
		}
	}

	/**
	 * Load all index information for a table.
	 *
	 * @param database  database where the table is searched
	 * @param tableName name of the table
	 * @return an unmodifiable list with all index information
	 */
	public static List<IndexInformation> getIndexInformationForTable(final SQLiteDatabase database, final String tableName) {
		final Cursor cursor = database.rawQuery(String.format("PRAGMA INDEX_LIST(%s)", tableName), null);
		final int count = cursor.getCount();
		if (count > 0) {
			cursor.moveToFirst();
			final List<IndexInformation> result = new ArrayList<IndexInformation>(count);
			do {
				final String indexNameFromDB = cursor.getString(cursor.getColumnIndex("name"));
				if (!indexNameFromDB.startsWith("sqlite_autoindex_")) {
					final boolean isUnique = "1".equals(cursor.getString(cursor.getColumnIndex("unique")));
					final IndexInformation indexInformation = new IndexInformation(indexNameFromDB, isUnique ? IndexType.UNIQUE : IndexType.INDEX);
					getIndexInformation(database, indexInformation);
					result.add(indexInformation);
				}
			} while (cursor.moveToNext());
			return Collections.unmodifiableList(result);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Load information about an index from database.
	 *
	 * @param database         database where the index is searched
	 * @param indexInformation name of the index
	 */
	private static void getIndexInformation(final SQLiteDatabase database, final IndexInformation indexInformation) {
		final String indexName = indexInformation.getIndexName();
		final Cursor cursor = database.rawQuery(String.format("PRAGMA INDEX_INFO(%s)", indexName), null);
		final int count = cursor.getCount();
		if (count > 0) {
			cursor.moveToFirst();
			do {
				final int position = cursor.getInt(cursor.getColumnIndex("seqno"));
				final String columnName = cursor.getString(cursor.getColumnIndex("name"));
				indexInformation.addNewIndexColumn(columnName, position, "");
			} while (cursor.moveToNext());
		} else {
			Log.i(TAG, String.format("No index information found for index with name '%s'", indexName));
		}
	}

	private static <T> Collection<T> diff(final Collection<T> minuend, final Collection<T> subtrahend) {
		final Collection<T> result = new ArrayList<T>(minuend.size());
		for (final T t : minuend) {
			if (!subtrahend.contains(t)) {
				result.add(t);
			}
		}
		return result;
	}
}
