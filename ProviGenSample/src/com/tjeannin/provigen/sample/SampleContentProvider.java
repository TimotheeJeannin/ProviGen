package com.tjeannin.provigen.sample;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.ProviGenSimpleSQLiteOpenHelper;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Id;


public class SampleContentProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper createOpenHelper(Context context) {
        return new ProviGenSimpleSQLiteOpenHelper(context, new Class[]{SampleContract.class}, 1);
    }

    @Override
    public Class[] getContractClasses() {
        return new Class[]{SampleContract.class};
    }

    @Contract(version = 1)
    public static interface SampleContract {

        @Id
        @Column(Type.INTEGER)
        public static final String _ID = "_id";

        @Column(Type.INTEGER)
        public static final String MY_INT = "int";

        @Column(Type.TEXT)
        public static final String MY_STRING = "string";

        @Column(Type.REAL)
        public static final String MY_REAL = "hour";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.tjeannin.provigen.sample/table_name");
    }
}
