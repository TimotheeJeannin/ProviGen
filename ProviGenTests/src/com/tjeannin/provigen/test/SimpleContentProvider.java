package com.tjeannin.provigen.test;

import android.net.Uri;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.Type;
import com.tjeannin.provigen.annotations.Column;
import com.tjeannin.provigen.annotations.Contract;
import com.tjeannin.provigen.annotations.Id;
import com.tjeannin.provigen.annotations.Table;

public class SimpleContentProvider extends ProviGenProvider {

	public SimpleContentProvider() throws InvalidContractException {
		super(SimpleContract.class);
	}

	@Contract(authority = "com.test.simple", databaseName = "simple")
	public static class SimpleContract {

		@Table
		public static final String TABLE_NAME = "simple";

		@Id
		@Column(type = Type.INTEGER)
		public static final String COLUMN_ID = "_id";

		@Column(type = Type.INTEGER)
		public static final String COLUMN_INT = "int";

		@Column(type = Type.TEXT)
		public static final String COLUMN_STRING = "string";

		@Column(type = Type.REAL)
		public static final String COLUMN_REAL = "hour";

		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/" + TABLE_NAME);

	}

}
