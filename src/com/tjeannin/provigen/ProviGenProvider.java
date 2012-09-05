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
import com.tjeannin.provigen.annotations.Id;
import com.tjeannin.provigen.annotations.Table;

public class ProviGenProvider extends ContentProvider {

	private String tableName;
	private String idField;
	private List<DatabaseField> databaseFields;

	private SQLiteOpenHelper sqLiteOpenHelper;

	private UriMatcher uriMatcher;
	private static final int ALARM = 1;
	private static final int ALARM_ID = 2;

	public static final String DATABASE_NAME = "alarm_app";

	private static String authority = "com.tjeannin.provigen";

	@SuppressWarnings("rawtypes")
	public ProviGenProvider(Class contractClass) throws InvalidContractException {

		try {
			Field[] fields = contractClass.getFields();
			for (Field field : fields) {

				Table table = field.getAnnotation(Table.class);
				if (table != null) {
					tableName = (String) field.get(null);
				}

				Id id = field.getAnnotation(Id.class);
				if (id != null) {
					idField = (String) field.get(null);
				}

				databaseFields = new ArrayList<DatabaseField>();
				Column column = field.getAnnotation(Column.class);
				if (column != null) {
					databaseFields.add(new DatabaseField((String) field.get(null), column.type()));
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean onCreate() {

		sqLiteOpenHelper = new SQLiteOpenHelper(getContext(), DATABASE_NAME, null, 1) {

			@Override
			public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

			}

			@Override
			public void onCreate(SQLiteDatabase database) {

				// Create alarm table.
				database.execSQL(buildTableCreationQuery(tableName, databaseFields));
			}

			private String buildTableCreationQuery(String tableName, List<DatabaseField> fields) {
				StringBuilder builder = new StringBuilder("CREATE TABLE ");
				builder.append(tableName + " ( ");
				for (DatabaseField field : databaseFields) {
					builder.append(" " + field.getName() + " " + field.getType());
					if(field.getName().equals(idField)){
						builder.append(" PRIMARY KEY AUTOINCREMENT ");
					}
					builder.append(", ");
				}
				builder.append(tableName + " ) ");
				return builder.toString();
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
				numberOfRowsAffected = database.delete(tableName, idField
						+ " = ? ", new String[] { alarmId });
			} else {
				numberOfRowsAffected = database.delete(tableName, selection
						+ " AND " + idField + " = ? ",
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
				cursor = database.query(tableName, projection, idField
						+ " = ? ", new String[] { alarmId }, "", "", sortOrder);
			} else {
				cursor = database.query(tableName, projection, selection
						+ " AND " + idField + " = ? ",
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
						idField + " = ? ", new String[] { alarmId });
			} else {
				numberOfRowsAffected = database.update(tableName, values,
						selection + " AND " + idField + " = ? ",
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
