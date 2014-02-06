package com.tjeannin.provigen;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.tjeannin.provigen.annotation.SortOrder;

class DatabaseField {

	private String name;
	private String type;
	private SortOrder sortOrder;
	private List<Constraint> constraints;

	public static final SortOrderComparator SORT_ORDER_COMPARATOR = new SortOrderComparator();

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

	public SortOrder getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(final SortOrder sortOrder) {
		this.sortOrder = sortOrder;
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

	private static class SortOrderComparator implements Comparator<DatabaseField> {
		@Override
		public int compare(final DatabaseField lhs, final DatabaseField rhs) {
			return Integer.valueOf(rhs.getSortOrder().weight()).compareTo(lhs.getSortOrder().weight());
		}
	}
}
