package com.tjeannin.provigen.test.index;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.index.IndexProvider.IndexContract;

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

		final List<String> indexNames = loadIndexForTable(IndexContract.CONTENT_URI.getLastPathSegment());
		assertEquals(5, indexNames.size());
		assertTrue(indexNames.contains("provigen_index_1"));
		assertTrue(indexNames.contains("provigen_index_2"));
		assertTrue(indexNames.contains("INDEX_3"));
		assertTrue(indexNames.contains("INDEX_4"));
		assertTrue(indexNames.contains("INDEX_5"));
	}

	private List<String> loadIndexForTable(final String tableName) {
		final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
		final Cursor cursor = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE tbl_name = ? and type = ? and name not like 'sqlite_autoindex_%'", new String[] { tableName, "index" });
		assertNotNull(cursor);
		final List<String> result = new ArrayList<String>(cursor.getCount());
		cursor.moveToFirst();
		do {
			result.add(cursor.getString(cursor.getColumnIndex("name")));
		} while (cursor.moveToNext());
		return result;
	}
}
