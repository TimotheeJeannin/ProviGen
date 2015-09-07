package com.tjeannin.provigen;

import android.content.Context;
import com.tjeannin.provigen.database.Database;
import com.tjeannin.provigen.database.SQLCipherDatabase;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.helper.TableUpdater;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDatabase.CursorFactory;
import net.sqlcipher.database.SQLiteOpenHelper;

/**
 * A simple implementation of a {@link SQLiteOpenHelper} that:
 * <ul>
 * <li>Create a table for each supplied contract when the database is created for the first time.</li>
 * <li>Add missing table columns when the database version is increased.</li>
 * <li>Accepts the password used to decrypt the database file when it is first opened.</li>
 * </ul>
 */
public class ProviGenSQLCipherOpenHelper extends SQLiteOpenHelper implements OpenHelper {

	private static String sPassword;
	private static boolean sIsPasswordInitialized;

	private final Class[] contracts;

	public static void setPassword(String value) {
		sPassword = value;
		sIsPasswordInitialized = true;
	}

	private static String getPassword() {
		if ( ! sIsPasswordInitialized) {
			throw new IllegalStateException("ProviGenSQLCipherOpenHelper.setPassword must be called before we get here. Consider calling it early in your Application.onCreate method.");
		}

		return sPassword;
	}

	/**
	 * Create a helper object to create, open, and/or manage a database.
	 * This method always returns very quickly.  The database is not actually
	 * created or opened until one of {@link #getWritableDatabase} or
	 * {@link #getReadableDatabase} is called.
	 *
	 * @param context           the context to use to open or create the database.
	 * @param databaseName      the name of the database file, or null for an in-memory database.
	 * @param factory           the factory to use for creating cursor objects, or null for the default.
	 * @param version           the version of the database. Each time the version is increased, missing columns will be added.
	 * @param contractClasses   the list of contract classes
	 */
	public ProviGenSQLCipherOpenHelper(Context context, String databaseName, CursorFactory factory, int version, Class[] contractClasses) {
		super(context, databaseName, factory, version);
		this.contracts = contractClasses;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		Database db = new SQLCipherDatabase(database);
		for (Class contract : contracts)
			new TableBuilder(contract).createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			Database db = new SQLCipherDatabase(database);
			for (Class contract : contracts)
				TableUpdater.addMissingColumns(db, contract);
		}
	}

	@Override
	public Database getWritableDb() {
		return new SQLCipherDatabase(getWritableDatabase(getPassword()));
	}

	@Override
	public Database getReadableDb() {
		return new SQLCipherDatabase(getReadableDatabase(getPassword()));
	}
}
