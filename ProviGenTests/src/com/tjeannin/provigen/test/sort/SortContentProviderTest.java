package com.tjeannin.provigen.test.sort;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.sort.SortContentProvider.ContractOne;
import com.tjeannin.provigen.test.sort.SortContentProvider.ContractTwo;

public class SortContentProviderTest extends ExtendedProviderTestCase<SortContentProvider> {

	private MockContentResolver contentResolver;

	public SortContentProviderTest() {
		super(SortContentProvider.class, "com.test.simple");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		contentResolver = getMockContentResolver();
	}

	public void testOrdering() {
		final ContentValues value1 = new ContentValues(1);
		final ContentValues value2 = new ContentValues(1);
		final ContentValues value3 = new ContentValues(1);

		value1.put(ContractOne.MY_INT, 4);
		value2.put(ContractOne.MY_INT, 2);
		value3.put(ContractOne.MY_INT, 8);

		contentResolver.insert(ContractOne.CONTENT_URI, value1);
		contentResolver.insert(ContractOne.CONTENT_URI, value2);
		contentResolver.insert(ContractOne.CONTENT_URI, value3);

		final Cursor query = contentResolver.query(ContractOne.CONTENT_URI, new String[] { ContractOne.MY_INT }, null, null, null);
		assertNotNull(query);
		assertEquals(3, query.getCount());
		query.moveToFirst();

		int last = Integer.MIN_VALUE;
		do {
			final int rowValue = query.getInt(query.getColumnIndex(ContractOne.MY_INT));
			assertTrue(String.format("%d < %d", last, rowValue), last < rowValue);
			last = rowValue;
		} while (query.moveToNext());

		query.close();
	}

	public void testMultipleOrdering() {
		final ContentValues value1 = new ContentValues(2);
		final ContentValues value2 = new ContentValues(2);
		final ContentValues value3 = new ContentValues(2);
		final ContentValues value4 = new ContentValues(2);
		final ContentValues value5 = new ContentValues(2);

		value1.put(ContractTwo.MY_INT, 4);
		value2.put(ContractTwo.MY_INT, 2);
		value3.put(ContractTwo.MY_INT, 4);
		value4.put(ContractTwo.MY_INT, 4);
		value5.put(ContractTwo.MY_INT, 8);

		value1.put(ContractTwo.MY_STRING, "c");
		value3.put(ContractTwo.MY_STRING, "a");
		value4.put(ContractTwo.MY_STRING, "b");

		contentResolver.insert(ContractTwo.CONTENT_URI, value1);
		contentResolver.insert(ContractTwo.CONTENT_URI, value2);
		contentResolver.insert(ContractTwo.CONTENT_URI, value3);
		contentResolver.insert(ContractTwo.CONTENT_URI, value4);
		contentResolver.insert(ContractTwo.CONTENT_URI, value5);

		final Cursor query = contentResolver.query(ContractTwo.CONTENT_URI, new String[] { ContractTwo.MY_INT, ContractTwo.MY_STRING }, null, null, null);
		assertNotNull(query);
		assertEquals(5, query.getCount());
		query.moveToFirst();

		final StringBuilder text = new StringBuilder("");
		do {
			final String value = query.getString(query.getColumnIndex(ContractTwo.MY_STRING));
			if (value != null) {
				text.append(value);
			}
		} while (query.moveToNext());

		assertEquals("abc", text.toString());

		query.moveToFirst();
		int last = Integer.MIN_VALUE;
		do {
			final int rowValue = query.getInt(query.getColumnIndex(ContractOne.MY_INT));
			assertTrue(String.format("%d <= %d", last, rowValue), last <= rowValue);
			last = rowValue;
		} while (query.moveToNext());

		query.close();
	}

	public void testOverridenSort() {
		final ContentValues value1 = new ContentValues(1);
		final ContentValues value2 = new ContentValues(1);
		final ContentValues value3 = new ContentValues(1);

		value1.put(ContractOne.MY_INT, 4);
		value2.put(ContractOne.MY_INT, 2);
		value3.put(ContractOne.MY_INT, 8);

		contentResolver.insert(ContractOne.CONTENT_URI, value1);
		contentResolver.insert(ContractOne.CONTENT_URI, value2);
		contentResolver.insert(ContractOne.CONTENT_URI, value3);

		final Cursor query = contentResolver.query(ContractOne.CONTENT_URI, new String[] { ContractOne.MY_INT }, null, null, ContractOne.MY_INT + " DESC");
		assertNotNull(query);
		assertEquals(3, query.getCount());
		query.moveToFirst();

		int last = Integer.MAX_VALUE;
		do {
			final int rowValue = query.getInt(query.getColumnIndex(ContractOne.MY_INT));
			assertTrue(String.format("%d > %d", last, rowValue), last > rowValue);
			last = rowValue;
		} while (query.moveToNext());

		query.close();
	}
}
