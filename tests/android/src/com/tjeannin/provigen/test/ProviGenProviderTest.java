package com.tjeannin.provigen.test;

import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.AlarmContract;
import com.tjeannin.provigen.ProviGenProvider;

public class ProviGenProviderTest extends ProviderTestCase2<ProviGenProvider> {

	private MockContentResolver contentResolver;

	public ProviGenProviderTest() {
		super(ProviGenProvider.class, "com.tjeannin.provigen");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();
	}

	public void testProviderIsEmpty() {
		Cursor cursor = contentResolver.query(AlarmContract.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

	public void testInsert() {
		contentResolver.insert(AlarmContract.CONTENT_URI, AlarmContract.getDefaultContentValues());
		Cursor cursor = contentResolver.query(AlarmContract.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 1);
	}

	public void testAutoIncrement() {

		contentResolver.insert(AlarmContract.CONTENT_URI, AlarmContract.getDefaultContentValues());
		contentResolver.insert(AlarmContract.CONTENT_URI, AlarmContract.getDefaultContentValues());
		contentResolver.insert(AlarmContract.CONTENT_URI, AlarmContract.getDefaultContentValues());

		contentResolver.delete(Uri.withAppendedPath(AlarmContract.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(AlarmContract.CONTENT_URI, AlarmContract.getDefaultContentValues());

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(AlarmContract.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertTrue(cursor.getCount() == 1);

		cursor = contentResolver.query(Uri.withAppendedPath(AlarmContract.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

}
