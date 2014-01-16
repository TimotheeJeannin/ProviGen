package com.tjeannin.provigen.test.constraint;

import android.net.Uri;

import com.tjeannin.provigen.Constraint.OnConflict;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.*;
import com.tjeannin.provigen.annotation.Column.Type;

public class ConstraintsProvider extends ProviGenProvider {

	public ConstraintsProvider() throws InvalidContractException {
		super(new Class[] { NotNullContract.class, UniqueContract.class, UniqueAndNotNullContract.class });
	}

	@Contract(version = 1)
	public static interface NotNullContract extends ProviGenBaseContract {

		@NotNull(OnConflict.ABORT)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/not_null_constraint_test");

	}

	@Contract(version = 1)
	public static interface UniqueContract extends ProviGenBaseContract {

		@Unique(OnConflict.REPLACE)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");

	}

	@Contract(version = 1)
	public static interface UniqueAndNotNullContract extends ProviGenBaseContract {

		@Unique(OnConflict.REPLACE)
		@NotNull(OnConflict.ABORT)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_and_not_null_constraint_test");

	}
}
