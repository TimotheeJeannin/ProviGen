package com.tjeannin.provigen.test;

import java.util.Arrays;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractThree;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractOne;
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
	}

	public void testInsert() {
		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());
	}

	public void testAutoIncrement() {

		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());

		contentResolver.delete(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(3)), "", null);

		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());

		Cursor cursor = contentResolver.query(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(4)), null, "", null, "");
		assertEquals(1, cursor.getCount());

		cursor = contentResolver.query(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(3)), null, "", null, "");
		assertEquals(0, cursor.getCount());
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
		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionTwo());
		Cursor cursor = contentResolver.query(ContractTwo.CONTENT_URI, null, "", null, "");

		assertEquals(4, cursor.getColumnCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne.MY_INT));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_REAL));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_STRING));
	}

	private void validateSimpleContractVersionOne() {
		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");

		assertEquals(2, cursor.getColumnCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne.MY_INT));
		assertFalse(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_REAL));
		assertFalse(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_STRING));
	}

	public void testHandlesBasicMultipleTables() {

		contentResolver.insert(ContractThree.CONTENT_URI, getDefaultContentValuesAnother());

		Cursor cursor = contentResolver.query(ContractThree.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());

		cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(0, cursor.getCount());
	}

	public void testAddingAnotherTableLater() throws InvalidContractException {

		getProvider().setContractClasses(new Class[] { ContractOne.class });

		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());

		getProvider().setContractClasses(new Class[] { ContractThree.class, ContractOne.class });

		contentResolver.insert(ContractThree.CONTENT_URI, getDefaultContentValuesVersionOne());
		cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());

		contentResolver.insert(ContractOne.CONTENT_URI, getDefaultContentValuesVersionOne());
		cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(2, cursor.getCount());
	}

	private ContentValues getDefaultContentValuesAnother() {
		ContentValues contentValues = new ContentValues(1);
		contentValues.put(ContractThree.ANOTHER_INT, 48);
		return contentValues;
	}

	private ContentValues getDefaultContentValuesVersionOne() {
		ContentValues contentValues = new ContentValues(4);
		contentValues.put(ContractOne.MY_INT, 1);
		return contentValues;
	}

	private ContentValues getDefaultContentValuesVersionTwo() {
		ContentValues contentValues = getDefaultContentValuesVersionOne();
		contentValues.put(ContractTwo.MY_STRING, "ok");
		contentValues.put(ContractTwo.MY_REAL, 1 / 3);
		return contentValues;
	}

}
