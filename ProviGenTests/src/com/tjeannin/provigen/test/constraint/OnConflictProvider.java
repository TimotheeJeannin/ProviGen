package com.tjeannin.provigen.test.constraint;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.tjeannin.provigen.Constraint.OnConflict;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.ProviGenSimpleSQLiteOpenHelper;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Unique;

public class OnConflictProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper createOpenHelper(Context context) {
        return new ProviGenSimpleSQLiteOpenHelper(context, new Class[] { ContractAbort.class, ContractReplace.class, ContractFail.class, ContractMultipleResolution.class }, 1);
    }

    @Override
    public Class[] getContractClasses() {
        return new Class[] { ContractAbort.class, ContractReplace.class, ContractFail.class, ContractMultipleResolution.class };
    }

	public static interface ContractAbort extends ProviGenBaseContract {

		@Unique(OnConflict.ABORT)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/abort");
	}

	public static interface ContractReplace extends ProviGenBaseContract {

		@Unique(OnConflict.REPLACE)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/replace");
	}

	public static interface ContractFail extends ProviGenBaseContract {

		@Unique(OnConflict.FAIL)
		@Column(Type.INTEGER)
		public static final String AN_INT = "an_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/fail");
	}

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
