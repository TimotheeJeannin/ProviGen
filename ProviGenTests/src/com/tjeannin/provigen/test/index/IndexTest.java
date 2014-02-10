package com.tjeannin.provigen.test.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.exceptions.InvalidContractException;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.index.IndexProvider.IndexContract;
import com.tjeannin.provigen.utils.DataBaseHelper;
import com.tjeannin.provigen.utils.IndexColumn;
import com.tjeannin.provigen.utils.IndexInformation;

public class IndexTest extends ExtendedProviderTestCase<IndexProvider> {

	private MockContentResolver contentResolver;

	public IndexTest() {
		super(IndexProvider.class, "com.test.simple");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();
	}

	public void testIndexCreation() {
		contentResolver.insert(IndexContract.CONTENT_URI, getContentValues(IndexContract.class));

		final List<String> indexNames = loadIndexForTable(IndexContract.CONTENT_URI);
		assertEquals(5, indexNames.size());
		assertTrue(indexNames.contains("provigen_index_1"));
		assertTrue(indexNames.contains("provigen_index_2"));
		assertTrue(indexNames.contains("INDEX_3"));
		assertTrue(indexNames.contains("INDEX_4"));
		assertTrue(indexNames.contains("INDEX_5"));
	}

	public void testUniqueAndIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.UniqueAndUniqueIndexContract.class });
		contentResolver.insert(IndexProvider.UniqueAndUniqueIndexContract.CONTENT_URI, getContentValues(IndexProvider.UniqueAndUniqueIndexContract.class));

		final List<String> indexNames = loadIndexForTable(IndexProvider.UniqueAndUniqueIndexContract.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("provigen_index_1"));
	}

	public void testPartialIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.PartialIndexContract.class });
		contentResolver.insert(IndexProvider.PartialIndexContract.CONTENT_URI, getContentValues(IndexProvider.PartialIndexContract.class));

		final List<String> indexNames = loadIndexForTable(IndexProvider.PartialIndexContract.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("provigen_index_1"));
	}

	public void testWeightedIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.WeightedIndexContract.class });
		contentResolver.insert(IndexProvider.WeightedIndexContract.CONTENT_URI, getContentValues(IndexProvider.WeightedIndexContract.class));

		final List<String> indexNames = loadIndexForTable(IndexProvider.WeightedIndexContract.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("INDEX_8"));
		final List<String> columnNames = loadIndexInformation("INDEX_8");
		assertEquals(2, columnNames.size());
		assertEquals(IndexProvider.WeightedIndexContract.COMBIND_INDEX_2, columnNames.get(0));
		assertEquals(IndexProvider.WeightedIndexContract.COMBIND_INDEX_1, columnNames.get(1));
	}

	private List<String> loadIndexInformation(final String indexName) {
		final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
		final List<IndexColumn> information = DataBaseHelper.getIndexInformation(sqLiteDatabase, indexName);
		final List<String> result = new ArrayList<String>(information.size());
		for (final IndexColumn column : information) {
			result.add(column.getName());
		}
		return Collections.unmodifiableList(result);
	}

	private List<String> loadIndexForTable(final Uri uri) {
		final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
		final List<IndexInformation> information = DataBaseHelper.getIndexInformationForTable(sqLiteDatabase, uri.getLastPathSegment());
		final List<String> result = new ArrayList<String>(information.size());
		for (final IndexInformation indexInformation : information) {
			final String indexName = indexInformation.getIndexName();
			if (!indexName.startsWith("sqlite_autoindex_")) {
				result.add(indexName);
			}
		}
		return result;
	}
}
