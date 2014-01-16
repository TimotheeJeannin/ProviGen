package com.tjeannin.provigen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field of a contract class that should not be null in the database.</br>
 * The value attribute should be one of the following:
 * <ul>
 * <li>{@link com.tjeannin.provigen.annotation.OnConflict.Resolve#ROLLBACK}</li>
 * <li>{@link com.tjeannin.provigen.annotation.OnConflict.Resolve#ABORT}</li>
 * <li>{@link com.tjeannin.provigen.annotation.OnConflict.Resolve#FAIL}</li>
 * <li>{@link com.tjeannin.provigen.annotation.OnConflict.Resolve#IGNORE}</li>
 * <li>{@link com.tjeannin.provigen.annotation.OnConflict.Resolve#REPLACE}</li>
 * </ul>
 * This constraint is <b>only</b> applied on table creation. <br/>
 * Adding this annotation to a {@link Contract} with an already created table will have <b>no effect</b>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NotNull {
}
