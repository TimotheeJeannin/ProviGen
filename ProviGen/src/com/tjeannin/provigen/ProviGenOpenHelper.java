package com.tjeannin.provigen;

import java.util.Iterator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class ProviGenOpenHelper extends SQLiteOpenHelper {

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
		builder.append(contractHolder.getTable()).append(" ( ");
		// myInt INTEGER, myString TEXT
		for (final Iterator<DatabaseField> iterator = contractHolder.getFields().iterator(); iterator.hasNext(); ) {
			final DatabaseField field = iterator.next();
			builder.append(' ').append(field.getName()).append(' ').append(field.getType().getDBStorageClass());
			if (field.getName().equals(contractHolder.getIdField())) {
				builder.append(" PRIMARY KEY AUTOINCREMENT ");
			}
			for (final Constraint constraint : field.getConstraints()) {
				builder.append(' ').append(constraint.getType().getDbConstraintName()).append(" ON CONFLICT ").append(constraint.getOnConflict().getDbConflictResolution());
			}
			if (iterator.hasNext()) {
				builder.append(", ");
			}
		}
		builder.append(" );");
		database.execSQL(builder.toString());
		try {
			IndexUtils.addConstraints(database, contractHolder);
		} catch (final InvalidContractException e) {
			throw new RuntimeException(e);
		}
	}

	void addMissingColumnsInTable(SQLiteDatabase database, ContractHolder contractHolder) {

		Cursor cursor = database.rawQuery("PRAGMA table_info(" + contractHolder.getTable() + ")", null);
		for (DatabaseField field : contractHolder.getFields()) {
			if (!fieldExistAsColumn(field.getName(), cursor)) {
				database.execSQL("ALTER TABLE " + contractHolder.getTable() + " ADD COLUMN " + field.getName() + " " + field.getType().getDBStorageClass() + ";");
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
