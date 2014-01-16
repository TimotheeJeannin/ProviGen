package com.tjeannin.provigen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field of a contract class that corresponds to the id of another contract.</br>
 * The value attribute should be a column from another contract class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
	String value();
}
