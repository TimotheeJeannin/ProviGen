package com.tjeannin.provigen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field of a contract class that should be used as a foreign key in the database. <br/>
 * This annotation should be used alongside with a {@link Column} annotation.<br/><br/>
 * <b>table</b> - The name of the table on which the foreign key is referenced.<br/>
 * <b>column</b> - The name of the column on which the foreign key is referenced.<br/>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ForeignKey {
    String table();
    String column();
}
