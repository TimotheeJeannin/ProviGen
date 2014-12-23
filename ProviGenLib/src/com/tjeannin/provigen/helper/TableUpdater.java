package com.tjeannin.provigen.helper;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tjeannin.provigen.annotation.Column;
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

                StringBuilder builder = new StringBuilder("ALTER TABLE ");
                builder.append(contract.getTable()).append(" ADD COLUMN ");

                builder.append(" ").append(field.name).append(" ").append(field.type);

                if (field.defaultValue != null) {
                    if (field.type.equals(Column.Type.TIMESTAMP)) {
                        throw new IllegalArgumentException("SQLite doesn't support DEFAULT value for TIMESTAMP " +
                                "when you add additional column to an already created TABLE.");
                    } else {
                        if(field.defaultValue.equals(Column.DefaultValue.NULL)) {
                            builder.append(" DEFAULT NULL");
                        } else {
                            builder.append(" DEFAULT '").append(field.defaultValue).append("'");
                        }
                    }
                }
                builder.append(";");

                database.execSQL(builder.toString());
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
