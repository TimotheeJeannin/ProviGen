package com.tjeannin.provigen.test;

import android.net.Uri;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.Unique.OnConflict;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Unique;

public class SimpleContentProvider extends ProviGenProvider {

	public SimpleContentProvider() throws InvalidContractException {
		super(new Class[] { ContractOne.class, ContractThree.class });
	}

	@Contract(version = 1)
	public static interface ContractOne extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		public static final String MY_INT = "int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");

	}

	@Contract(version = 2)
	public static interface ContractTwo extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		public static final String MY_INT = "int";

		@Column(Type.TEXT)
		public static final String MY_STRING = "string";

		@Column(Type.REAL)
		public static final String MY_REAL = "real";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple_2");

	}

	@Contract(version = 1)
	public static interface ContractThree extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		public static final String ANOTHER_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/another_table_name");

	}

	@Contract(version = 1)
	public static interface ContractFour extends ProviGenBaseContract {

		@Unique(OnConflict.REPLACE)
		@Column(Type.INTEGER)
		public static final String ANOTHER_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_test_table");

	}

}
