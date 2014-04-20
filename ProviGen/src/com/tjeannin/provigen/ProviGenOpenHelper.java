package com.tjeannin.provigen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.helper.TableUpdater;

public class ProviGenOpenHelper extends SQLiteOpenHelper {

    private final Class[] contracts;

    public ProviGenOpenHelper(Context context, Class[] contractClasses, int version) {
        this(context, "ProviGenDatabase", null, version, contractClasses);
    }

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
        for (Class contract : contracts)
            TableUpdater.addMissingColumns(database, contract);
    }
}
