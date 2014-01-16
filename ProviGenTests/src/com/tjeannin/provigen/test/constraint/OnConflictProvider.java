package com.tjeannin.provigen.test.constraint;

import android.net.Uri;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.*;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.OnConflict.Resolve;

public class OnConflictProvider extends ProviGenProvider {

	public OnConflictProvider() throws InvalidContractException {
		super(new Class[] { ContractAbort.class, ContractReplace.class, ContractFail.class });
	}

	public static interface BaseContract extends ProviGenBaseContract {
		@Unique
		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";
	}

	@Contract(version = 1)
	@OnConflict(Resolve.ABORT)
	public static interface ContractAbort extends BaseContract {
		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/abort");
	}

	@Contract(version = 1)
	@OnConflict(Resolve.REPLACE)
	public static interface ContractReplace extends BaseContract {
		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/replace");
	}

	@Contract(version = 1)
	@OnConflict(Resolve.FAIL)
	public static interface ContractFail extends BaseContract {
		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/fail");
	}
}
