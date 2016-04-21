package com.tjeannin.provigen;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import com.tjeannin.provigen.helper.ContractUtil;
import com.tjeannin.provigen.model.Contract;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Behaves as a {@link ContentProvider} for the given contract class.
 */
public abstract class ProviGenProvider extends ContentProvider {

    private static final int ITEM = 1;
    private static final int ITEM_ID = 2;
    private static final int INNER_JOIN = 3;
    private static final int LEFT_OUTER_JOIN = 4;
    private static final int CROSS_JOIN = 5;

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
     * Override for insert conflict resolver. By default return SQLiteDatabase.CONFLICT_NONE.
     */
    public int conflictAlgorithm() {
        return SQLiteDatabase.CONFLICT_NONE;
    }

    /**
     * Whether to call the getContentResolver().notifyChange(uri, null) method after insert.
     * @return true or false
     */
    public boolean insertNotifyChange() {
        return true;
    }

    /**
     * Whether to call the getContentResolver().notifyChange(uri, null) method after update.
     * @return true or false
     */
    public boolean updateNotifyChange() {
        return true;
    }

    /**
     * Whether to call the getContentResolver().notifyChange(uri, null) method after delete.
     * @return true or false
     */
    public boolean deleteNotifyChange() {
        return true;
    }

    /**
     * Get max number of join entities (by default 10)
     * @return Max number of join entities
     */
    public int maxJoinEntities() {
        return 10;
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
            StringBuilder innerJoinPathSegment;
            StringBuilder leftOuterJoinPathSegment;
            StringBuilder crossJoinPathSegment;

            if(contract.getDbName() == null) {
                uriMatcher.addURI(contract.getAuthority(), contract.getTable(), ITEM);
                uriMatcher.addURI(contract.getAuthority(), contract.getTable() + "/#", ITEM_ID);

                innerJoinPathSegment = new StringBuilder(contract.getTable()).append("/inner_join/*/*");
                leftOuterJoinPathSegment = new StringBuilder(contract.getTable()).append("/left_outer_join/*/*");
                crossJoinPathSegment = new StringBuilder(contract.getTable()).append("/cross_join/*/*");
            } else {
                uriMatcher.addURI(contract.getAuthority(), contract.getDbName() + "/" + contract.getTable(), ITEM);
                uriMatcher.addURI(contract.getAuthority(), contract.getDbName() + "/" + contract.getTable() + "/#", ITEM_ID);

                innerJoinPathSegment = new StringBuilder(contract.getDbName()).append("/").append(contract.getTable()).append("/inner_join/*/*");
                leftOuterJoinPathSegment = new StringBuilder(contract.getDbName()).append("/").append(contract.getTable()).append("/left_outer_join/*/*");
                crossJoinPathSegment = new StringBuilder(contract.getDbName()).append("/").append(contract.getTable()).append("/cross_join/*/*");
            }

            for(int i = 0; i < maxJoinEntities(); i++) {
                uriMatcher.addURI(contract.getAuthority(), innerJoinPathSegment.toString(), INNER_JOIN);
                uriMatcher.addURI(contract.getAuthority(), leftOuterJoinPathSegment.toString(), LEFT_OUTER_JOIN);
                uriMatcher.addURI(contract.getAuthority(), crossJoinPathSegment.toString(), CROSS_JOIN);
                innerJoinPathSegment.append("/*/*");
                leftOuterJoinPathSegment.append("/*/*");
                crossJoinPathSegment.append("/*/*");
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

            // JOINs
            case INNER_JOIN:
            case LEFT_OUTER_JOIN:
            case CROSS_JOIN:
                Map<String, String> joinProjectionMap = new LinkedHashMap<>();
                for(String field : projection) {
                    joinProjectionMap.put(field, field.contains(".") ? field + " AS " + ContractUtil.joinName(field.substring(0, field.indexOf(".")), field.substring(field.indexOf(".") + 1)) : field);
                }
                queryBuilder.setProjectionMap(joinProjectionMap);
                queryBuilder.setTables(parseJoinUri(uri, contract.getDbName() != null));
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
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
                long rowId;
                if(conflictAlgorithm() == SQLiteDatabase.CONFLICT_NONE) {
                    rowId = db.insert(contract.getTable(), null, values);
                } else {
                    rowId = db.insertWithOnConflict(contract.getTable(), null, values, conflictAlgorithm());
                }

                Uri rowUri = Uri.EMPTY;
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(uri, rowId);
                }
                if(insertNotifyChange()) {
                    getContext().getContentResolver().notifyChange(uri, null);
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
                    for (ContentValues cv : values) {
                        if(conflictAlgorithm() == SQLiteDatabase.CONFLICT_NONE) {
                            db.insert(contract.getTable(), null, cv);
                        } else {
                            db.insertWithOnConflict(contract.getTable(), null, cv, conflictAlgorithm());
                        }
                    }
                    db.setTransactionSuccessful();

                    if(insertNotifyChange()) {
                        getContext().getContentResolver().notifyChange(uri, null);
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
        if(updateNotifyChange()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Contract contract = findMatchingContract(uri);
        SQLiteDatabase db = openDatabase(contract, false);
        int numberOfRowsAffected;

        switch (uriMatcher.match(uri)) {
            case ITEM:
            case INNER_JOIN:
            case LEFT_OUTER_JOIN:
            case CROSS_JOIN:
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
        if(deleteNotifyChange()) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        Contract contract = findMatchingContract(uri);
        switch (uriMatcher.match(uri)) {
            case ITEM:
            case INNER_JOIN:
            case LEFT_OUTER_JOIN:
            case CROSS_JOIN:
                return "vnd.android.cursor.dir/vdn." + contract.getAuthority() + '.' + contract.getTable();

            case ITEM_ID:
                return "vnd.android.cursor.item/vdn."  + contract.getAuthority() + '.' + contract.getTable();

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

    /**
     * Return join expression as string from URI
     * @param uri Join URI
     * @param hasDbName True if URI has database name
     * @return Join expression
     */
    private String parseJoinUri(Uri uri, boolean hasDbName) {
        List<String> pathSegments = uri.getPathSegments();
        if(pathSegments.size() > 3) {
            StringBuilder builder = new StringBuilder();
            String table1 = pathSegments.get(hasDbName ? 1 : 0);
            String joinType;
            switch (pathSegments.get(hasDbName ? 2 : 1)) {
                case "inner_join":
                    joinType = " INNER JOIN ";
                    break;

                case "left_outer_join":
                    joinType = " LEFT OUTER JOIN ";
                    break;

                case "cross_join":
                    joinType = " CROSS JOIN ";
                    break;

                default:
                    joinType = "";
            }
            builder.append(table1);

            List<String> subList = pathSegments.subList(hasDbName ? 3 : 2, pathSegments.size()); // ex: {"doctor", "doctor_id:id", ...}
            for(String tableOrFields : subList) {
                // fields separate by ":"
                if(tableOrFields.contains(":")) {
                    builder.append(" ON ")
                            .append(tableOrFields.substring(0, tableOrFields.indexOf(":")))
                            .append(" = ")
                            .append(tableOrFields.substring(tableOrFields.indexOf(":") + 1));
                }
                // table name
                else {
                    builder.append(joinType).append(" ").append(tableOrFields);
                }
            }

            return builder.toString();
        } else {
            throw new IllegalArgumentException("Illegal join URI: " + uri);
        }
    }
}
