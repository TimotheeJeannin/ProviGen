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
import com.tjeannin.provigen.utils.IndexInformation;
import com.tjeannin.provigen.utils.IndexUtils;

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

		final List<String> indexNames = loadIndexNames(IndexContract.CONTENT_URI);
		assertEquals(5, indexNames.size());
		assertTrue(indexNames.contains("INDEX_1"));
		assertTrue(indexNames.contains("INDEX_2"));
		assertTrue(indexNames.contains("INDEX_3"));
		assertTrue(indexNames.contains("INDEX_4"));
		assertTrue(indexNames.contains("INDEX_5"));
	}

	public void testUniqueAndIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.UniqueAndUniqueIndexContract.class });
		contentResolver.insert(IndexProvider.UniqueAndUniqueIndexContract.CONTENT_URI, getContentValues(IndexProvider.UniqueAndUniqueIndexContract.class));

		final List<String> indexNames = loadIndexNames(IndexProvider.UniqueAndUniqueIndexContract.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("INDEX_1"));
	}

	public void testPartialIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.PartialIndexContract.class });
		contentResolver.insert(IndexProvider.PartialIndexContract.CONTENT_URI, getContentValues(IndexProvider.PartialIndexContract.class));

		final List<String> indexNames = loadIndexNames(IndexProvider.PartialIndexContract.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("INDEX_1"));
	}

	public void testWeightedIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.WeightedIndexContract.class });
		contentResolver.insert(IndexProvider.WeightedIndexContract.CONTENT_URI, getContentValues(IndexProvider.WeightedIndexContract.class));

		final List<String> indexNames = loadIndexNames(IndexProvider.WeightedIndexContract.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("INDEX_8"));
		final List<String> columnNames = loadIndexColumns(IndexProvider.WeightedIndexContract.CONTENT_URI, "INDEX_8");
		assertEquals(2, columnNames.size());
		assertEquals(IndexProvider.WeightedIndexContract.COMBIND_INDEX_2, columnNames.get(0));
		assertEquals(IndexProvider.WeightedIndexContract.COMBIND_INDEX_1, columnNames.get(1));
	}

	public void testUpdateIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.UpdateIndexContract1.class });
		contentResolver.insert(IndexProvider.UpdateIndexContract1.CONTENT_URI, getContentValues(IndexProvider.UpdateIndexContract1.class));

		final List<String> indexNames = loadIndexNames(IndexProvider.UpdateIndexContract1.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("INDEX_9"));
		final List<String> columnNames = loadIndexColumns(IndexProvider.UpdateIndexContract1.CONTENT_URI, "INDEX_9");
		assertEquals(1, columnNames.size());
		assertEquals(IndexProvider.UpdateIndexContract1.FIELD_1, columnNames.get(0));

		getProvider().setContractClasses(new Class[] { IndexProvider.UpdateIndexContract2.class });
		contentResolver.insert(IndexProvider.UpdateIndexContract2.CONTENT_URI, getContentValues(IndexProvider.UpdateIndexContract2.class));
		final List<String> newIndexNames = loadIndexNames(IndexProvider.UpdateIndexContract2.CONTENT_URI);
		assertEquals(2, newIndexNames.size());
		assertTrue(newIndexNames.contains("INDEX_9"));
		assertTrue(newIndexNames.contains("INDEX_10"));
		final List<String> newColumnNamesIdx9 = loadIndexColumns(IndexProvider.UpdateIndexContract2.CONTENT_URI, "INDEX_9");
		assertEquals(1, newColumnNamesIdx9.size());
		assertEquals(IndexProvider.UpdateIndexContract2.FIELD_1, newColumnNamesIdx9.get(0));
		final List<String> newColumnNamesIdx10 = loadIndexColumns(IndexProvider.UpdateIndexContract2.CONTENT_URI, "INDEX_10");
		assertEquals(1, newColumnNamesIdx10.size());
		assertEquals(IndexProvider.UpdateIndexContract2.FIELD_2, newColumnNamesIdx10.get(0));
	}

	public void testDowngradeIndex() throws InvalidContractException {
		getProvider().setContractClasses(new Class[] { IndexProvider.UpdateIndexContract2.class });
		contentResolver.insert(IndexProvider.UpdateIndexContract2.CONTENT_URI, getContentValues(IndexProvider.UpdateIndexContract2.class));
		final List<String> newIndexNames = loadIndexNames(IndexProvider.UpdateIndexContract2.CONTENT_URI);
		assertEquals(2, newIndexNames.size());
		assertTrue(newIndexNames.contains("INDEX_9"));
		assertTrue(newIndexNames.contains("INDEX_10"));
		final List<String> newColumnNamesIdx9 = loadIndexColumns(IndexProvider.UpdateIndexContract2.CONTENT_URI, "INDEX_9");
		assertEquals(1, newColumnNamesIdx9.size());
		assertEquals(IndexProvider.UpdateIndexContract2.FIELD_1, newColumnNamesIdx9.get(0));
		final List<String> newColumnNamesIdx10 = loadIndexColumns(IndexProvider.UpdateIndexContract2.CONTENT_URI, "INDEX_10");
		assertEquals(1, newColumnNamesIdx10.size());
		assertEquals(IndexProvider.UpdateIndexContract2.FIELD_2, newColumnNamesIdx10.get(0));

		getProvider().setContractClasses(new Class[] { IndexProvider.UpdateIndexContract3.class });
		contentResolver.insert(IndexProvider.UpdateIndexContract3.CONTENT_URI, getContentValues(IndexProvider.UpdateIndexContract3.class));

		final List<String> indexNames = loadIndexNames(IndexProvider.UpdateIndexContract3.CONTENT_URI);
		assertEquals(1, indexNames.size());
		assertTrue(indexNames.contains("INDEX_9"));
		final List<String> columnNames = loadIndexColumns(IndexProvider.UpdateIndexContract3.CONTENT_URI, "INDEX_9");
		assertEquals(1, columnNames.size());
		assertEquals(IndexProvider.UpdateIndexContract3.FIELD_1, columnNames.get(0));
	}

	private List<String> loadIndexColumns(final Uri uri, final String indexName) {
		final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
		final List<IndexInformation> information = IndexUtils.getIndexInformationForTable(sqLiteDatabase, uri.getLastPathSegment());
		for (final IndexInformation indexInformation : information) {
			if( indexInformation.getIndexName().equals(indexName)) {
				return indexInformation.getIndexColumnNames();
			}
		}
		return Collections.emptyList();
	}

	private List<String> loadIndexNames(final Uri uri) {
		final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
		final List<IndexInformation> information = IndexUtils.getIndexInformationForTable(sqLiteDatabase, uri.getLastPathSegment());
		final List<String> result = new ArrayList<String>(information.size());
		for (final IndexInformation indexInformation : information) {
			result.add(indexInformation.getIndexName());
		}
		return result;
	}
}
