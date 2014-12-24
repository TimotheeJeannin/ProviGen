package com.tjeannin.provigen.test;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.helper.TableBuilder;

public class DefaultProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper openHelper(final Context context) {
        return new SQLiteOpenHelper(context, "ProviGenDatabase", null, 1) {
            @Override
            public void onCreate(SQLiteDatabase database) {
                new TableBuilder(ContractOne.class)
                        .addDefault(ContractOne.AN_INT, 45)
                        .addDefault(ContractOne.A_STRING, "default_string")
                        .createTable(database);
            }

            @Override
            public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            }
        };
    }

    @Override
    public Class[] contractClasses() {
        return new Class[]{ContractOne.class};
    }


    public static interface ContractOne extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String AN_INT = "an_int";

        @Column(Type.TEXT)
        public static final String A_STRING = "a_string";

        @Column(Type.REAL)
        public static final String A_REAL = "a_real";

        @Column(Type.INTEGER)
        public static final String A_DATE = "a_date";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/not_null_constraint_test");

    }
}
