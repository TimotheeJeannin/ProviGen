package com.tjeannin.provigen.test.basis;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenOpenHelper;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;

public class SimpleContentProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper openHelper(Context context) {
        return new ProviGenOpenHelper(getContext(), "ProviGenDatabase", null, 1, new Class[]{ContractOne.class});
    }

    @Override
    public Class[] contractClasses() {
        return new Class[]{ContractOne.class};
    }

    public interface ContractOne extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        String MY_INT = "int";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");
    }

    public interface ContractTwo extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        String MY_INT = "int";

        @Column(Type.TEXT)
        String MY_STRING = "string";

        @Column(Type.REAL)
        String MY_REAL = "real";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");
    }
}
