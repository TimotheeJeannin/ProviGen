package com.tjeannin.provigen.test.constraint;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.constraint.OnConflictProvider.ContractAbort;
import com.tjeannin.provigen.test.constraint.OnConflictProvider.ContractFail;
import com.tjeannin.provigen.test.constraint.OnConflictProvider.ContractMultipleResolution;
import com.tjeannin.provigen.test.constraint.OnConflictProvider.ContractReplace;

public class OnConflictTest extends ExtendedProviderTestCase<OnConflictProvider> {

    private MockContentResolver contentResolver;
    private ContentValues contentValues;
    private ContentValues conflictingContentValues;

    public OnConflictTest() {
        super(OnConflictProvider.class, "com.test.simple");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        contentValues = new ContentValues();
        contentValues.put(ContractAbort._ID, 1);
        contentValues.put(ContractAbort.AN_INT, 15);

        conflictingContentValues = new ContentValues();
        conflictingContentValues.put(ContractAbort._ID, 2);
        conflictingContentValues.put(ContractAbort.AN_INT, 15);

        contentResolver = getMockContentResolver();
    }

    public int getRowInt(Uri contentUri, String rowId) {
        Cursor cursor = contentResolver.query(Uri.withAppendedPath(contentUri, rowId), null, "", null, "");
        cursor.moveToFirst();
        int intValue = cursor.getInt(cursor.getColumnIndex(ContractAbort.AN_INT));
        cursor.close();
        return intValue;
    }

    public void testOnConflictAbort() {
        contentResolver.insert(ContractAbort.CONTENT_URI, contentValues);
        contentResolver.insert(ContractAbort.CONTENT_URI, conflictingContentValues);
        assertEquals(1, getRowCount(Uri.withAppendedPath(ContractAbort.CONTENT_URI, "1")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractAbort.CONTENT_URI, "2")));
        assertEquals(15, getRowInt(ContractAbort.CONTENT_URI, "1"));
    }

    public void testOnConflictReplace() {
        contentResolver.insert(ContractReplace.CONTENT_URI, contentValues);
        contentResolver.insert(ContractReplace.CONTENT_URI, conflictingContentValues);
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractReplace.CONTENT_URI, "1")));
        assertEquals(1, getRowCount(Uri.withAppendedPath(ContractReplace.CONTENT_URI, "2")));
        assertEquals(15, getRowInt(ContractReplace.CONTENT_URI, "2"));
    }

    public void testOnConflictFail() {
        contentResolver.insert(ContractFail.CONTENT_URI, contentValues);
        contentResolver.insert(ContractFail.CONTENT_URI, conflictingContentValues);
        assertEquals(1, getRowCount(Uri.withAppendedPath(ContractFail.CONTENT_URI, "1")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractFail.CONTENT_URI, "2")));
        assertEquals(15, getRowInt(ContractFail.CONTENT_URI, "1"));
    }

    public void testMultipleConflictResolution() {

        ContentValues contentValues = new ContentValues();
        contentValues.put(ContractMultipleResolution._ID, 1);
        contentValues.put(ContractMultipleResolution.AN_INT, 15);
        contentValues.put(ContractMultipleResolution.ANOTHER_INT, 25);

        ContentValues conflictingAbortContentValues = new ContentValues();
        conflictingAbortContentValues.put(ContractMultipleResolution._ID, 2);
        conflictingAbortContentValues.put(ContractMultipleResolution.AN_INT, 16);
        conflictingAbortContentValues.put(ContractMultipleResolution.ANOTHER_INT, 25);

        ContentValues conflictingReplaceContentValues = new ContentValues();
        conflictingReplaceContentValues.put(ContractMultipleResolution._ID, 3);
        conflictingReplaceContentValues.put(ContractMultipleResolution.AN_INT, 15);
        conflictingReplaceContentValues.put(ContractMultipleResolution.ANOTHER_INT, 26);

        contentResolver.insert(ContractMultipleResolution.CONTENT_URI, contentValues);
        assertEquals(1, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "1")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "2")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "3")));

        contentResolver.insert(ContractMultipleResolution.CONTENT_URI, conflictingAbortContentValues);
        assertEquals(1, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "1")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "2")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "3")));

        contentResolver.insert(ContractMultipleResolution.CONTENT_URI, conflictingReplaceContentValues);
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "1")));
        assertEquals(0, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "2")));
        assertEquals(1, getRowCount(Uri.withAppendedPath(ContractMultipleResolution.CONTENT_URI, "3")));
    }
}
