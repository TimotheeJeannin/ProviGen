package com.tjeannin.provigen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;

import com.tjeannin.provigen.annotations.Column;
import com.tjeannin.provigen.annotations.Contract;
import com.tjeannin.provigen.annotations.Id;
import com.tjeannin.provigen.annotations.Table;

public class ProviGenProvider extends ContentProvider {

	private String authority;
	private String databaseName;
	private String databaseIdField;
	private String databaseTableName;
	private List<DatabaseField> databaseFields;

	private SQLiteOpenHelper sqLiteOpenHelper;

	private UriMatcher uriMatcher;
	private static final int ITEM = 1;
	private static final int ITEM_ID = 2;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ProviGenProvider(Class contractClass) throws InvalidContractException {

		Contract contract = (Contract) contractClass.getAnnotation(Contract.class);
		if (contract != null) {
			if (authority != null) {
				throw new InvalidContractException("A contract can not have several authority.");
			}
			if (databaseName != null) {
				throw new InvalidContractException("A contract can not have several databaseName.");
			}
			authority = contract.authority();
			databaseName = contract.databaseName();
		}

		databaseFields = new ArrayList<DatabaseField>();

		Field[] fields = contractClass.getFields();
		for (Field field : fields) {

			Table table = field.getAnnotation(Table.class);
			if (table != null) {
				if (databaseTableName != null) {
					throw new InvalidContractException("A contract can not have several fields annoted with Table.");
				}
				try {
					databaseTableName = (String) field.get(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				if (databaseIdField != null) {
					throw new InvalidContractException("A contract can not have several fields annoted with Id.");
				}
				try {
					databaseIdField = (String) field.get(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			Column column = field.getAnnotation(Column.class);
			if (column != null) {
				try {
					databaseFields.add(new DatabaseField((String) field.get(null), column.type()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onCreate() {

		sqLiteOpenHelper = new SQLiteOpenHelper(getContext(), databaseName, null, 1) {

			@Override
			public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

			}

			@Override
			public void onCreate(SQLiteDatabase database) {

				database.execSQL(buildTableCreationQuery(databaseTableName, databaseFields));
			}

			private String buildTableCreationQuery(String tableName, List<DatabaseField> fields) {
				StringBuilder builder = new StringBuilder("CREATE TABLE ");
				builder.append(tableName + " ( ");
				for (DatabaseField field : databaseFields) {
					builder.append(" " + field.getName() + " " + field.getType());
					if (field.getName().equals(databaseIdField)) {
						builder.append(" PRIMARY KEY AUTOINCREMENT ");
					}
					builder.append(", ");
				}
				builder.append(tableName + " ) ");
				return builder.toString();
			}

		};

		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(authority, databaseTableName, ITEM);
		uriMatcher.addURI(authority, databaseTableName + "/#", ITEM_ID);

		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

		int numberOfRowsAffected = 0;

		switch (uriMatcher.match(uri)) {
		case ITEM:
			numberOfRowsAffected = database.delete(databaseTableName, selection, selectionArgs);
			break;
		case ITEM_ID:
			String itemId = String.valueOf(ContentUris.parseId(uri));

			if (TextUtils.isEmpty(selection)) {
				numberOfRowsAffected = database.delete(databaseTableName, databaseIdField + " = ? ", new String[] { itemId });
			} else {
				numberOfRowsAffected = database.delete(databaseTableName, selection + " AND " +
						databaseIdField + " = ? ", appendToStringArray(selectionArgs, itemId));
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
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
		case ITEM:
			long itemId = database.insert(databaseTableName, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.withAppendedPath(uri, String.valueOf(itemId));
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

		Cursor cursor = null;

		switch (uriMatcher.match(uri)) {
		case ITEM:
			cursor = database.query(databaseTableName, projection, selection, selectionArgs, "", "", sortOrder);
			break;
		case ITEM_ID:
			String itemId = String.valueOf(ContentUris.parseId(uri));
			if (TextUtils.isEmpty(selection)) {
				cursor = database.query(databaseTableName, projection, databaseIdField + " = ? ", new String[] { itemId }, "", "", sortOrder);
			} else {
				cursor = database.query(databaseTableName, projection, selection + " AND " + databaseIdField + " = ? ",
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
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

		int numberOfRowsAffected = 0;

		switch (uriMatcher.match(uri)) {
		case ITEM:
			numberOfRowsAffected = database.update(databaseTableName, values, selection, selectionArgs);
			break;
		case ITEM_ID:
			String itemId = String.valueOf(ContentUris.parseId(uri));

			if (TextUtils.isEmpty(selection)) {
				numberOfRowsAffected = database.update(databaseTableName, values, databaseIdField + " = ? ", new String[] { itemId });
			} else {
				numberOfRowsAffected = database.update(databaseTableName, values, selection + " AND " + databaseIdField + " = ? ",
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
