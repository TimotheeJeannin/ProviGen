package com.tjeannin.provigen;

import java.util.ArrayList;
import java.util.List;

class DatabaseField {

	private String name;
	private String type;
	private List<Constraint> constraints;

	public DatabaseField(String name, String type) {
		super();
		this.name = name;
		this.type = type;
		this.constraints = new ArrayList<Constraint>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public boolean isUnique() {
		for (Constraint constraint : constraints) {
			if (constraint.isUnique()) {
				return true;
			}
		}
		return false;
	}

	public boolean isNotNull() {
		for (Constraint constraint : constraints) {
			if (constraint.isNotNull()) {
				return true;
			}
		}
		return false;
	}
}
