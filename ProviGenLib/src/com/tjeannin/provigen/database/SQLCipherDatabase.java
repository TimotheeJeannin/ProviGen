package com.tjeannin.provigen.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

/**
 * the {@link SQLiteDatabase} implementation of the {@link Database} interface. it delegates
 * to a concrete {@link SQLiteDatabase}.
 */
public class SQLCipherDatabase implements Database {
	private final SQLiteDatabase delegate;

	public SQLCipherDatabase(SQLiteDatabase delegate) {
		this.delegate = delegate;
	}

	@Override
	public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		return delegate.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
	}

	@Override
	public Cursor rawQuery(String sql, String[] selectionArgs) {
		return delegate.rawQuery(sql, selectionArgs);
	}

	@Override
	public long insert(String table, String nullColumnHack, ContentValues values) {
		return delegate.insert(table, nullColumnHack, values);
	}

	@Override
	public int delete(String table, String whereClause, String[] whereArgs) {
		return delegate.delete(table, whereClause, whereArgs);
	}

	@Override
	public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
		return delegate.update(table, values, whereClause, whereArgs);
	}

	@Override
	public void execSQL(String sql) throws SQLException {
		delegate.execSQL(sql);
	}
}

