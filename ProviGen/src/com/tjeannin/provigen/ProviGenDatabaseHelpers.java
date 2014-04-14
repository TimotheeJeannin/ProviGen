package com.tjeannin.provigen;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ProviGenDatabaseHelpers {

    public static void createTable(SQLiteDatabase database, Class contractClass) throws InvalidContractException {

        ContractHolder contractHolder = new ContractHolder(contractClass);

        // CREATE TABLE myTable (
        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(contractHolder.getTable()).append(" ( ");

        // myInt INTEGER, myString TEXT
        for (int i = 0; i < contractHolder.getFields().size(); i++) {
            DatabaseField field = contractHolder.getFields().get(i);
            builder.append(" ").append(field.getName()).append(" ").append(field.getType());
            if (field.getName().equals(contractHolder.getIdField())) {
                builder.append(" PRIMARY KEY AUTOINCREMENT ");
            }
            for (Constraint constraint : field.getConstraints()) {
                builder.append(" ").append(constraint.getType()).append(" ON CONFLICT ").append(constraint.getOnConflict());
            }
            builder.append(", ");
        }
        builder.deleteCharAt(builder.length() - 2);
        builder.append(" ) ");

        database.execSQL(builder.toString());
    }

    public static void addMissingColumns(SQLiteDatabase database, Class contractClass) throws InvalidContractException {

        ContractHolder contractHolder = new ContractHolder(contractClass);

        Cursor cursor = database.rawQuery("PRAGMA table_info(" + contractHolder.getTable() + ")", null);
        for (DatabaseField field : contractHolder.getFields()) {
            if (!fieldExistAsColumn(field.getName(), cursor)) {
                database.execSQL("ALTER TABLE " + contractHolder.getTable() + " ADD COLUMN " + field.getName() + " " + field.getType() + ";");
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
