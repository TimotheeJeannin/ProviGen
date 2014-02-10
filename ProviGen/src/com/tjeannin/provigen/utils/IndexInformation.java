package com.tjeannin.provigen.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
	 * Is this an unique index.
	 */
	private final boolean m_isUnique;
	/**
	 * Columns in the index.
	 */
	private final List<IndexColumn> m_indexColumns = new ArrayList<IndexColumn>(0);

	/**
	 * CTOR. indexColumns are sorted in the constructor by their position.
	 *
	 * @param indexName    name of the index
	 * @param isUnique     is this an unique index
	 * @param indexColumns columns in the index
	 */
	IndexInformation(final String indexName, final boolean isUnique, final Collection<IndexColumn> indexColumns) {
		m_indexName = indexName;
		m_isUnique = isUnique;
		m_indexColumns.addAll(indexColumns);
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
	 * Is this an unique index.
	 *
	 * @return {@code true} when this is an unique index; {@code false} otherwise
	 */
	public boolean isUnique() {
		return m_isUnique;
	}

	/**
	 * Get an unmodifiable list of the columns in the index, sorted by their position in the index.
	 *
	 * @return an unmodifiable list of the columns in the index
	 */
	public List<IndexColumn> getIndexColumns() {
		return Collections.unmodifiableList(m_indexColumns);
	}

	@Override
	public String toString() {
		return "IndexInformation{" +
				"indexName='" + m_indexName + '\'' +
				", isUnique=" + m_isUnique +
				", indexColumns=" + m_indexColumns +
				'}';
	}
}
