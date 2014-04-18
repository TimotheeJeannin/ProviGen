package com.tjeannin.provigen.test.basis;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import com.tjeannin.provigen.Constraint;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.ProviGenSimpleSQLiteOpenHelper;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.NotNull;

public class SimpleContentProvider extends ProviGenProvider {

    @Override
    public SQLiteOpenHelper createOpenHelper(Context context) {
        return new ProviGenSimpleSQLiteOpenHelper(context, new Class[]{ContractOne.class}, 1);
    }

    @Override
    public Class[] getContractClasses() {
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
        @NotNull(Constraint.OnConflict.ABORT)
        String MY_STRING = "string";

        @Column(Type.REAL)
        String MY_REAL = "real";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");
    }
}
