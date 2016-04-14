package com.tjeannin.provigen.model;

/**
 * Created by Dre on 14.04.2016.
 *
 */
public class ForeignKeyConstraint {

    private String column;
    private String tableReferenced;
    private String columnReferenced;

    public ForeignKeyConstraint() {}

    public ForeignKeyConstraint(String column, String tableReferenced, String columnReferenced) {
        this.column = column;
        this.tableReferenced = tableReferenced;
        this.columnReferenced = columnReferenced;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getTableReferenced() {
        return tableReferenced;
    }

    public void setTableReferenced(String tableReferenced) {
        this.tableReferenced = tableReferenced;
    }

    public String getColumnReferenced() {
        return columnReferenced;
    }

    public void setColumnReferenced(String columnReferenced) {
        this.columnReferenced = columnReferenced;
    }
}
