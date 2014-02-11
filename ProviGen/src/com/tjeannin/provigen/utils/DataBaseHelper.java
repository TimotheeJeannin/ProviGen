package com.tjeannin.provigen.utils;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.tjeannin.provigen.exceptions.DatabaseException;

/**
 * Some helpers for working with the database.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public final class DataBaseHelper {

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
		if (cursor.getCount() == 1 && cursor.moveToFirst()) {
			final String version = cursor.getString(cursor.getColumnIndex("sqlite_version"));
			Log.d(IndexUtils.TAG, String.format("Using SQLite version '%s'", version));
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
			Log.e(IndexUtils.TAG, "Unable to read SQLite version", e);
		}
		return false;
	}

	/**
	 * Generate a list of the columns in an existing table.
	 *
	 * @param database  database where the table should be searched
	 * @param tableName name of the table
	 * @return list of column names
	 */
	public static List<String> getColumnInformation(final SQLiteDatabase database, final String tableName) {
		final Cursor cursor = database.rawQuery(String.format("PRAGMA table_info(%s)", tableName), null);
		final List<String> result = new ArrayList<String>(cursor.getCount());
		if (cursor.moveToFirst()) {
			do {
				final String columnName = cursor.getString(cursor.getColumnIndex("name"));
				result.add(columnName);
			} while (cursor.moveToNext());
		}
		return result;
	}
}
