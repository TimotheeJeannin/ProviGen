package com.tjeannin.provigen.test.sort;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.sort.SortContentProvider.ContractOne;
import com.tjeannin.provigen.test.sort.SortContentProvider.ContractThree;
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

		final StringBuilder myStringSB = new StringBuilder("#");
		final StringBuilder myIntSB = new StringBuilder("#");
		do {
			final String myString = query.getString(query.getColumnIndex(ContractTwo.MY_STRING));
			final String myInt = query.getString(query.getColumnIndex(ContractTwo.MY_INT));
			if (myString != null) {
				myStringSB.append(myString);
			}
			if (myInt != null) {
				myIntSB.append(myInt);
			}
			myStringSB.append('#');
			myIntSB.append('#');
		} while (query.moveToNext());

		assertEquals("##a#b#c##", myStringSB.toString());
		assertEquals("#2#4#4#4#8#", myIntSB.toString());
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

	public void testEqualWeight() {
		final ContentValues value1 = new ContentValues(4);
		final ContentValues value2 = new ContentValues(4);
		final ContentValues value3 = new ContentValues(4);
		final ContentValues value4 = new ContentValues(4);
		final ContentValues value5 = new ContentValues(4);

		value1.put(ContractThree.MY_INT, 4);
		value2.put(ContractThree.MY_INT, 2);
		value3.put(ContractThree.MY_INT, 2);
		value4.put(ContractThree.MY_INT, 7);
		value5.put(ContractThree.MY_INT, 7);

		value1.put(ContractThree.MY_STRING, "1");
		value2.put(ContractThree.MY_STRING, "2");
		value3.put(ContractThree.MY_STRING, "8");
		value4.put(ContractThree.MY_STRING, "8");
		value5.put(ContractThree.MY_STRING, "8");

		value1.put(ContractThree.MY_REAL, 4.9d);
		value2.put(ContractThree.MY_REAL, 2.1d);
		value3.put(ContractThree.MY_REAL, 8.2d);
		value4.put(ContractThree.MY_REAL, 4.3d);
		value5.put(ContractThree.MY_REAL, 4.6d);

		value1.put(ContractThree.MY_BLOB, "BLOB 1".getBytes());
		value2.put(ContractThree.MY_BLOB, "BLOB 2".getBytes());
		value3.put(ContractThree.MY_BLOB, "BLOB 3".getBytes());
		value4.put(ContractThree.MY_BLOB, "BLOB 4".getBytes());
		value5.put(ContractThree.MY_BLOB, "BLOB 5".getBytes());

		contentResolver.insert(ContractThree.CONTENT_URI, value1);
		contentResolver.insert(ContractThree.CONTENT_URI, value2);
		contentResolver.insert(ContractThree.CONTENT_URI, value3);
		contentResolver.insert(ContractThree.CONTENT_URI, value4);
		contentResolver.insert(ContractThree.CONTENT_URI, value5);

		final Cursor query = contentResolver.query(ContractThree.CONTENT_URI, null, null, null, null);
		assertNotNull(query);
		assertEquals(5, query.getCount());
		query.moveToFirst();

		final StringBuilder myStringSB = new StringBuilder("#");
		final StringBuilder myIntSB = new StringBuilder("#");
		final StringBuilder myRealSB = new StringBuilder("#");
		do {
			final String myString = query.getString(query.getColumnIndex(ContractThree.MY_STRING));
			final String myInt = query.getString(query.getColumnIndex(ContractThree.MY_INT));
			final String myReal = query.getString(query.getColumnIndex(ContractThree.MY_REAL));
			if (myString != null) {
				myStringSB.append(myString);
			}
			if (myInt != null) {
				myIntSB.append(myInt);
			}
			if (myReal != null) {
				myRealSB.append(myReal);
			}
			myStringSB.append('#');
			myIntSB.append('#');
			myRealSB.append('#');
		} while (query.moveToNext());

		assertEquals("#2#2#4#7#7#", myIntSB.toString());
		assertEquals("#8#2#1#8#8#", myStringSB.toString());
		assertEquals("#8.2#2.1#4.9#4.6#4.3#", myRealSB.toString());
	}
}
