package com.tjeannin.provigen;

import java.lang.reflect.Field;
import java.util.List;

import com.tjeannin.provigen.annotations.Column;
import com.tjeannin.provigen.annotations.Table;

import android.app.ActionBar.Tab;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.util.SparseArray;

public class ProviGenProvider extends ContentProvider {

	private String tableName;
	private SparseArray<String> columns;

	private SQLiteOpenHelper sqLiteOpenHelper;

	private UriMatcher uriMatcher;
	private static final int ALARM = 1;
	private static final int ALARM_ID = 2;

	public static final String DATABASE_NAME = "alarm_app";

	private static String authority;

	public ProviGenProvider(Class contractClass) throws InvalidContractException {

		Field[] fields = contractClass.getFields();
		for (Field field : fields) {

			if (field.getAnnotations().length == 1) {

				Table table = field.getAnnotation(Table.class);
				if (table != null) {
					tableName = field.getName();
				}

				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					columns.append(column.value(), field.getName());
				}

			} else {
				throw new InvalidContractException();
			}

		}

	}

	@Override
	public boolean onCreate() {

		sqLiteOpenHelper = new SQLiteOpenHelper(getContext(), DATABASE_NAME,
				null, 1) {

			private final String DATABASE_CREATE = "CREATE TABLE " + tableName
					+ " ( " + COLUMN_ID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_HOUR
					+ " INTEGER, " + COLUMN_MINUTE + " INTEGER, "
					+ COLUMN_SNOOZETIME + " NUMERIC, " + COLUMN_RINGTIME
					+ " NUMERIC, " + COLUMN_ACTIVE + " INTEGER, "
					+ COLUMN_SNOOZED + " INTEGER, " + COLUMN_NAME + " TEXT, "
					+ COLUMN_ACTIVE_DAYS + " TEXT " + " ) ";

			@Override
			public void onUpgrade(SQLiteDatabase database, int oldVersion,
					int newVersion) {

			}

			@Override
			public void onCreate(SQLiteDatabase database) {

				// Create alarm table.
				database.execSQL(DATABASE_CREATE);
			}
		};

		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(authority, "alarm", ALARM);
		uriMatcher.addURI(authority, "alarm/#", ALARM_ID);

		return true;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

		int numberOfRowsAffected = 0;

		switch (uriMatcher.match(uri)) {
		case ALARM:
			numberOfRowsAffected = database.delete(tableName, selection,
					selectionArgs);
			break;
		case ALARM_ID:
			String alarmId = String.valueOf(ContentUris.parseId(uri));

			if (TextUtils.isEmpty(selection)) {
				numberOfRowsAffected = database.delete(tableName, COLUMN_ID
						+ " = ? ", new String[] { alarmId });
			} else {
				numberOfRowsAffected = database.delete(tableName, selection
						+ " AND " + COLUMN_ID + " = ? ",
						appendToStringArray(selectionArgs, alarmId));
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
		case ALARM:
			return "vnd.android.cursor.dir/vnd."
					+ getContext().getPackageName() + ".alarm";
		case ALARM_ID:
			return "vnd.android.cursor.item/vnd."
					+ getContext().getPackageName() + ".alarm";
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();

		switch (uriMatcher.match(uri)) {
		case ALARM:
			long alarmId = database.insert(tableName, null, values);
			getContext().getContentResolver().notifyChange(uri, null);
			return Uri.withAppendedPath(uri, String.valueOf(alarmId));
		default:
			throw new IllegalArgumentException("Unknown uri " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();

		Cursor cursor = null;

		switch (uriMatcher.match(uri)) {
		case ALARM:
			cursor = database.query(tableName, projection, selection,
					selectionArgs, "", "", sortOrder);
			break;
		case ALARM_ID:
			String alarmId = String.valueOf(ContentUris.parseId(uri));
			if (TextUtils.isEmpty(selection)) {
				cursor = database.query(tableName, projection, COLUMN_ID
						+ " = ? ", new String[] { alarmId }, "", "", sortOrder);
			} else {
				cursor = database.query(tableName, projection, selection
						+ " AND " + COLUMN_ID + " = ? ",
						appendToStringArray(selectionArgs, alarmId), "", "",
						sortOrder);
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
		case ALARM:
			numberOfRowsAffected = database.update(tableName, values,
					selection, selectionArgs);
			break;
		case ALARM_ID:
			String alarmId = String.valueOf(ContentUris.parseId(uri));

			if (TextUtils.isEmpty(selection)) {
				numberOfRowsAffected = database.update(tableName, values,
						COLUMN_ID + " = ? ", new String[] { alarmId });
			} else {
				numberOfRowsAffected = database.update(tableName, values,
						selection + " AND " + COLUMN_ID + " = ? ",
						appendToStringArray(selectionArgs, alarmId));
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
	 * @param array
	 *            The array to copy.
	 * @param element
	 *            The element to append.
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
