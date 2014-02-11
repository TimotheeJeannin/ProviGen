package com.tjeannin.provigen.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.tjeannin.provigen.annotation.IndexType;

/**
 * This class holds all information about an index.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public class IndexInformation implements Serializable {
	/**
	 * Name of the index.
	 */
	private final String m_indexName;
	/**
	 * Columns in the index.
	 */
	private final List<IndexColumn> m_indexColumns = new ArrayList<IndexColumn>(0);
	/**
	 * Type of the index.
	 */
	private final IndexType m_type;
	/**
	 * Expressions for partial index.
	 */
	private final Collection<String> m_expr = new ArrayList<String>(0);

	/**
	 * CTOR. indexColumns are sorted in the constructor by their position.
	 *
	 * @param indexName name of the index
	 * @param indexType is this an unique index
	 */
	IndexInformation(final String indexName, final IndexType indexType) {
		m_indexName = indexName;
		m_type = indexType;
		Collections.sort(m_indexColumns);
	}

	/**
	 * Get the name of the index.
	 *
	 * @return name of the index
	 */
	public String getIndexName() {
		return m_indexName;
	}

	/**
	 * Get the Type of the index..
	 *
	 * @return type of the index
	 */
	public IndexType getType() {
		return m_type;
	}

	/**
	 * Get an unmodifiable list of the column names in the index, sorted by their position in the index.
	 *
	 * @return an unmodifiable list of the column names in the index
	 */
	public List<String> getIndexColumnNames() {
		final List<String> result = new ArrayList<String>(m_indexColumns.size());
		for (final IndexColumn column : m_indexColumns) {
			result.add(column.getName());
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * Get an unmodifiable list of expressions for creation of a partial index.
	 *
	 * @return an unmodifiable list of expressions for partial index
	 */
	public Collection<String> getExpressions() {
		return Collections.unmodifiableCollection(m_expr);
	}

	/**
	 * Add a new index column to the information.
	 *
	 * @param columnName column name
	 * @param position   position of the column in the index
	 * @param expr       expression for partial index
	 */
	public void addNewIndexColumn(final String columnName, final int position, final String expr) {
		if (expr != null && expr.trim().length() > 0) {
			m_expr.add(String.format("(%s)", expr));
		}
		m_indexColumns.add(new IndexColumn(columnName, position));
		Collections.sort(m_indexColumns);
	}

	@Override
	public String toString() {
		return "IndexInformation{" +
				"indexName='" + m_indexName + '\'' +
				", type=" + m_type +
				", indexColumns=" + m_indexColumns +
				", expr=" + m_expr +
				'}';
	}

	/**
	 * This class holds the information about a column in an index. This class also implements the {@link Comparable}
	 * interface for sorting the IndexColumns by their position in the index.
	 *
	 * @author Michael Cramer <michael@bigmichi1.de>
	 * @since 1.6
	 */
	private static class IndexColumn implements Comparable<IndexColumn>, Serializable {
		/**
		 * Name of the column.
		 */
		private final String m_name;
		/**
		 * Position of the column in the index.
		 */
		private final int m_position;

		/**
		 * CTOR.
		 *
		 * @param name     name of the column
		 * @param position position of the column in the index.
		 */
		private IndexColumn(final String name, final int position) {
			m_name = name;
			m_position = position;
		}

		/**
		 * Get the name of the column.
		 *
		 * @return name of the column
		 */
		public String getName() {
			return m_name;
		}

		@Override
		public int compareTo(final IndexColumn another) {
			return m_position > another.m_position ? 1 : m_position < another.m_position ? -1 : 0;
		}

		@Override
		public String toString() {
			return "IndexColumns{" +
					"name='" + m_name + '\'' +
					", position=" + m_position +
					'}';
		}
	}
}
