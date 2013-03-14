package com.tjeannin.provigen.test;

import java.util.Arrays;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractOne;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractThree;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractTwo;

public class SimpleContentProviderTest extends ProviderTestCase2<SimpleContentProvider> {

	private MockContentResolver contentResolver;

	public SimpleContentProviderTest() {
		super(SimpleContentProvider.class, "com.test.simple");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();
	}

	@Override
	protected void tearDown() throws Exception {
		getContext().deleteDatabase("ProviGenDatabase");
		super.tearDown();
	}

	public void testProviderIsEmpty() {

		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null,
				"", null, "");
		assertEquals(0, cursor.getCount());
		cursor.close();
	}

	public void testInsert() {
		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());
		cursor.close();
	}

	public void testAutoIncrement() {

		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));
		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));
		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));

		contentResolver.delete(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertEquals(1, cursor.getCount());
		cursor.close();

		cursor = contentResolver.query(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertEquals(0, cursor.getCount());
		cursor.close();
	}

	public void testUpgradeFromDatabaseVersion1to2() throws InvalidContractException {

		getProvider().setContractClasses(new Class[] { ContractOne.class });
		validateSimpleContractVersionOne();
		getProvider().setContractClasses(new Class[] { ContractTwo.class });
		validateSimpleContractVersionTwo();
	}

	public void testUpgradingExistingContractWithMultipleTables() throws InvalidContractException {

		getProvider().setContractClasses(new Class[] { ContractThree.class, ContractOne.class });
		validateSimpleContractVersionOne();
		getProvider().setContractClasses(new Class[] { ContractTwo.class, ContractThree.class });
		validateSimpleContractVersionTwo();
	}

	private void validateSimpleContractVersionTwo() {
		contentResolver.insert(ContractTwo.CONTENT_URI, getValues(2));
		Cursor cursor = contentResolver.query(ContractTwo.CONTENT_URI, null, "", null, "");

		assertEquals(4, cursor.getColumnCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne.MY_INT));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_REAL));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_STRING));
		cursor.close();
	}

	private void validateSimpleContractVersionOne() {
		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");

		assertEquals(2, cursor.getColumnCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne.MY_INT));
		assertFalse(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_REAL));
		assertFalse(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_STRING));
		cursor.close();
	}

	public void testMultipleTablesInsert() {

		// Check there is no data.
		assertEquals(0, getCount(ContractOne.CONTENT_URI));
		assertEquals(0, getCount(ContractThree.CONTENT_URI));

		contentResolver.insert(ContractThree.CONTENT_URI, getValues(3));

		assertEquals(0, getCount(ContractOne.CONTENT_URI));
		assertEquals(1, getCount(ContractThree.CONTENT_URI));

		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));

		assertEquals(1, getCount(ContractOne.CONTENT_URI));
		assertEquals(1, getCount(ContractThree.CONTENT_URI));
	}

	public void testAddingAnotherTableLater() throws InvalidContractException {

		getProvider().setContractClasses(new Class[] { ContractOne.class });

		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));
		assertEquals(1, getCount(ContractOne.CONTENT_URI));

		getProvider().setContractClasses(new Class[] { ContractThree.class, ContractOne.class });
		assertEquals(0, getCount(ContractThree.CONTENT_URI));
		assertEquals(1, getCount(ContractOne.CONTENT_URI));

		contentResolver.insert(ContractThree.CONTENT_URI, getValues(3));
		assertEquals(1, getCount(ContractThree.CONTENT_URI));
		assertEquals(1, getCount(ContractOne.CONTENT_URI));

		contentResolver.insert(ContractOne.CONTENT_URI, getValues(1));
		assertEquals(1, getCount(ContractThree.CONTENT_URI));
		assertEquals(2, getCount(ContractOne.CONTENT_URI));
	}

	public void testGetMimeType() {
		String mimeType = getProvider().getType(ContractOne.CONTENT_URI);
		assertEquals("vnd.android.cursor.dir/vdn.table_name_simple", mimeType);
	}

	private int getCount(Uri uri) {
		Cursor cursor = contentResolver.query(uri, null, "", null, "");
		int count = cursor.getCount();
		cursor.close();
		return count;
	}

	private ContentValues getValues(int contractNumber) {
		ContentValues contentValues = new ContentValues();
		switch (contractNumber) {
		case 2:
			contentValues.put(ContractTwo.MY_STRING, "ok");
			contentValues.put(ContractTwo.MY_REAL, 1 / 3);
		case 1:
			contentValues.put(ContractOne.MY_INT, 1);
			break;
		case 3:
			contentValues = new ContentValues(1);
			contentValues.put(ContractThree.ANOTHER_INT, 48);
			break;
		}
		return contentValues;
	}
}
