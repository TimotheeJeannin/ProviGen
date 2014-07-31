package com.tjeannin.provigen.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.tjeannin.provigen.model.Contract;
import com.tjeannin.provigen.model.ContractField;

/**
 * Facilitate the update of an database table from a contract class.
 */
public class TableUpdater {

    /**
     * Adds missing table columns for the given contract class.
     *
     * @param database      The database in which the updated table is.
     * @param contractClass The contract class to work with.
     */
    public static void addMissingColumns(SQLiteDatabase database, Class contractClass) {

        Contract contract = new Contract(contractClass);

        Cursor cursor = database.rawQuery("PRAGMA table_info(" + contract.getTable() + ")", null);
        for (ContractField field : contract.getFields()) {
            if (!fieldExistAsColumn(field.name, cursor)) {
                database.execSQL("ALTER TABLE " + contract.getTable() + " ADD COLUMN " + field.name + " " + field.type + ";");
            }
        }
    }

    private static boolean fieldExistAsColumn(String field, Cursor columnCursor) {
        if (columnCursor.moveToFirst()) {
            do {
                if (field.equals(columnCursor.getString(1))) {
                    return true;
                }
            } while (columnCursor.moveToNext());
        }
        return false;
    }
}
