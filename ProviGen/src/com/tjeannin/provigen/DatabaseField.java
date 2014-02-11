package com.tjeannin.provigen;

import java.util.ArrayList;
import java.util.List;

import com.tjeannin.provigen.annotation.Column;

public class DatabaseField {
	private String name;
	private Column.Type type;
	private List<Constraint> constraints;

	DatabaseField(final String name, final Column.Type type) {
		this.name = name;
		this.type = type;
		this.constraints = new ArrayList<Constraint>(2);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Column.Type getType() {
		return type;
	}

	public void setType(final Column.Type type) {
		this.type = type;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(final List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public boolean isUnique() {
		for (final Constraint constraint : constraints) {
			if (constraint.isUnique()) {
				return true;
			}
		}
		return false;
	}

	public boolean isNotNull() {
		for (final Constraint constraint : constraints) {
			if (constraint.isNotNull()) {
				return true;
			}
		}
		return false;
	}
}
