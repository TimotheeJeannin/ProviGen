package com.tjeannin.provigen.helper;

import android.database.sqlite.SQLiteDatabase;
import com.tjeannin.provigen.model.Constraint;
import com.tjeannin.provigen.model.Contract;
import com.tjeannin.provigen.model.ContractField;
import com.tjeannin.provigen.model.Contract.InvalidContractException;

import java.util.ArrayList;
import java.util.List;

public class TableBuilder {

    private Contract contract;
    private List<Constraint> constraints;

    public TableBuilder(Class contractClass) throws InvalidContractException {
        contract = new Contract(contractClass);
        constraints = new ArrayList<Constraint>();
    }

    public TableBuilder addConstraint(String constraintColumn, String constraintType, String constraintConflictClause) {
        constraints.add(new Constraint(constraintColumn, constraintType, constraintConflictClause));
        return this;
    }

    public String getSQL() {

        StringBuilder builder = new StringBuilder("CREATE TABLE ");
        builder.append(contract.getTable()).append(" ( ");

        for (ContractField field : contract.getFields()) {
            builder.append(" ").append(field.name).append(" ").append(field.type);
            if (field.name.equals(contract.getIdField())) {
                builder.append(" PRIMARY KEY AUTOINCREMENT ");
            }
            for (Constraint constraint : constraints) {
                if (constraint.targetColumn.equals(field.name)) {
                    builder.append(" ").append(constraint.type).append(" ON CONFLICT ").append(constraint.conflictClause);
                }
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
