package com.tjeannin.provigen.annotation;

public enum IndexType {
	INDEX("INDEX"),

	UNIQUE("UNIQUE INDEX");
	private final String sqlPart;

	IndexType(final String sqlPart) {
		this.sqlPart = sqlPart;
	}

	public String getSqlPart() {
		return sqlPart;
	}
}
