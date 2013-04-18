package com.tjeannin.provigen.test.basis;

import java.util.Arrays;
import java.util.List;

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
