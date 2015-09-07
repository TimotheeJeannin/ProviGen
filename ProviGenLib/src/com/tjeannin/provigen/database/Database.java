package com.tjeannin.provigen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

/**
 * this interface defines the methods used from {@link net.sqlcipher.database.SQLiteDatabase} and
 * {@link android.database.sqlite.SQLiteDatabase}
 */
public interface Database {
	Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy);

	Cursor rawQuery(String sql, String[] selectionArgs);

	long insert(String table, String nullColumnHack, ContentValues values);

	int delete(String table, String whereClause, String[] whereArgs);

	int update(String table, ContentValues values, String whereClause, String[] whereArgs);

	void execSQL(String sql) throws SQLException;
}
