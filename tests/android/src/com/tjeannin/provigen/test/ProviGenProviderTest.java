package com.tjeannin.provigen.test;

import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.Alarm;
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
		Cursor cursor = contentResolver.query(Alarm.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

	public void testInsert() {
		contentResolver.insert(Alarm.CONTENT_URI, Alarm.getDefaultContentValues());
		Cursor cursor = contentResolver.query(Alarm.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 1);
	}

	public void testAutoIncrement() {

		contentResolver.insert(Alarm.CONTENT_URI, Alarm.getDefaultContentValues());
		contentResolver.insert(Alarm.CONTENT_URI, Alarm.getDefaultContentValues());
		contentResolver.insert(Alarm.CONTENT_URI, Alarm.getDefaultContentValues());

		contentResolver.delete(Uri.withAppendedPath(Alarm.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(Alarm.CONTENT_URI, Alarm.getDefaultContentValues());

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(Alarm.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertTrue(cursor.getCount() == 1);

		cursor = contentResolver.query(Uri.withAppendedPath(Alarm.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

}
