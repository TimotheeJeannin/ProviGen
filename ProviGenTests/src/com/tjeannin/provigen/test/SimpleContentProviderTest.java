package com.tjeannin.provigen.test;

import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.test.SimpleContentProvider.SimpleContract;

public class SimpleContentProviderTest extends ProviderTestCase2<AlarmContentProvider> {

	private MockContentResolver contentResolver;

	public SimpleContentProviderTest() {
		super(AlarmContentProvider.class, "com.test.simple");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();
	}

	public void testProviderIsEmpty() {

		Cursor cursor = contentResolver.query(SimpleContract.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

	public void testInsert() {
		contentResolver.insert(SimpleContract.CONTENT_URI, null);
		Cursor cursor = contentResolver.query(SimpleContract.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 1);
	}

	public void testAutoIncrement() {

		contentResolver.insert(SimpleContract.CONTENT_URI, null);
		contentResolver.insert(SimpleContract.CONTENT_URI, null);
		contentResolver.insert(SimpleContract.CONTENT_URI, null);

		contentResolver.delete(Uri.withAppendedPath(SimpleContract.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(SimpleContract.CONTENT_URI, null);

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(SimpleContract.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertTrue(cursor.getCount() == 1);

		cursor = contentResolver.query(Uri.withAppendedPath(SimpleContract.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

}
