package com.tjeannin.provigen;

import com.tjeannin.provigen.database.Database;

/**
 * this interface defines the methods used from the SQLite and SqlCipher implementations of SQLiteOpenHelper,
 * {@link net.sqlcipher.database.SQLiteOpenHelper} and {@link android.database.sqlite.SQLiteOpenHelper}
 */
public interface OpenHelper {
	Database getWritableDb();
	Database getReadableDb();
}
