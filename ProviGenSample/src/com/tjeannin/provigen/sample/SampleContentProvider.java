package com.tjeannin.provigen.sample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.ProviGenDatabaseHelpers;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Id;

public class SampleContentProvider extends ProviGenProvider {

	public SampleContentProvider() throws InvalidContractException {
		super(SampleContract.class, SampleOpenHelper.class);
	}

	public static class SampleOpenHelper extends SQLiteOpenHelper {

		public SampleOpenHelper(Context context) {
			super(context, "sampleDatabase", null, 1);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			ProviGenDatabaseHelpers.createTable(database, SampleContract.class);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			ProviGenDatabaseHelpers.addMissingColumns(database, SampleContract.class);
		}
	}

	public static interface SampleContract {

		@Id
		@Column(Type.INTEGER)
		public static final String _ID = "_id";

		@Column(Type.INTEGER)
		public static final String MY_INT = "int";

		@Column(Type.TEXT)
		public static final String MY_STRING = "string";

		@Column(Type.REAL)
		public static final String MY_REAL = "hour";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.tjeannin.provigen.sample/table_name");
	}
}
