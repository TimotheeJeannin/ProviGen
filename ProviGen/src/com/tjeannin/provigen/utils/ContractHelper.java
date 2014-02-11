package com.tjeannin.provigen.utils;

import java.util.Collection;

import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.exceptions.IndexException;

public final class ContractHelper {
	private ContractHelper() {
	}

	public static void addIndexInformation(final Collection<IndexInformation> indexInformation, final String columnName, final Index index) throws IndexException {
		final String indexName = index.name();
		if (indexName.trim().length() == 0 || !hasIndexDefinition(indexInformation, index)) {
			indexInformation.add(createIndexInformation(columnName, index));
		} else {
			extendIndexInformation(indexInformation, columnName, index);
		}
	}

	private static boolean hasIndexDefinition(final Iterable<IndexInformation> indexInformation, final Index index) throws IndexException {
		for (final IndexInformation information : indexInformation) {
			final String indexName = index.name();
			if (information.getIndexName().equals(indexName)) {
				final IndexType indexType = index.type();
				if (information.getType() == indexType) {
					return true;
				} else {
					throw new IndexException(String.format("Index with name %s was allready defined with another index type. Actual type: %s", indexName, indexType));
				}
			}
		}
		return false;
	}

	private static void extendIndexInformation(final Iterable<IndexInformation> indexInformation, final String columnName, final Index index) {
		for (final IndexInformation information : indexInformation) {
			if (information.getIndexName().equals(index.name())) {
				information.addNewIndexColumn(columnName, index.position(), index.expr());
			}
		}
	}

	private static IndexInformation createIndexInformation(final String columnName, final Index index) {
		final IndexInformation indexInformation = new IndexInformation(index.name(), index.type());
		indexInformation.addNewIndexColumn(columnName, index.position(), index.expr());
		return indexInformation;
	}
}
