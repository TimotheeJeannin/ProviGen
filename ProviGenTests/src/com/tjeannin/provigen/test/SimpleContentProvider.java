package com.tjeannin.provigen.test;

import android.net.Uri;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.Type;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Id;

public class SimpleContentProvider extends ProviGenProvider {

	public SimpleContentProvider() throws InvalidContractException {
		super(SimpleContract.class);
	}

	public static class SimpleContract {

		@Id
		@Column(type = Type.INTEGER)
		public static final String COLUMN_ID = "_id";

		@Column(type = Type.INTEGER)
		public static final String COLUMN_INT = "int";

		@Column(type = Type.TEXT)
		public static final String COLUMN_STRING = "string";

		@Column(type = Type.REAL)
		public static final String COLUMN_REAL = "hour";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");

	}

}
