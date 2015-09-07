package com.tjeannin.provigen.sqlciphersample;

import android.app.Application;
import com.tjeannin.provigen.ProviGenSQLCipherOpenHelper;
import net.sqlcipher.database.SQLiteDatabase;

public class ProviGenSqlcipherSampleApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();

		// all applications using SqlCipher must call loadLibs()
		SQLiteDatabase.loadLibs(getApplicationContext());

		// we must set the database file encryption password before anyone else tries to access it.
		ProviGenSQLCipherOpenHelper.setPassword("swordfish");
	}
}
