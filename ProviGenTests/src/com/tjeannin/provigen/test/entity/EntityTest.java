package com.tjeannin.provigen.test.entity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.test.mock.MockCursor;
import com.tjeannin.provigen.test.entity.EntityContentProvider.EntityContract;

public class EntityTest extends AndroidTestCase {

    private ContentResolver contentResolver;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentResolver = getContext().getContentResolver();
        contentResolver.delete(EntityContract.CONTENT_URI, null, null);
    }

    public void testInsertDelete() {
        EntityClass entity = new EntityClass();
        assertEquals(0, getCount(EntityContract.CONTENT_URI));
        entity.insert(contentResolver);
        assertEquals(1, getCount(EntityContract.CONTENT_URI));
        entity.delete(contentResolver);
        assertEquals(0, getCount(EntityContract.CONTENT_URI));
    }

    private int getCount(Uri uri) {
        Cursor cursor = contentResolver.query(uri, null, "", null, "");
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void testEntityContentProviderWorks() {

        assertEquals(0, getCount(EntityContract.CONTENT_URI));

        ContentValues contentValues = new ContentValues();
        contentValues.put(EntityContract.MY_INT, 1456);
        contentValues.put(EntityContract.MY_STRING, "hfdzue");
        contentValues.put(EntityContract.MY_DOUBLE, 1456.45);
        contentValues.put(EntityContract.MY_BOOLEAN, false);
        contentValues.put(EntityContract.MY_URI, "www.google.com");

        Uri uri = contentResolver.insert(EntityContract.CONTENT_URI, contentValues);
        assertEquals(1, getCount(EntityContract.CONTENT_URI));
        contentResolver.delete(uri, null, null);
        assertEquals(0, getCount(EntityContract.CONTENT_URI));
    }

    public void testGetEntityContentValues() {

        EntityClass entity = new EntityClass();

        entity.setMyInt(123);
        entity.setMyBoolean(true);
        entity.setMyUri(Uri.parse("http://www.google.com"));
        entity.setMyString("dze");
        entity.setMyDouble(78.89);

        ContentValues contentValues = entity.getContentValues();

        assertEquals(Integer.valueOf(123), contentValues.getAsInteger(EntityContract.MY_INT));
        assertEquals(Boolean.TRUE, contentValues.getAsBoolean(EntityContract.MY_BOOLEAN));
        assertEquals("dze", contentValues.getAsString(EntityContract.MY_STRING));
        assertEquals("http://www.google.com", contentValues.getAsString(EntityContract.MY_URI));
        assertEquals(78.89, contentValues.getAsDouble(EntityContract.MY_DOUBLE));
    }

    private class EntityCursor extends MockCursor {

        @Override
        public double getDouble(int columnIndex) {
            return 8465.16;
        }

        @Override
        public int getInt(int columnIndex) {
            if (columnIndex == 3) {
                return 1;
            }
            return 888;
        }

        @Override
        public String getString(int columnIndex) {
            switch (columnIndex) {
                case 1:
                    return "testString";
                case 2:
                    return "www.google.fr";
            }
            return null;
        }

        @Override
        public int getColumnIndex(String columnName) {
            if (columnName.equals(EntityContract.MY_STRING)) {
                return 1;
            } else if (columnName.equals(EntityContract.MY_URI)) {
                return 2;
            } else if (columnName.equals(EntityContract.MY_BOOLEAN)) {
                return 3;
            } else {
                return 0;
            }
        }
    }

    public void testEntityConstructor() {

        EntityClass entity = new EntityClass(new EntityCursor());

        assertEquals(888, entity.getMyInt());
        assertEquals(true, entity.getMyBoolean());
        assertEquals("testString", entity.getMyString());
        assertEquals(Uri.parse("www.google.fr"), entity.getMyUri());
        assertEquals(8465.16, entity.getMyDouble());

        ContentValues contentValues = entity.getContentValues();

        assertEquals(Integer.valueOf(888), contentValues.getAsInteger(EntityContract.MY_INT));
        assertEquals(Boolean.TRUE, contentValues.getAsBoolean(EntityContract.MY_BOOLEAN));
        assertEquals("testString", contentValues.getAsString(EntityContract.MY_STRING));
        assertEquals("www.google.fr", contentValues.getAsString(EntityContract.MY_URI));
        assertEquals(8465.16, contentValues.getAsDouble(EntityContract.MY_DOUBLE));
    }

    public void testGetUri() {
        EntityClass entity = new EntityClass(new EntityCursor());
        entity.setId(45);
        assertEquals(Uri.parse("content://com.test.entity/table_entity/45"), entity.getUri());
    }
}
