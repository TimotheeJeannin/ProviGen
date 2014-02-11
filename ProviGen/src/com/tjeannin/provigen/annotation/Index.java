package com.tjeannin.provigen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identifies a field of a contract class that should be indexed in the database.</br>
 * This constraint is <b>only</b> applied on table creation. <br/>
 * Adding this annotation to a {@link Contract} with an already created table will have <b>no effect</b>.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Index {
	/**
	 * Which type the index should be.
	 *
	 * @return type of the index
	 */
	IndexType type() default IndexType.INDEX;

	/**
	 * Name of the index. If no name is given a name will be generated.
	 *
	 * @return name
	 */
	String name() default "";

	/**
	 * Position of the field in the index. Higher value means column is ordered after columns with lower value.
	 *
	 * @return weighting of the column in the index.
	 */
	int position() default 0;

	/**
	 * Defines an expression for creating an "partial index".<p><b>This will only work with SQLite version greater or equal
	 * 3.8.0.</b> All other versions ignore these expression silently</p>
	 *
	 * @see <a href="http://www.sqlite.org/lang_createindex.html">SQLite Query Language: CREATE INDEX</a>
	 */
	String expr() default "";
}
