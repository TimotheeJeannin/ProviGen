package com.tjeannin.provigen.test;

import java.util.Arrays;

import android.database.Cursor;
import android.net.Uri;
import android.test.mock.MockContentResolver;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractOne;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractThree;
import com.tjeannin.provigen.test.SimpleContentProvider.ContractTwo;

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

		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null,
				"", null, "");
		assertEquals(0, cursor.getCount());
		cursor.close();
	}

	public void testInsert() {
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
		assertEquals(1, cursor.getCount());
		cursor.close();
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
		contentResolver.insert(ContractTwo.CONTENT_URI, getContentValues(ContractTwo.class));
		Cursor cursor = contentResolver.query(ContractTwo.CONTENT_URI, null, "", null, "");

		assertEquals(4, cursor.getColumnCount());
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne._ID));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractOne.MY_INT));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_REAL));
		assertTrue(Arrays.asList(cursor.getColumnNames()).contains(ContractTwo.MY_STRING));
		cursor.close();
	}

	private void validateSimpleContractVersionOne() {
		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
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
		assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
		assertEquals(0, getRowCount(ContractThree.CONTENT_URI));

		contentResolver.insert(ContractThree.CONTENT_URI, getContentValues(ContractThree.class));

		assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
		assertEquals(1, getRowCount(ContractThree.CONTENT_URI));

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

		assertEquals(1, getRowCount(ContractOne.CONTENT_URI));
		assertEquals(1, getRowCount(ContractThree.CONTENT_URI));
	}

	public void testAddingAnotherTableLater() throws InvalidContractException {

		getProvider().setContractClasses(new Class[] { ContractOne.class });

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

		getProvider().setContractClasses(new Class[] { ContractThree.class, ContractOne.class });
		assertEquals(0, getRowCount(ContractThree.CONTENT_URI));
		assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

		contentResolver.insert(ContractThree.CONTENT_URI, getContentValues(ContractThree.class));
		assertEquals(1, getRowCount(ContractThree.CONTENT_URI));
		assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

		contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
		assertEquals(1, getRowCount(ContractThree.CONTENT_URI));
		assertEquals(2, getRowCount(ContractOne.CONTENT_URI));
	}

	public void testGetMimeType() {
		String mimeType = getProvider().getType(ContractOne.CONTENT_URI);
		assertEquals("vnd.android.cursor.dir/vdn.table_name_simple", mimeType);
	}
}
