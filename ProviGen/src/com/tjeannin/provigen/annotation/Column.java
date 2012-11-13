package com.tjeannin.provigen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.tjeannin.provigen.Type;

/**
 * Identifies a field of a contract class that corresponds to a column in the database.</br>
 * The {@link Type} attribute should be one of the following:
 * <ul>
 * <li>{@link Type#INTEGER}</li>
 * <li>{@link Type#BLOB}</li>
 * <li>{@link Type#REAL}</li>
 * <li>{@link Type#TEXT}</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
	String type();
}
