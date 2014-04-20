package com.tjeannin.provigen.test.constraint;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.tjeannin.provigen.*;
import com.tjeannin.provigen.Constraint.OnConflict;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.builder.TableBuilder;

public class ConstraintsProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper createOpenHelper(final Context context) {
        return new SQLiteOpenHelper(context, "ProviGenDatabase", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase database) {
                try {
                    new TableBuilder(NotNullContract.class)
                            .addConstraint(NotNullContract.AN_INT, Constraint.NOT_NULL, OnConflict.ABORT)
                            .createTable(database);

                    new TableBuilder(UniqueContract.class)
                            .addConstraint(UniqueContract.AN_INT, Constraint.UNIQUE, OnConflict.REPLACE)
                            .createTable(database);

                    new TableBuilder(UniqueAndNotNullContract.class)
                            .addConstraint(UniqueAndNotNullContract.AN_INT, Constraint.NOT_NULL, OnConflict.ABORT)
                            .addConstraint(UniqueAndNotNullContract.AN_INT, Constraint.UNIQUE, OnConflict.REPLACE)
                            .createTable(database);
                } catch (InvalidContractException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }

    @Override
    public Class[] getContractClasses() {
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
