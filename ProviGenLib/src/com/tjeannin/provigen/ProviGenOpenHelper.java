package com.tjeannin.provigen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.helper.TableUpdater;

/**
 * A simple implementation of a {@link SQLiteOpenHelper} that:
 * <ul>
 * <li>Create a table for each supplied contract when the database is created for the first time.</li>
 * <li>Add missing table columns when the database version is increased.</li>
 * </ul>
 */
public class ProviGenOpenHelper extends SQLiteOpenHelper {

    private final Class[] contracts;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context      the context to use to open or create the database.
     * @param databaseName the name of the database file, or null for an in-memory database.
     * @param factory      the factory to use for creating cursor objects, or null for the default.
     * @param version      the version of the database. Each time the version is increased, missing columns will be added.
     */
    public ProviGenOpenHelper(Context context, String databaseName, CursorFactory factory, int version, Class[] contractClasses) {
        super(context, databaseName, factory, version);
        this.contracts = contractClasses;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (Class contract : contracts)
            new TableBuilder(contract).createTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            for (Class contract : contracts)
                TableUpdater.addMissingColumns(database, contract);
        }
    }
}
