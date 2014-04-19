package com.tjeannin.provigen;

import java.util.ArrayList;
import java.util.List;

public class DatabaseField {

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
}
