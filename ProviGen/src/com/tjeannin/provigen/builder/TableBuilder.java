package com.tjeannin.provigen.builder;

import android.database.sqlite.SQLiteDatabase;
import com.tjeannin.provigen.Constraint;
import com.tjeannin.provigen.ContractHolder;
import com.tjeannin.provigen.DatabaseField;
import com.tjeannin.provigen.InvalidContractException;

public class TableBuilder {

    private ContractHolder contractHolder;

    public TableBuilder(Class contractClass) throws InvalidContractException {
        contractHolder = new ContractHolder(contractClass);
    }

    public String getSQL() {

        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(contractHolder.getTable()).append(" ( ");

        for (DatabaseField field : contractHolder.getFields()) {
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
        return builder.toString();
    }

    public void createTable(SQLiteDatabase database) {
        database.execSQL(getSQL());
    }
}
