package com.tjeannin.provigen.test.constraint;

import android.net.Uri;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.*;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.Constraint.OnConflict;

public class OnConflictProvider extends ProviGenProvider {

	public OnConflictProvider() throws InvalidContractException {
		super(new Class[] { ContractAbort.class, ContractReplace.class, ContractFail.class, ContractMultipleResolution.class });
	}

	@Contract(version = 1)
	public static interface ContractAbort extends ProviGenBaseContract {

		@Unique(OnConflict.ABORT)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/abort");
	}

	@Contract(version = 1)
	public static interface ContractReplace extends ProviGenBaseContract {

		@Unique(OnConflict.REPLACE)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/replace");
	}

	@Contract(version = 1)
	public static interface ContractFail extends ProviGenBaseContract {

		@Unique(OnConflict.FAIL)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/fail");
	}

	@Contract(version = 1)
	public static interface ContractMultipleResolution extends ProviGenBaseContract {

		@Unique(OnConflict.REPLACE)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@Unique(OnConflict.ABORT)
		@Column(Type.INTEGER)
		public static final String ANOTHER_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/multiple");
	}
}
