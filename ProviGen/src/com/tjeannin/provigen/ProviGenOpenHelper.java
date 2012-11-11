package com.tjeannin.provigen;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ProviGenOpenHelper extends SQLiteOpenHelper {

	private ContractHolder contractHolder;

	public ProviGenOpenHelper(Context context, ContractHolder contractHolder) throws InvalidContractException {
		super(context, "ProviGenDatabase", null, 1);
		this.contractHolder = contractHolder;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(buildTableCreationQuery(contractHolder.getTable(), contractHolder.getFields()));
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

	}

	private String buildTableCreationQuery(String tableName, List<DatabaseField> fields) {

		StringBuilder builder = new StringBuilder("CREATE TABLE ");
		builder.append(tableName + " ( ");
		for (DatabaseField field : fields) {
			builder.append(" " + field.getName() + " " + field.getType());
			if (field.getName().equals(fields)) {
				builder.append(" PRIMARY KEY AUTOINCREMENT ");
			}
			builder.append(", ");
		}
		builder.append(tableName + " ) ");
		return builder.toString();
	}

}
