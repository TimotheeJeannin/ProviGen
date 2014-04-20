package com.tjeannin.provigen;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProviGenDatabaseHelpers {

    public static void addMissingColumns(SQLiteDatabase database, Class contractClass) throws InvalidContractException {

        ContractHolder contractHolder = new ContractHolder(contractClass);

        Cursor cursor = database.rawQuery("PRAGMA table_info(" + contractHolder.getTable() + ")", null);
        for (DatabaseField field : contractHolder.getFields()) {
            if (!fieldExistAsColumn(field.name, cursor)) {
                database.execSQL("ALTER TABLE " + contractHolder.getTable() + " ADD COLUMN " + field.name + " " + field.type + ";");
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
