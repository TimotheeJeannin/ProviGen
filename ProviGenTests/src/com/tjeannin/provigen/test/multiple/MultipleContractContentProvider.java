package com.tjeannin.provigen.test.multiple;

import android.content.Context;
import android.net.Uri;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.ProviGenSQLiteOpenHelper;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.OpenHelper;

public class MultipleContractContentProvider extends ProviGenProvider {

    private static Class[] contractClasses = new Class[]{ContractOne.class, ContractTwo.class};

    @Override
    public OpenHelper openHelper(Context context) {
        return new ProviGenSQLiteOpenHelper(getContext(), "ProviGenDatabase", null, 1, contractClasses);
    }

    @Override
    public Class[] contractClasses() {
        return contractClasses;
    }

    public static interface ContractOne extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String MY_INT = "int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");
    }

    public static interface ContractTwo extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        public static final String ANOTHER_INT = "another_int";

        @ContentUri
        public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/another_table_name");
    }
}
