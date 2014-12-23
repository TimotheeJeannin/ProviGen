package com.tjeannin.provigen.annotation;

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
 *
 * If the Type is {@link Type#TIMESTAMP}, the defaultValue attribute can be one of the following:
 * <ul>
 * <li>{@link DefaultValue#CURRENT_TIME}</li>
 * <li>{@link DefaultValue#CURRENT_DATE}</li>
 * <li>{@link DefaultValue#CURRENT_TIMESTAMP}</li>
 * </ul>
 *
 * For all the other Types, it can be either {@link DefaultValue#NULL} or any String value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String value();

    String defaultValue() default "NULL";

    public class Type {
        public static final String INTEGER = "INTEGER";
        public static final String REAL = "REAL";
        public static final String TEXT = "TEXT";
        public static final String BLOB = "BLOB";
        public static final String TIMESTAMP = "TIMESTAMP";
    }

    public class DefaultValue {
        public static final String CURRENT_TIME = "CURRENT_TIME";
        public static final String CURRENT_DATE = "CURRENT_DATE";
        public static final String CURRENT_TIMESTAMP = "CURRENT_TIMESTAMP";
        public static final String NULL = "NULL";
    }
}
