package com.tjeannin.provigen;

import com.tjeannin.provigen.annotation.Contract;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

/**
 * Behaves as a {@link ContentProvider} for the given {@link Contract} class.
 */
public class ProviGenProvider extends ContentProvider {

	private ProviGenOpenHelper openHelper;

	private UriMatcher uriMatcher;
	private static final int ITEM = 1;
	private static final int ITEM_ID = 2;

	private ContractHolder contractHolder;

	/**
	 * @param contractClass A {@link Contract} class.
	 * @throws InvalidContractException
	 */
	@SuppressWarnings("rawtypes")
	public ProviGenProvider(Class contractClass) throws InvalidContractException {
		contractHolder = new ContractHolder(contractClass);
	}

	@Override
	public boolean onCreate() {

		openHelper = new ProviGenOpenHelper(getContext(), this, contractHolder);

		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(contractHolder.getAuthority(), contractHolder.getTable(), ITEM);
		uriMatcher.addURI(contractHolder.getAuthority(), contractHolder.getTable() + "/#", ITEM_ID);

		return true;
	}

	/**
	 * Called when the database is created for the first time. </br>
	 * The {@link ProviGenProvider} automatically creates a database table if {@code super.onCreateDatabase(database)} is called.</br>
	 * The initial population of the tables should happen here.
	 * @param database The database.
	 */
	public void onCreateDatabase(SQLiteDatabase database) {
		openHelper.autoCreateDatabase(database);
	}

	/**
	 * Called when the database needs to be upgraded. </br>
	 * If the {@link Contract} contains new columns, the {@link ProviGenProvider} automatically adds these columns
	 * if {@code super.onUpgradeDatabase(database, oldVersion, newVersion)} is called.</br>
	 * Anything else related to database upgrade should be done here.
	 * <p>
	 * This method executes within a transaction. If an exception is thrown, all changes
	 * will automatically be rolled back.
	 * </p>
	 * @param database The database.
	 * @param oldVersion The old database version (same as contract old version).
	 * @param newVersion The new database version (same as contract new version).
	 */
	public void onUpgradeDatabase(SQLiteDatabase database, int oldVersion, int newVersion) {
		openHelper.autoUpgradeDatabase(database, oldVersion, newVersion);
	}

	/**
	 * Used to switch from a {@link Contract} to an other dynamically.<br/>
	 * Note that this may change the database schema.
	 * May be used for testing purpose. <br/>
	 * @param contractClass The {@link Contract} to switch to.
	 * @throws InvalidContractException
	 */
	@SuppressWarnings("rawtypes")
	public void setContractClass(Class contractClass) throws InvalidContractException {
		contractHolder = new ContractHolder(contractClass);
		openHelper = new ProviGenOpenHelper(getContext(), this, contractHolder);
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = openHelper.getWritableDatabase();

		int numberOfRowsAffected = 0;

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
		switch (uriMatcher.match(uri)) {
		case ITEM:
			return "vnd.android.cursor.dir/vnd." + getContext().getPackageName() + ".item";
		case ITEM_ID:
			return "vnd.android.cursor.item/vnd." + getContext().getPackageName() + ".item";
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = openHelper.getWritableDatabase();

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
			for (int i = 0; i < array.length; i++) {
				newArray[i] = array[i];
			}
			newArray[array.length] = element;
			return newArray;
		} else {
			return new String[] { element };
		}
	}

}
