package com.tjeannin.provigen.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.test.SimpleContentProvider.SimpleContract;

public class SimpleContentProviderTest extends
		ProviderTestCase2<SimpleContentProvider> {

	private MockContentResolver contentResolver;

	public SimpleContentProviderTest() {
		super(SimpleContentProvider.class, "com.test.simple");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();
	}

	public void testProviderIsEmpty() {

		Cursor cursor = contentResolver.query(SimpleContract.CONTENT_URI, null,
				"", null, "");
		assertEquals(0, cursor.getCount());
	}

	public void testInsert() {
		contentResolver.insert(SimpleContract.CONTENT_URI, getDefaultContentValues());
		Cursor cursor = contentResolver.query(SimpleContract.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());
	}

	public void testAutoIncrement() {

		contentResolver.insert(SimpleContract.CONTENT_URI, getDefaultContentValues());
		contentResolver.insert(SimpleContract.CONTENT_URI, getDefaultContentValues());
		contentResolver.insert(SimpleContract.CONTENT_URI, getDefaultContentValues());

		contentResolver.delete(Uri.withAppendedPath(SimpleContract.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(SimpleContract.CONTENT_URI, getDefaultContentValues());

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(SimpleContract.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertEquals(1, cursor.getCount());

		cursor = contentResolver.query(Uri.withAppendedPath(SimpleContract.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertEquals(0, cursor.getCount());
	}

	private ContentValues getDefaultContentValues() {
		ContentValues contentValues = new ContentValues(4);
		contentValues.put(SimpleContract.COLUMN_INT, 1);
		contentValues.put(SimpleContract.COLUMN_STRING, "ok");
		contentValues.put(SimpleContract.COLUMN_REAL, 1 / 3);
		return contentValues;
	}

}
