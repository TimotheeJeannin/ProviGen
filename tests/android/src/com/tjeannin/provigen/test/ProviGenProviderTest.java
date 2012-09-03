package com.tjeannin.provigen.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.test.AlarmContentProvider.AlarmContract;

public class ProviGenProviderTest extends ProviderTestCase2<AlarmContentProvider> {

	private MockContentResolver contentResolver;

	public ProviGenProviderTest() {
		super(AlarmContentProvider.class, "com.tjeannin.provigen");
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
		contentResolver.insert(AlarmContract.CONTENT_URI, getDefaultContentValues());
		Cursor cursor = contentResolver.query(AlarmContract.CONTENT_URI, null, "", null, "");
		assertTrue(cursor.getCount() == 1);
	}

	public void testAutoIncrement() {

		contentResolver.insert(AlarmContract.CONTENT_URI, getDefaultContentValues());
		contentResolver.insert(AlarmContract.CONTENT_URI, getDefaultContentValues());
		contentResolver.insert(AlarmContract.CONTENT_URI, getDefaultContentValues());

		contentResolver.delete(Uri.withAppendedPath(AlarmContract.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(AlarmContract.CONTENT_URI, getDefaultContentValues());

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(AlarmContract.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertTrue(cursor.getCount() == 1);

		cursor = contentResolver.query(Uri.withAppendedPath(AlarmContract.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertTrue(cursor.getCount() == 0);
	}

	private ContentValues getDefaultContentValues() {

		ContentValues contentValues = new ContentValues();
		contentValues.put(AlarmContract.COLUMN_HOUR, 8);
		contentValues.put(AlarmContract.COLUMN_MINUTE, 30);
		contentValues.put(AlarmContract.COLUMN_SNOOZETIME, 0);
		contentValues.put(AlarmContract.COLUMN_RINGTIME, 0);
		contentValues.put(AlarmContract.COLUMN_ACTIVE, false);
		contentValues.put(AlarmContract.COLUMN_SNOOZED, false);
		contentValues.put(AlarmContract.COLUMN_NAME, "");
		contentValues.put(AlarmContract.COLUMN_ACTIVE_DAYS, "1111111");
		return contentValues;
	}

}
