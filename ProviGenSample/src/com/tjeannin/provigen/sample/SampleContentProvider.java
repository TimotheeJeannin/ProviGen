package com.tjeannin.provigen.sample;

import android.net.Uri;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.Type;
import com.tjeannin.provigen.annotations.Column;
import com.tjeannin.provigen.annotations.ContentUri;
import com.tjeannin.provigen.annotations.Id;

public class SampleContentProvider extends ProviGenProvider {

	public SampleContentProvider() throws InvalidContractException {
		super(SampleContract.class);
	}

	public static class SampleContract {

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
		public static final Uri CONTENT_URI = Uri.parse("content://com.tjeannin.provigen.sample/table_name");

	}

}
