package com.tjeannin.provigen.test.entity;

import android.net.Uri;

import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.exception.InvalidContractException;

public class EntityContentProvider extends ProviGenProvider {

    public EntityContentProvider() throws InvalidContractException {
        super(EntityContract.class);
    }

    @Contract(version = 1)
    public interface EntityContract extends ProviGenBaseContract {

        @Column(Type.INTEGER)
        String MY_INT = "my_int";

        @Column(Type.TEXT)
        String MY_STRING = "my_string";

        @Column(Type.REAL)
        String MY_DOUBLE = "my_double";

        @Column(Type.INTEGER)
        String MY_BOOLEAN = "my_boolean";

        @Column(Type.TEXT)
        String MY_URI = "my_uri";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.entity/table_entity");
    }
}
