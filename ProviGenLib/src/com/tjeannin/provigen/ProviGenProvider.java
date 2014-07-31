package com.tjeannin.provigen;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import com.tjeannin.provigen.model.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Behaves as a {@link ContentProvider} for the given contract class.
 */
public abstract class ProviGenProvider extends ContentProvider {

    private List<Contract> contracts = new ArrayList<Contract>();

    private UriMatcher uriMatcher;
    private static final int ITEM = 1;
    private static final int ITEM_ID = 2;
    private SQLiteOpenHelper openHelper;

    /**
     * This method should return an instance of a {@link android.database.sqlite.SQLiteOpenHelper}.
     * It will be called only once, so you can safely create a new instance of the SQLiteOpenHelper on the method body.
     *
     * @param context A context to pass to the SQLiteOpenHelper instance while creating it.
     * @return the SQLiteOpenHelper that the ProviGenProvider will use.
     */
    public abstract SQLiteOpenHelper openHelper(Context context);

    /**
     * This method should return the list of contract classes that the ProviGenProvider will use.
     * It will be called only once.
     *
     * @return an array of contract classes.
     */
    public abstract Class[] contractClasses();

    @Override
    public boolean onCreate() {

        openHelper = openHelper(getContext());
        for (Class contract : contractClasses()) {
            contracts.add(new Contract(contract));
        }

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        for (Contract contract : contracts) {
            uriMatcher.addURI(contract.getAuthority(), contract.getTable(), ITEM);
            uriMatcher.addURI(contract.getAuthority(), contract.getTable() + "/#", ITEM_ID);
        }

        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = openHelper.getWritableDatabase();

        int numberOfRowsAffected = 0;
        Contract contract = findMatchingContract(uri);

        switch (uriMatcher.match(uri)) {
            case ITEM:
                numberOfRowsAffected = database.delete(contract.getTable(), selection, selectionArgs);
                break;
            case ITEM_ID:
                String itemId = String.valueOf(ContentUris.parseId(uri));

                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = database.delete(contract.getTable(), contract.getIdField() + " = ? ", new String[]{itemId});
                } else {
                    numberOfRowsAffected = database.delete(contract.getTable(), selection + " AND " +
                            contract.getIdField() + " = ? ", appendToStringArray(selectionArgs, itemId));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsAffected;
    }

    @Override
    public String getType(Uri uri) {

        Contract contract = findMatchingContract(uri);

        switch (uriMatcher.match(uri)) {
            case ITEM:
                return "vnd.android.cursor.dir/vdn." + contract.getTable();
            case ITEM_ID:
                return "vnd.android.cursor.item/vdn." + contract.getTable();
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = openHelper.getWritableDatabase();

        Contract contract = findMatchingContract(uri);
        switch (uriMatcher.match(uri)) {
            case ITEM:
                long itemId = database.insert(contract.getTable(), null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return Uri.withAppendedPath(uri, String.valueOf(itemId));
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = openHelper.getReadableDatabase();

        Contract contract = findMatchingContract(uri);
        Cursor cursor = null;

        switch (uriMatcher.match(uri)) {
            case ITEM:
                cursor = database.query(contract.getTable(), projection, selection, selectionArgs, "", "", sortOrder);
                break;
            case ITEM_ID:
                String itemId = String.valueOf(ContentUris.parseId(uri));
                if (TextUtils.isEmpty(selection)) {
                    cursor = database.query(contract.getTable(), projection, contract.getIdField() + " = ? ", new String[]{itemId}, "", "", sortOrder);
                } else {
                    cursor = database.query(contract.getTable(), projection, selection + " AND " + contract.getIdField() + " = ? ",
                            appendToStringArray(selectionArgs, itemId), "", "", sortOrder);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        // Make sure that potential listeners are getting notified.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase database = openHelper.getWritableDatabase();

        Contract contract = findMatchingContract(uri);
        int numberOfRowsAffected = 0;

        switch (uriMatcher.match(uri)) {
            case ITEM:
                numberOfRowsAffected = database.update(contract.getTable(), values, selection, selectionArgs);
                break;
            case ITEM_ID:
                String itemId = String.valueOf(ContentUris.parseId(uri));

                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = database.update(contract.getTable(), values, contract.getIdField() + " = ? ", new String[]{itemId});
                } else {
                    numberOfRowsAffected = database.update(contract.getTable(), values, selection + " AND " + contract.getIdField() + " = ? ",
                            appendToStringArray(selectionArgs, itemId));
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsAffected;
    }

    /**
     * @param uri The {@link Uri} to be matched.
     * @return A {@link com.tjeannin.provigen.model.Contract} matching the given {@link Uri}.
     */
    public Contract findMatchingContract(Uri uri) {
        for (Contract contract : contracts) {
            if (contract.getTable().equals(uri.getPathSegments().get(0))) {
                return contract;
            }
        }
        return null;
    }

    /**
     * Appends the given element to a copy of the given array.
     *
     * @param array   The array to copy.
     * @param element The element to append.
     * @return An Array with the element appended.
     */
    private static String[] appendToStringArray(String[] array, String element) {
        if (array != null) {
            String[] newArray = new String[array.length + 1];
            System.arraycopy(array, 0, newArray, 0, array.length);
            newArray[array.length] = element;
            return newArray;
        } else {
            return new String[]{element};
        }
    }

}
