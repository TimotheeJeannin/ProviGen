package com.tjeannin.provigen;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProviGenOpenHelper extends SQLiteOpenHelper {

	private ContractHolder contractHolder;

	ProviGenOpenHelper(Context context, ContractHolder contractHolder) throws InvalidContractException {
		super(context, "ProviGenDatabase", null, 1);
		this.contractHolder = contractHolder;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(buildTableCreationQuery());
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

	}

	private String buildTableCreationQuery() {

		StringBuilder builder = new StringBuilder("CREATE TABLE ");
		builder.append(contractHolder.getTable() + " ( ");
		for (int i = 0; i < contractHolder.getFields().size(); i++) {
			DatabaseField field = contractHolder.getFields().get(i);
			builder.append(" " + field.getName() + " " + field.getType());
			if (field.getName().equals(contractHolder.getIdField())) {
				builder.append(" PRIMARY KEY AUTOINCREMENT ");
			}
			if (i < contractHolder.getFields().size() - 1) {
				builder.append(", ");
			}
		}
		builder.append(" ) ");
		return builder.toString();
	}

}
