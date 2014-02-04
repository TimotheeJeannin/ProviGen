package com.tjeannin.provigen.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation specifies the sorting of a result returned by the query.
 * Ordering is done by setting this annotation to a column on which the result is ordered.
 * If the annotaion is set on more than one column, ordering is done for all columns. Each
 * column is weighted to order the columns in the sort order query part.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SortOrder {
	/**
	 * Default ordering for the column.
	 *
	 * @return Ordering
	 */
	Order order() default Order.UNSORTED;

	/**
	 * Weighting for the column, if more than one column has a {@link SortOrder}.
	 *
	 * @return weighting
	 */
	int weight() default 0;

	enum Order {
		/**
		 * The query result has no specific order.
		 */
		UNSORTED,
		/**
		 * The query result is ordered ascending.
		 */
		ASC,
		/**
		 * The query result is ordered descending.
		 */
		DESC
	}
}
