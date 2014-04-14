package com.tjeannin.provigen;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import com.tjeannin.provigen.annotation.Contract;

/**
 * Behaves as a {@link ContentProvider} for the given {@link Contract} class.
 */
public abstract class ProviGenProvider extends ContentProvider {

	private ContractHolderList contracts = new ContractHolderList();

	private UriMatcher uriMatcher;
	private static final int ITEM = 1;
	private static final int ITEM_ID = 2;
    private SQLiteOpenHelper openHelper;

    public abstract SQLiteOpenHelper createOpenHelper(Context context);

    public abstract Class[] getContractClasses();

	@Override
	public boolean onCreate() {

        openHelper = createOpenHelper(getContext());
        for(Class contract : getContractClasses()){
            try {
                contracts.add(new ContractHolder(contract));
            } catch (InvalidContractException e) {
                e.printStackTrace();
            }
        }

		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		for (ContractHolder contract : contracts) {
			uriMatcher.addURI(contract.getAuthority(), contract.getTable(), ITEM);
			uriMatcher.addURI(contract.getAuthority(), contract.getTable() + "/#", ITEM_ID);
		}

		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = openHelper.getWritableDatabase();

		int numberOfRowsAffected = 0;
		ContractHolder contractHolder = contracts.findMatching(uri);

		switch (uriMatcher.match(uri)) {
		case ITEM:
			numberOfRowsAffected = database.delete(contractHolder.getTable(), selection, selectionArgs);
			break;
		case ITEM_ID:
			String itemId = String.valueOf(ContentUris.parseId(uri));

			if (TextUtils.isEmpty(selection)) {
				numberOfRowsAffected = database.delete(contractHolder.getTable(), contractHolder.getIdField() + " = ? ", new String[] { itemId });
			} else {
				numberOfRowsAffected = database.delete(contractHolder.getTable(), selection + " AND " +
						contractHolder.getIdField() + " = ? ", appendToStringArray(selectionArgs, itemId));
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

		ContractHolder contractHolder = contracts.findMatching(uri);

		switch (uriMatcher.match(uri)) {
		case ITEM:
			return "vnd.android.cursor.dir/vdn." + contractHolder.getTable();
		case ITEM_ID:
			return "vnd.android.cursor.item/vdn." + contractHolder.getTable();
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = openHelper.getWritableDatabase();

		ContractHolder contractHolder = contracts.findMatching(uri);
		switch (uriMatcher.match(uri)) {
		case ITEM:
			long itemId = database.insert(contractHolder.getTable(), null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.withAppendedPath(uri, String.valueOf(itemId));
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = openHelper.getReadableDatabase();

		ContractHolder contractHolder = contracts.findMatching(uri);
		Cursor cursor = null;

		switch (uriMatcher.match(uri)) {
		case ITEM:
			cursor = database.query(contractHolder.getTable(), projection, selection, selectionArgs, "", "", sortOrder);
			break;
		case ITEM_ID:
			String itemId = String.valueOf(ContentUris.parseId(uri));
			if (TextUtils.isEmpty(selection)) {
				cursor = database.query(contractHolder.getTable(), projection, contractHolder.getIdField() + " = ? ", new String[] { itemId }, "", "", sortOrder);
			} else {
				cursor = database.query(contractHolder.getTable(), projection, selection + " AND " + contractHolder.getIdField() + " = ? ",
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

		ContractHolder contractHolder = contracts.findMatching(uri);
		int numberOfRowsAffected = 0;

		switch (uriMatcher.match(uri)) {
		case ITEM:
			numberOfRowsAffected = database.update(contractHolder.getTable(), values, selection, selectionArgs);
			break;
		case ITEM_ID:
			String itemId = String.valueOf(ContentUris.parseId(uri));

			if (TextUtils.isEmpty(selection)) {
				numberOfRowsAffected = database.update(contractHolder.getTable(), values, contractHolder.getIdField() + " = ? ", new String[] { itemId });
			} else {
				numberOfRowsAffected = database.update(contractHolder.getTable(), values, selection + " AND " + contractHolder.getIdField() + " = ? ",
						appendToStringArray(selectionArgs, itemId));
			}
			break;
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return numberOfRowsAffected;
	}

	/**
	 * Appends the given element to a copy of the given array.
	 * @param array The array to copy.
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
			return new String[] { element };
		}
	}

}
