package com.tjeannin.provigen.test.constraint;

import android.net.Uri;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.OnConflict;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.NotNull;

public class NotNullConstraintProvider extends ProviGenProvider {

	public NotNullConstraintProvider() throws InvalidContractException {
		super(ContractClass.class);
	}

	@Contract(version = 1)
	public static interface ContractClass extends ProviGenBaseContract {

		@NotNull(OnConflict.ABORT)
		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");

	}
}
