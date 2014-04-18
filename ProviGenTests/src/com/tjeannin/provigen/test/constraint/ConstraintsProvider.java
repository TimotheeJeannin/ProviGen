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
import com.tjeannin.provigen.annotation.NotNull;
import com.tjeannin.provigen.annotation.Unique;

public class ConstraintsProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper createOpenHelper(Context context) {
        return new ProviGenSimpleSQLiteOpenHelper(context, new Class[]{NotNullContract.class, UniqueContract.class, UniqueAndNotNullContract.class}, 1);
    }

    @Override
    public Class[] getContractClasses() {
        return new Class[]{NotNullContract.class, UniqueContract.class, UniqueAndNotNullContract.class};
    }


    public static interface NotNullContract extends ProviGenBaseContract {

        @NotNull(OnConflict.ABORT)
        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/not_null_constraint_test");

    }

    public static interface UniqueContract extends ProviGenBaseContract {

        @Unique(OnConflict.REPLACE)
        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");

    }

    public static interface UniqueAndNotNullContract extends ProviGenBaseContract {

        @Unique(OnConflict.REPLACE)
        @NotNull(OnConflict.ABORT)
        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_and_not_null_constraint_test");

    }
}
