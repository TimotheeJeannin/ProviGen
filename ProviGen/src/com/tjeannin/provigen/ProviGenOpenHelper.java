package com.tjeannin.provigen;

import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tjeannin.provigen.exceptions.IndexException;
import com.tjeannin.provigen.exceptions.InvalidContractException;

class ProviGenOpenHelper extends SQLiteOpenHelper {
	/**
	 * Logging tag.
	 */
	public static final String TAG = "PROVIGEN_TABLE";
	private ProviGenProvider provigenProvider;

	ProviGenOpenHelper(Context context, ProviGenProvider proviGenProvider, String databaseName, int version) {
		super(context, databaseName, null, version);
		this.provigenProvider = proviGenProvider;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		provigenProvider.onCreateDatabase(database);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		provigenProvider.onUpgradeDatabase(database, oldVersion, newVersion);
	}

	void createTable(final SQLiteDatabase database, final ContractHolder contractHolder) {
		// CREATE TABLE myTable (
		final StringBuilder builder = new StringBuilder("CREATE TABLE ");
		builder.append(contractHolder.getTable()).append(" (");
		// myInt INTEGER, myString TEXT
		for (final Iterator<DatabaseField> iterator = contractHolder.getFields().iterator(); iterator.hasNext(); ) {
			final DatabaseField field = iterator.next();
			builder.append(field.getName()).append(' ').append(field.getType().getDBStorageClass());
			if (field.getName().equals(contractHolder.getIdField())) {
				builder.append(" PRIMARY KEY AUTOINCREMENT");
			}
			for (final Constraint constraint : field.getConstraints()) {
				builder.append(' ').append(constraint.getType().getDbConstraintName()).append(" ON CONFLICT ").append(constraint.getOnConflict().getDbConflictResolution());
			}
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(')');
		Log.v(TAG, builder.toString());
		database.execSQL(builder.toString());
		try {
			IndexUtils.addConstraints(database, contractHolder);
		} catch (final IndexException e) {
			Log.e(TAG, "Could not create Index", e);
		}
	}

	void addMissingColumnsInTable(SQLiteDatabase database, ContractHolder contractHolder) {

		Cursor cursor = database.rawQuery("PRAGMA table_info(" + contractHolder.getTable() + ")", null);
		for (DatabaseField field : contractHolder.getFields()) {
			if (!fieldExistAsColumn(field.getName(), cursor)) {
				final StringBuilder sql = new StringBuilder("ALTER TABLE ").append(contractHolder.getTable()).append(" ADD COLUMN ").append(field.getName()).append(' ').append(field.getType().getDBStorageClass());
				Log.v(TAG, sql.toString());
				database.execSQL(sql.toString());
			}
		}
	}

	public boolean hasTableInDatabase(SQLiteDatabase database, ContractHolder contractHolder) {

		Cursor cursor = database.rawQuery(
		  "SELECT * FROM sqlite_master WHERE name = ? ",
		  new String[] { contractHolder.getTable() });
		boolean exists = cursor.getCount() != 0;
		cursor.close();
		return exists;
	}

	private boolean fieldExistAsColumn(String field, Cursor columnCursor) {
		if (columnCursor.moveToFirst()) {
			do {
				if (field.equals(columnCursor.getString(1))) {
					return true;
				}
			} while (columnCursor.moveToNext());
		}
		return false;
	}

}
