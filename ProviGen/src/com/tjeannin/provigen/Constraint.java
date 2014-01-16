package com.tjeannin.provigen;

public class Constraint {

	/**
	 * A UNIQUE constraint is similar to a PRIMARY KEY constraint, except that a single table may have any number of UNIQUE constraints.
	 * For each UNIQUE constraint on the table, each row must feature a unique combination of values in the columns identified by the UNIQUE constraint.
	 * As with PRIMARY KEY constraints, for the purposes of UNIQUE constraints NULL values are considered distinct from all other values (including other NULLs).
	 * If an INSERT or UPDATE statement attempts to modify the table content so that two or more rows feature identical values in a set of columns that are subject to a UNIQUE constraint, it is a constraint violation.
	 */
	public static final String UNIQUE = "UNIQUE";

	/**
	 * A NOT NULL constraint may only be attached to a column definition, not specified as a table constraint.
	 * Not surprisingly, a NOT NULL constraint dictates that the associated column may not contain a NULL value.
	 * Attempting to set the column value to NULL when inserting a new row or updating an existing one causes a constraint violation.
	 */
	public static final String NOT_NULL = "NOT_NULL";

	private String type;
	private String onConflict;

	Constraint(String type, String onConflict) {
		super();
		this.type = type;
		this.onConflict = onConflict;
	}

	String getType() {
		return type;
	}

	void setType(String type) {
		this.type = type;
	}

	String getOnConflict() {
		return onConflict;
	}

	void setOnConflict(String onConflict) {
		this.onConflict = onConflict;
	}

	boolean isUnique() {
        return type != null && type.equals(UNIQUE);
    }

	boolean isNotNull() {
        return type != null && type.equals(NOT_NULL);
    }
}
