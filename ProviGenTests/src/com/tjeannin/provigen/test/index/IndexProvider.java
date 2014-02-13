package com.tjeannin.provigen.test.index;

import android.net.Uri;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.annotation.Unique;
import com.tjeannin.provigen.exceptions.InvalidContractException;

/**
 * Provider class with all contracts that are used by the {@link com.tjeannin.provigen.test.index.IndexTest}.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public class IndexProvider extends ProviGenProvider {

    public static final String INDEX_1 = "INDEX_1";
    public static final String INDEX_2 = "INDEX_2";
    public static final String INDEX_3 = "INDEX_3";
    public static final String INDEX_4 = "INDEX_4";
    public static final String INDEX_5 = "INDEX_5";

    public IndexProvider() throws InvalidContractException {
        super(new Class[] { IndexContract.class });
    }

    @Contract(version = 1)
    public interface IndexContract extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String INDEX_COLUMN = "idx_1";

        @Column(Type.INTEGER)
        @Index(value = INDEX_2, type = IndexType.UNIQUE)
        String UNIQUE_INDEX_COLUMN = "idx_2";

        @Column(Type.INTEGER)
        @Index(INDEX_3)
        String INDEX_WITH_NAME = "idx_3";

        @Column(Type.INTEGER)
        @Index(value = INDEX_4, type = IndexType.UNIQUE)
        String UNIQUE_INDEX_WITH_NAME = "idx_4";

        @Column(Type.INTEGER)
        @Index(INDEX_5)
        String COMBIND_INDEX_1 = "idx_5";

        @Column(Type.INTEGER)
        @Index(INDEX_5)
        String COMBINED_INDEX_2 = "idx_6";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/index_test");
    }

    @Contract(version = 1)
    public interface UniqueAndUniqueIndexContract extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Unique
        @Index(value = INDEX_1, type = IndexType.UNIQUE)
        String INDEX_COLUMN = "idx_1";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_and_unique_index_test");
    }

    @Contract(version = 1)
    public interface PartialIndexContract extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(value = INDEX_1, type = IndexType.UNIQUE, expr = "idx_1 > 2")
        String INDEX_COLUMN = "idx_1";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/partial_index_test");
    }

    @Contract(version = 1)
    public interface WeightedIndexContract extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(value = INDEX_1, position = 9)
        String COMBIND_INDEX_1 = "idx_1";

        @Column(Type.INTEGER)
        @Index(value = INDEX_1, position = 1)
        String COMBIND_INDEX_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/partial_index_test");
    }

    @Contract(version = 1)
    public interface UpdateIndexContract1 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/update_index_test");
    }

    @Contract(version = 2)
    public interface UpdateIndexContract2 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        @Index(INDEX_2)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/update_index_test");
    }

    @Contract(version = 3)
    public interface UpdateIndexContract3 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/update_index_test");
    }

    @Contract(version = 1)
    public interface ModifyIndexContract1 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/modify_index_test");
    }

    @Contract(version = 2)
    public interface ModifyIndexContract2 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/modify_index_test");
    }

    @Contract(version = 3)
    public interface ModifyIndexContract3 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(INDEX_1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/modify_index_test");
    }

    @Contract(version = 1)
    public interface ChangeIndexColumnsContract1 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(value = INDEX_1, position = 3)
        String FIELD_1 = "idx_1";

        @Index(value = INDEX_1, position = 1)
        @Column(Type.INTEGER)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/order_index_test");
    }

    @Contract(version = 2)
    public interface ChangeIndexColumnsContract2 extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index(value = INDEX_1, position = 1)
        String FIELD_1 = "idx_1";

        @Column(Type.INTEGER)
        @Index(value = INDEX_1, position = 2)
        String FIELD_2 = "idx_2";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/order_index_test");
    }

    @Contract(version = 1)
    public interface FailingDuplicateNameContract extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index("f_duplicate_name_index_test")
        String FIELD_1 = "idx_1";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/f_duplicate_name_index_test");
    }

    @Contract(version = 1)
    public interface FailingEmptyNameContract extends ProviGenBaseContract {
        @Column(Type.INTEGER)
        @Index("")
        String FIELD_1 = "idx_1";

        @ContentUri
        Uri CONTENT_URI = Uri.parse("content://com.test.simple/f_empty_name_index_test");
    }
}
