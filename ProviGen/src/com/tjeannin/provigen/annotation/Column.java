package com.tjeannin.provigen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field of a contract class that corresponds to a column in the database.</br>
 * The value attribute should be one of the following:
 * <ul>
 * <li>{@link Type#INTEGER}</li>
 * <li>{@link Type#BLOB}</li>
 * <li>{@link Type#REAL}</li>
 * <li>{@link Type#TEXT}</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Column {
	Type value();

	enum Type {
		/**
		 * The value is a signed integer, stored in 1, 2, 3, 4, 6, or 8 bytes depending
		 * on the magnitude of the value.
		 */
		INTEGER("INTEGER"),
		/**
		 * The value is a floating point value, stored as an 8-byte IEEE floating point number.
		 */
		REAL("REAL"),
		/**
		 * The value is a text string, stored using the database encoding (UTF-8, UTF-16BE or UTF-16LE).
		 */
		TEXT("TEXT"),
		/**
		 * The value is a blob of data, stored exactly as it was input.
		 */
		BLOB("BLOB");
		private final String dbStorageClass;

		Type(final String dbStorageClass) {
			this.dbStorageClass = dbStorageClass;
		}

		/**
		 * Return the SQLite storage class.
		 * @return SQLite database storage class
		 */
		public String getDBStorageClass() {
			return dbStorageClass;
		}
	}
}
