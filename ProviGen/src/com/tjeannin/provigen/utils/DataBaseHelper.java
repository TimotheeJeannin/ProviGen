package com.tjeannin.provigen.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.exceptions.DatabaseException;

/**
 * Some helpers for working with the database.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public final class DataBaseHelper {
	/**
	 * Logging tag.
	 */
	public static final String TAG = "PROVIGEN_DATABASE";

	private DataBaseHelper() {
	}

	/**
	 * Determine the current running SQLite version.
	 *
	 * @param database database which should be tested
	 * @return version identifier
	 * @throws DatabaseException version can't be determined.
	 */
	public static String getSQLiteVersion(final SQLiteDatabase database) throws DatabaseException {
		final Cursor cursor = database.rawQuery("SELECT sqlite_version() AS sqlite_version", null);
		if (cursor.getCount() == 1) {
			cursor.moveToFirst();
			final String version = cursor.getString(cursor.getColumnIndex("sqlite_version"));
			Log.d(TAG, String.format("Using SQLite version '%s'", version));
			return version;
		} else {
			throw new DatabaseException("Unknown SQLite version");
		}
	}

	/**
	 * Determines if the current running SQLite version is greater or equal than the given on.
	 * Method returns also false if the version can't be determined.
	 *
	 * @param database database which should be tested
	 * @param version  version that should be compared
	 * @return {@code true} if the version is greater or equal the given one; {@code false} otherwise
	 */
	public static boolean isRunningSQLiteVersionGreaterOrEqual(final SQLiteDatabase database, final String version) {
		try {
			final String currentVersion = getSQLiteVersion(database);
			return VersionComparator.VERSION_COMPARATOR.compare(currentVersion, version) >= 0;
		} catch (final DatabaseException e) {
			Log.e(TAG, "Unable to read SQLite version", e);
		}
		return false;
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
				final String indexName = cursor.getString(cursor.getColumnIndex("name"));
				final boolean isUnique = "1".equals(cursor.getString(cursor.getColumnIndex("unique")));
				final IndexInformation indexInformation = new IndexInformation(indexName, isUnique ? IndexType.UNIQUE : IndexType.INDEX);
				getIndexInformation(database, indexInformation);
				result.add(indexInformation);
			} while (cursor.moveToNext());
			return Collections.unmodifiableList(result);
		} else {
			return Collections.emptyList();
		}
	}

	/**
	 * Load information about an index from database.
	 *
	 * @param database  database where the index is searched
	 * @param indexInformation name of the index
	 */
	public static void getIndexInformation(final SQLiteDatabase database, final IndexInformation indexInformation) {
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
}
