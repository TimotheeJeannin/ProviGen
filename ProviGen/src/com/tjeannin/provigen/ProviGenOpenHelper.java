package com.tjeannin.provigen;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.tjeannin.provigen.exceptions.IndexException;
import com.tjeannin.provigen.utils.DataBaseHelper;
import com.tjeannin.provigen.utils.IndexUtils;

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
		final String tableName = contractHolder.getTable();
		// CREATE TABLE myTable (
		final StringBuilder builder = new StringBuilder("CREATE TABLE ");
		builder.append(tableName).append(" (");
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
			IndexUtils.addConstraints(database, tableName, contractHolder.getIndexInformation());
		} catch (final IndexException e) {
			Log.e(TAG, "Could not create Index", e);
		}
	}

	void updateTable(final SQLiteDatabase database, final ContractHolder contractHolder) {
		final String tableName = contractHolder.getTable();
		final List<String> columnNames = DataBaseHelper.getColumnInformation(database, tableName);
		for (final DatabaseField field : contractHolder.getFields()) {
			final String columnName = field.getName();
			if (!columnNames.contains(columnName)) {
				final StringBuilder sql = new StringBuilder("ALTER TABLE ").append(tableName).append(" ADD COLUMN ").append(columnName).append(' ').append(field.getType().getDBStorageClass());
				Log.v(TAG, sql.toString());
				database.execSQL(sql.toString());
			}
		}
		try {
			IndexUtils.addConstraints(database, tableName, contractHolder.getIndexInformation());
		} catch (final IndexException e) {
			Log.e(TAG, "Could not update Index", e);
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
}
