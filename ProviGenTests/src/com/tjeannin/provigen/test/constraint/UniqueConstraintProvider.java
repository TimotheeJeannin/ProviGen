package com.tjeannin.provigen.test.constraint;

import android.net.Uri;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.*;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.OnConflict.Resolve;

public class UniqueConstraintProvider extends ProviGenProvider {

	public UniqueConstraintProvider() throws InvalidContractException {
		super(ContractClass.class);
	}

	@Contract(version = 1)
	@OnConflict(Resolve.REPLACE)
	public static interface ContractClass extends ProviGenBaseContract {

		@Unique
		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");

	}
}
