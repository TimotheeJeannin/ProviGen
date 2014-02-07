package com.tjeannin.provigen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Index;

class DatabaseField {
	public static final Comparator<DatabaseField> INDEX_COMPARATOR = new IndexComparator();

	private String name;
	private Column.Type type;
	private Index index;
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

	public void setIndex(final Index index) {
		this.index = index;
	}

	public Index getIndex() {
		return index;
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

	private static class IndexComparator implements Comparator<DatabaseField>, Serializable {
		@Override
		public int compare(final DatabaseField o1, final DatabaseField o2) {
			final Index idx1 = o1.getIndex();
			final Index idx2 = o2.getIndex();
			return Integer.valueOf(idx2.weight()).compareTo(idx1.weight());
		}
	}

}
