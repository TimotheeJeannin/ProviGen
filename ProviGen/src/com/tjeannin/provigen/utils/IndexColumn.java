package com.tjeannin.provigen.utils;

import java.io.Serializable;

/**
 * This class holds the information about a column in an index. This class also implements the {@link java.lang.Comparable}
 * interface for sorting the IndexColumns by their position in the index.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public class IndexColumn implements Comparable<IndexColumn>, Serializable {
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
	IndexColumn(final String name, final int position) {
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
