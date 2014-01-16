package com.tjeannin.provigen.test.basis;

import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.basis.SimpleContentProvider.ContractOne;
import com.tjeannin.provigen.test.basis.SimpleContentProvider.ContractTwo;

public class SimpleContentProviderTest extends ExtendedProviderTestCase<SimpleContentProvider> {

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
		assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
	}

	public void testInsertQueryUpdateDelete() {

		// Insert
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

		// Update
		ContentValues contentValues = new ContentValues(2);
		contentValues.put(ContractOne.MY_INT, 15);
		contentResolver.update(ContractOne.CONTENT_URI, contentValues, "", null);

		// Query
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		cursor.moveToFirst();
		assertEquals(cursor.getInt(cursor.getColumnIndex(ContractOne.MY_INT)), 15);
		cursor.close();

		// Delete
		contentResolver.delete(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(1)), "", null);
		assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
	}

	public void testBulkInsert() {

		ContentValues[] contentValuesArray = new ContentValues[10];
		for (int i = 0; i < 10; i++) {
			contentValuesArray[i] = getContentValues(ContractOne.class);
		}

		contentResolver.bulkInsert(ContractOne.CONTENT_URI, contentValuesArray);
		assertEquals(10, getRowCount(ContractOne.CONTENT_URI));
	}

	public void testUpdateMultiple() {
		ContentValues contentValues = new ContentValues();
		contentValues.put(ContractOne.MY_INT, 5);
		contentResolver.insert(ContractOne.CONTENT_URI, contentValues);
		contentValues.put(ContractOne.MY_INT, 7);
		contentResolver.insert(ContractOne.CONTENT_URI, contentValues);

		contentValues.put(ContractOne.MY_INT, 9);
		contentResolver.update(ContractOne.CONTENT_URI, contentValues, "", null);

		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(2, getRowCount(ContractOne.CONTENT_URI));
		while (cursor.moveToNext()) {
			assertEquals(9, cursor.getInt(cursor.getColumnIndex(ContractOne.MY_INT)));
		}
	}

	public void testDeleteMultiple() {

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

		assertEquals(3, getRowCount(ContractOne.CONTENT_URI));
		contentResolver.delete(ContractOne.CONTENT_URI, "", null);
		assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
	}

	public void testAutoIncrement() {

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

		contentResolver.delete(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

		assertEquals(1, getRowCount(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(4))));
		assertEquals(0, getRowCount(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(3))));
	}

	public void testUpgradeFromContractOneToTwo() throws InvalidContractException {

		getProvider().setContractClasses(new Class[] { ContractOne.class });

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

		// Check database fits ContractOne.
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(2, cursor.getColumnCount());
		List<String> columnNameList = Arrays.asList(cursor.getColumnNames());
		assertTrue(columnNameList.contains(ContractOne._ID));
		assertTrue(columnNameList.contains(ContractOne.MY_INT));
		assertFalse(columnNameList.contains(ContractTwo.MY_REAL));
		assertFalse(columnNameList.contains(ContractTwo.MY_STRING));
		cursor.close();

		getProvider().setContractClasses(new Class[] { ContractTwo.class });

		// Check database fits ContractTwo.
		cursor = contentResolver.query(ContractTwo.CONTENT_URI, null, "", null, "");
		assertEquals(4, cursor.getColumnCount());
		columnNameList = Arrays.asList(cursor.getColumnNames());
		assertTrue(columnNameList.contains(ContractTwo._ID));
		assertTrue(columnNameList.contains(ContractTwo.MY_INT));
		assertTrue(columnNameList.contains(ContractTwo.MY_REAL));
		assertTrue(columnNameList.contains(ContractTwo.MY_STRING));
		cursor.close();
	}

	public void testGetMimeType() {
		String mimeType = getProvider().getType(ContractOne.CONTENT_URI);
		assertEquals("vnd.android.cursor.dir/vdn.table_name_simple", mimeType);
	}
}
