package com.tjeannin.provigen.test.constraint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.ProviGenSQLiteOpenHelper;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.database.AndroidSQLiteDatabase;
import com.tjeannin.provigen.database.Database;
import com.tjeannin.provigen.OpenHelper;
import com.tjeannin.provigen.helper.TableBuilder;
import com.tjeannin.provigen.model.Constraint;
import com.tjeannin.provigen.model.Constraint.OnConflict;

public class OnConflictProvider extends ProviGenProvider {

    @Override
    public OpenHelper openHelper(Context context) {
        return new ProviGenSQLiteOpenHelper(context, "ProviGenDatabase", null, 1, contractClasses()) {
            @Override
            public void onCreate(SQLiteDatabase database) {
                Database db = new AndroidSQLiteDatabase(database);
                new TableBuilder(ContractAbort.class)
                        .addConstraint(ContractAbort.AN_INT, Constraint.UNIQUE, OnConflict.ABORT)
                        .createTable(db);

                new TableBuilder(ContractReplace.class)
                        .addConstraint(ContractReplace.AN_INT, Constraint.UNIQUE, OnConflict.REPLACE)
                        .createTable(db);

                new TableBuilder(ContractFail.class)
                        .addConstraint(ContractFail.AN_INT, Constraint.UNIQUE, OnConflict.FAIL)
                        .createTable(db);

                new TableBuilder(ContractMultipleResolution.class)
                        .addConstraint(ContractMultipleResolution.AN_INT, Constraint.UNIQUE, OnConflict.REPLACE)
                        .addConstraint(ContractMultipleResolution.ANOTHER_INT, Constraint.UNIQUE, OnConflict.ABORT)
                        .createTable(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }

    @Override
    public Class[] contractClasses() {
        return new Class[]{ContractAbort.class, ContractReplace.class, ContractFail.class, ContractMultipleResolution.class};
    }

    public static interface ContractAbort extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/abort");
    }

    public static interface ContractReplace extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/replace");
    }

    public static interface ContractFail extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/fail");
    }

    public static interface ContractMultipleResolution extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @Column(Type.INTEGER)
        public static final String ANOTHER_INT = "another_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/multiple");
    }
}
