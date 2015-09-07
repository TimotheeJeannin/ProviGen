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

public class ConstraintsProvider extends ProviGenProvider {

    @Override
    public OpenHelper openHelper(Context context) {
        return new ProviGenSQLiteOpenHelper(context, "ProviGenDatabase", null, 1, contractClasses()) {
            @Override
            public void onCreate(SQLiteDatabase database) {
                Database db = new AndroidSQLiteDatabase(database);
                new TableBuilder(NotNullContract.class)
                        .addConstraint(NotNullContract.AN_INT, Constraint.NOT_NULL, OnConflict.ABORT)
                        .createTable(db);

                new TableBuilder(UniqueContract.class)
                        .addConstraint(UniqueContract.AN_INT, Constraint.UNIQUE, OnConflict.REPLACE)
                        .createTable(db);

                new TableBuilder(UniqueAndNotNullContract.class)
                        .addConstraint(UniqueAndNotNullContract.AN_INT, Constraint.NOT_NULL, OnConflict.ABORT)
                        .addConstraint(UniqueAndNotNullContract.AN_INT, Constraint.UNIQUE, OnConflict.REPLACE)
                        .createTable(db);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }

    @Override
    public Class[] contractClasses() {
        return new Class[]{NotNullContract.class, UniqueContract.class, UniqueAndNotNullContract.class};
    }


    public static interface NotNullContract extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/not_null_constraint_test");

    }

    public static interface UniqueContract extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");

    }

    public static interface UniqueAndNotNullContract extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_and_not_null_constraint_test");

    }
}
