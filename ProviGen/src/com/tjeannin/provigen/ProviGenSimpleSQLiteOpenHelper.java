package com.tjeannin.provigen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.helper.TableUpdater;

public class ProviGenSimpleSQLiteOpenHelper extends SQLiteOpenHelper {

    private final Class[] contracts;

    public ProviGenSimpleSQLiteOpenHelper(Context context, Class[] contractClasses, int version) {
        this(context, "ProviGenDatabase", null, version, contractClasses);
    }

    public ProviGenSimpleSQLiteOpenHelper(Context context, String databaseName, CursorFactory factory, int version, Class[] contractClasses) {
        super(context, databaseName, factory, version);
        this.contracts = contractClasses;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        for (Class contract : contracts)
            try {
                new TableBuilder(contract).createTable(database);
            } catch (InvalidContractException e) {
                e.printStackTrace();
            }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        for (Class contract : contracts)
            try {
                TableUpdater.addMissingColumns(database, contract);
            } catch (InvalidContractException e) {
                e.printStackTrace();
            }
    }
}
