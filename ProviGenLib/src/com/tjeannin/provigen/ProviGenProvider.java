package com.tjeannin.provigen;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;

import com.tjeannin.provigen.model.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Behaves as a {@link ContentProvider} for the given contract class.
 */
public abstract class ProviGenProvider extends ContentProvider {

    private static final int ITEM = 1;
    private static final int ITEM_ID = 2;

    private List<Contract> contracts = new ArrayList<>();
    private UriMatcher uriMatcher;
    private SQLiteOpenHelper[] openHelpers;

    /**
     * This method should return an instance of a {@link android.database.sqlite.SQLiteOpenHelper}.
     * If you want to use multiple databases override {@link #openHelpers(Context)} and you may return null in this method.
     * It will be called only once, so you can safely create a new instance of the SQLiteOpenHelper on the method body.
     *
     * @param context A context to pass to the SQLiteOpenHelper instance while creating it.
     * @return the SQLiteOpenHelper that the ProviGenProvider will use.
     */
    public abstract SQLiteOpenHelper openHelper(Context context);

    /**
     * Override if you want to use multiple databases.
     * @param context A context to pass to the SQLiteOpenHelper instance while creating it.
     * @return the SQLiteOpenHelper[] that the ProviGenProvider will use.
     */
    public SQLiteOpenHelper [] openHelpers(Context context) {
        return new SQLiteOpenHelper[] { openHelper(context) };
    }

    /**
     * This method should return the list of contract classes that the ProviGenProvider will use.
     * It will be called only once.
     *
     * @return an array of contract classes.
     */
    public abstract Class[] contractClasses();

    /**
     * Override if you want to use multiple databases.
     * This method should return the array of array contract classes that the ProviGenProvider will use.
     * It will be called only once.
     *
     * @return an array of array of contract classes.
     */
    public Class[][] contractClassesMultipleDb() {
        return new Class[][] { contractClasses() };
    }

    /**
     * Override for insert conflict resolver. By default return SQLiteDatabase.CONFLICT_REPLACE.
     */
    public int conflictAlgorithm() {
        return SQLiteDatabase.CONFLICT_REPLACE;
    }

    @Override
    public boolean onCreate() {
        openHelpers = openHelpers(getContext());

        for(Class[] contractArray : contractClassesMultipleDb()) {
            for (Class contract : contractArray) {
                contracts.add(new Contract(contract));
            }
        }

        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        for (Contract contract : contracts) {
            if(contract.getDbName() == null) {
                uriMatcher.addURI(contract.getAuthority(), contract.getTable(), ITEM);
                uriMatcher.addURI(contract.getAuthority(), contract.getTable() + "/#", ITEM_ID);
            } else {
                uriMatcher.addURI(contract.getAuthority(), contract.getDbName() + "/" + contract.getTable(), ITEM);
                uriMatcher.addURI(contract.getAuthority(), contract.getDbName() + "/" + contract.getTable() + "/#", ITEM_ID);
            }
        }

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        Contract contract = findMatchingContract(uri);
        SQLiteDatabase db = openDatabase(contract, true);
        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case ITEM:
                queryBuilder.setTables(contract.getTable());
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case ITEM_ID:
                String itemId = String.valueOf(ContentUris.parseId(uri));
                if (TextUtils.isEmpty(selection)) {
                    queryBuilder.setTables(contract.getTable());
                    cursor = queryBuilder.query(db, projection, contract.getIdFields().get(0) + " = ? ", new String[]{itemId}, null, null, sortOrder);
                } else {
                    queryBuilder.setTables(contract.getTable());
                    cursor = queryBuilder.query(db, projection, selection + " AND " + contract.getIdFields().get(0) + " = ? ", appendToStringArray(selectionArgs, itemId), null, null, sortOrder);
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
    public Uri insert(Uri uri, ContentValues values) {
        Contract contract = findMatchingContract(uri);
        SQLiteDatabase db = openDatabase(contract, false);

        switch (uriMatcher.match(uri)) {
            case ITEM:
                long rowId = db.insertWithOnConflict(contract.getTable(), null, values, conflictAlgorithm());
                Uri rowUri = Uri.EMPTY;
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(uri, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }

                return rowUri;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        Contract contract = findMatchingContract(uri);
        SQLiteDatabase db = openDatabase(contract, false);

        switch (uriMatcher.match(uri)) {
            case ITEM:
                db.beginTransaction();
                try {
                    // вставляем
                    for (ContentValues cv : values) {
                        db.insertWithOnConflict(contract.getTable(), null, cv, conflictAlgorithm());
                    }
                    db.setTransactionSuccessful();

                    Context context = getContext();
                    if(context != null) {
                        context.getContentResolver().notifyChange(uri, null);
                    }
                } finally {
                    db.endTransaction();
                }
                return values.length;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Contract contract = findMatchingContract(uri);
        SQLiteDatabase db = openDatabase(contract, false);
        int numberOfRowsAffected;

        switch (uriMatcher.match(uri)) {
            case ITEM:
                numberOfRowsAffected = db.update(contract.getTable(), values, selection, selectionArgs);
                break;

            case ITEM_ID:
                String itemId = String.valueOf(ContentUris.parseId(uri));
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = db.update(contract.getTable(), values, contract.getIdFields().get(0) + " = ? ", new String[]{itemId});
                } else {
                    numberOfRowsAffected = db.update(contract.getTable(), values, selection + " AND " + contract.getIdFields().get(0) + " = ? ",
                            appendToStringArray(selectionArgs, itemId));
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return numberOfRowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Contract contract = findMatchingContract(uri);
        SQLiteDatabase db = openDatabase(contract, false);
        int numberOfRowsAffected;

        switch (uriMatcher.match(uri)) {
            case ITEM:
                numberOfRowsAffected = db.delete(contract.getTable(), selection, selectionArgs);
                break;

            case ITEM_ID:
                String itemId = String.valueOf(ContentUris.parseId(uri));
                if (TextUtils.isEmpty(selection)) {
                    numberOfRowsAffected = db.delete(contract.getTable(), contract.getIdFields().get(0) + " = ? ", new String[]{itemId});
                } else {
                    numberOfRowsAffected = db.delete(contract.getTable(), selection + " AND " +
                            contract.getIdFields().get(0) + " = ? ", appendToStringArray(selectionArgs, itemId));
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
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

    /**
     * @param uri The {@link Uri} to be matched.
     * @return A {@link com.tjeannin.provigen.model.Contract} matching the given {@link Uri}.
     */
    public Contract findMatchingContract(Uri uri) {
        for (Contract contract : contracts) {
            if(contract.getDbName() == null) {
                if (contract.getTable().equals(uri.getPathSegments().get(0))) {
                    return contract;
                }
            } else {
                if (contract.getDbName().equals(uri.getPathSegments().get(0)) && contract.getTable().equals(uri.getPathSegments().get(1))) {
                    return contract;
                }
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

    /**
     * Open database
     * @param contract Contract class
     * @param readOnly Open database only for read
     * @return SQLiteDatabase object
     */
    private SQLiteDatabase openDatabase(Contract contract, boolean readOnly) {
        if(contract.getDbName() == null) {
            return readOnly ? openHelpers[0].getReadableDatabase() : openHelpers[0].getWritableDatabase();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                for (SQLiteOpenHelper helper : openHelpers) {
                    if (contract.getDbName().equals(helper.getDatabaseName())) {
                        return readOnly ? helper.getReadableDatabase() : helper.getWritableDatabase();
                    }
                }
            }
            return readOnly ? openHelpers[1].getReadableDatabase() : openHelpers[1].getWritableDatabase();
        }
    }
}
