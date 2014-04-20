package com.tjeannin.provigen.test.basis;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.basis.SimpleContentProvider.ContractOne;
import com.tjeannin.provigen.test.basis.SimpleContentProvider.ContractTwo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        final ContentValues contentValues = new ContentValues(2);
        contentValues.put(ContractOne.MY_INT, 15);
        contentResolver.update(ContractOne.CONTENT_URI, contentValues, "", null);

        // Query
        final Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
        cursor.moveToFirst();
        assertEquals(cursor.getInt(cursor.getColumnIndex(ContractOne.MY_INT)), 15);
        cursor.close();

        // Delete
        contentResolver.delete(Uri.withAppendedPath(ContractOne.CONTENT_URI, String.valueOf(1)), "", null);
        assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
    }

    public void testBulkInsert() {

        final ContentValues[] contentValuesArray = new ContentValues[10];
        for (int i = 0; i < 10; i++) {
            contentValuesArray[i] = getContentValues(ContractOne.class);
        }

        contentResolver.bulkInsert(ContractOne.CONTENT_URI, contentValuesArray);
        assertEquals(10, getRowCount(ContractOne.CONTENT_URI));
    }

    public void testUpdateMultiple() {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ContractOne.MY_INT, 5);
        contentResolver.insert(ContractOne.CONTENT_URI, contentValues);
        contentValues.put(ContractOne.MY_INT, 7);
        contentResolver.insert(ContractOne.CONTENT_URI, contentValues);

        contentValues.put(ContractOne.MY_INT, 9);
        contentResolver.update(ContractOne.CONTENT_URI, contentValues, "", null);

        final Cursor cursor = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
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

    public void testUpgradeFromContractOneToTwo() {

        contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

        // Check database fits ContractOne.
        final Cursor cursorOne = contentResolver.query(ContractOne.CONTENT_URI, null, "", null, "");
        assertEquals(2, cursorOne.getColumnCount());
        final List<String> columnNameListContractOne = Arrays.asList(cursorOne.getColumnNames());
        assertTrue(columnNameListContractOne.contains(ContractOne._ID));
        assertTrue(columnNameListContractOne.contains(ContractOne.MY_INT));
        assertFalse(columnNameListContractOne.contains(ContractTwo.MY_REAL));
        assertFalse(columnNameListContractOne.contains(ContractTwo.MY_STRING));
        cursorOne.close();

        final List<String> columnsContractOne = getTableFields(ContractOne.CONTENT_URI);
        assertEquals(2, columnsContractOne.size());
        assertTrue(columnsContractOne.contains(ContractOne._ID));
        assertTrue(columnsContractOne.contains(ContractOne.MY_INT));
        assertFalse(columnsContractOne.contains(ContractTwo.MY_REAL));
        assertFalse(columnsContractOne.contains(ContractTwo.MY_STRING));

        resetContractClasses(new Class[]{ContractTwo.class});

        // Check database fits ContractTwo.
        final Cursor cursorTwo = contentResolver.query(ContractTwo.CONTENT_URI, null, "", null, "");
        assertEquals(4, cursorTwo.getColumnCount());
        final List<String> columnNameListContractTwo = Arrays.asList(cursorTwo.getColumnNames());
        assertTrue(columnNameListContractTwo.contains(ContractTwo._ID));
        assertTrue(columnNameListContractTwo.contains(ContractTwo.MY_INT));
        assertTrue(columnNameListContractTwo.contains(ContractTwo.MY_REAL));
        assertTrue(columnNameListContractTwo.contains(ContractTwo.MY_STRING));
        cursorTwo.close();

        final List<String> columnsContractTwo = getTableFields(ContractTwo.CONTENT_URI);
        assertEquals(4, columnsContractTwo.size());
        assertTrue(columnsContractTwo.contains(ContractTwo._ID));
        assertTrue(columnsContractTwo.contains(ContractTwo.MY_INT));
        assertTrue(columnsContractTwo.contains(ContractTwo.MY_REAL));
        assertTrue(columnsContractTwo.contains(ContractTwo.MY_STRING));

        assertEquals(ContractOne.CONTENT_URI.getLastPathSegment(), ContractTwo.CONTENT_URI.getLastPathSegment());
        assertTrue(columnsContractTwo.containsAll(columnsContractOne));
    }

    private List<String> getTableFields(final Uri contentUri) {
        final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
        final String tableName = contentUri.getLastPathSegment();
        final Cursor rawQuery = sqLiteDatabase.rawQuery("PRAGMA table_info(" + tableName + ')', null);
        final List<String> result = new ArrayList<String>(rawQuery.getCount());
        if (rawQuery.moveToFirst()) {
            do {
                result.add(rawQuery.getString(rawQuery.getColumnIndex("name")));
            } while (rawQuery.moveToNext());
        }
        rawQuery.close();
        return result;
    }

    public void testGetMimeType() {
        final String mimeType = getProvider().getType(ContractOne.CONTENT_URI);
        assertEquals("vnd.android.cursor.dir/vdn.table_name_simple", mimeType);
    }
}
