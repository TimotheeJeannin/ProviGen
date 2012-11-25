package com.tjeannin.provigen.test;

import java.util.Arrays;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.SimpleContentProvider.SimpleContractVersionOne;
import com.tjeannin.provigen.test.SimpleContentProvider.SimpleContractVersionTwo;

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

		Cursor cursor = contentResolver.query(SimpleContractVersionOne.CONTENT_URI, null,
				"", null, "");
		assertEquals(0, cursor.getCount());
	}

	public void testInsert() {
		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		Cursor cursor = contentResolver.query(SimpleContractVersionOne.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());
	}

	public void testAutoIncrement() {

		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionOne());

		contentResolver.delete(Uri.withAppendedPath(SimpleContractVersionOne.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionOne());

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(SimpleContractVersionOne.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertEquals(1, cursor.getCount());

		cursor = contentResolver.query(Uri.withAppendedPath(SimpleContractVersionOne.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertEquals(0, cursor.getCount());
	}

	public void testUpgradeFromDatabaseVersion1to2() throws InvalidContractException {

		getContext().deleteDatabase("ProviGenDatabase");

		getProvider().setContractClass(SimpleContractVersionOne.class);

		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		Cursor cursor = contentResolver.query(SimpleContractVersionOne.CONTENT_URI, null, "", null, "");

		assertEquals(2, cursor.getColumnCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionOne.COLUMN_INT));
		assertFalse(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionTwo.COLUMN_REAL));
		assertFalse(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionTwo.COLUMN_STRING));

		getProvider().setContractClass(SimpleContractVersionTwo.class);

		contentResolver.insert(SimpleContractVersionOne.CONTENT_URI, getDefaultContentValuesVersionTwo());
		cursor = contentResolver.query(SimpleContractVersionTwo.CONTENT_URI, null, "", null, "");

		assertEquals(4, cursor.getColumnCount());
		assertEquals(2, cursor.getCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionOne.COLUMN_INT));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionTwo.COLUMN_REAL));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(SimpleContractVersionTwo.COLUMN_STRING));
	}

	private ContentValues getDefaultContentValuesVersionOne() {
		ContentValues contentValues = new ContentValues(4);
		contentValues.put(SimpleContractVersionOne.COLUMN_INT, 1);
		return contentValues;
	}

	private ContentValues getDefaultContentValuesVersionTwo() {
		ContentValues contentValues = getDefaultContentValuesVersionOne();
		contentValues.put(SimpleContractVersionTwo.COLUMN_STRING, "ok");
		contentValues.put(SimpleContractVersionTwo.COLUMN_REAL, 1 / 3);
		return contentValues;
	}

}
