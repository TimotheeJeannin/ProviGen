package com.tjeannin.provigen.test;

import android.content.ContentValues;
import android.database.Cursor;

import com.tjeannin.provigen.test.DefaultProvider.ContractOne;

public class DefaultTest extends ExtendedProviderTestCase<DefaultProvider> {

    public DefaultTest() {
        super(DefaultProvider.class, "com.test.simple");
    }

    public void testDefaultInt() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContractOne.A_STRING, "a dummy string");

        getMockContentResolver().insert(ContractOne.CONTENT_URI, contentValues);

        Cursor cursor = getMockContentResolver().query(ContractOne.CONTENT_URI, null, "", null, "");
        cursor.moveToFirst();
        assertEquals(45, cursor.getInt(cursor.getColumnIndex(ContractOne.AN_INT)));
    }

    public void testDefaultString() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ContractOne.AN_INT, 18);

        getMockContentResolver().insert(ContractOne.CONTENT_URI, contentValues);

        Cursor cursor = getMockContentResolver().query(ContractOne.CONTENT_URI, null, "", null, "");
        cursor.moveToFirst();
        assertEquals("default_string", cursor.getInt(cursor.getColumnIndex(ContractOne.A_STRING)));
    }
}
