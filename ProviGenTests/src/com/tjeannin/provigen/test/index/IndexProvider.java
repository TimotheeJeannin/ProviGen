package com.tjeannin.provigen.test.index;

import android.net.Uri;
import com.tjeannin.provigen.exceptions.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.annotation.Unique;

public class IndexProvider extends ProviGenProvider {

	public IndexProvider() throws InvalidContractException {
		super(new Class[] { IndexContract.class });
	}

	@Contract(version = 1)
	public interface IndexContract extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index("INDEX_1")
		String INDEX_COLUMN = "idx_1";

		@Column(Type.INTEGER)
		@Index(value = "INDEX_2",type = IndexType.UNIQUE)
		String UNIQUE_INDEX_COLUMN = "idx_2";

		@Column(Type.INTEGER)
		@Index("INDEX_3")
		String INDEX_WITH_NAME = "idx_3";

		@Column(Type.INTEGER)
		@Index(type = IndexType.UNIQUE, value = "INDEX_4")
		String UNIQUE_INDEX_WITH_NAME = "idx_4";

		@Column(Type.INTEGER)
		@Index("INDEX_5")
		String COMBIND_INDEX_1 = "idx_5";

		@Column(Type.INTEGER)
		@Index("INDEX_5")
		String COMBINED_INDEX_2 = "idx_6";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/index_test");
	}

	@Contract(version = 1)
	public interface UniqueAndUniqueIndexContract extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Unique
		@Index(value = "INDEX_1", type = IndexType.UNIQUE)
		String INDEX_COLUMN = "idx_1";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_and_unique_index_test");
	}

	@Contract(version = 1)
	public interface PartialIndexContract extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index(value = "INDEX_1", type = IndexType.UNIQUE, expr = "idx_1 > 2")
		String INDEX_COLUMN = "idx_1";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/partial_index_test");
	}

	@Contract(version = 1)
	public interface WeightedIndexContract extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index(position = 9, value = "INDEX_8")
		String COMBIND_INDEX_1 = "idx_1";

		@Column(Type.INTEGER)
		@Index(position = 1, value = "INDEX_8")
		String COMBIND_INDEX_2 = "idx_2";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/partial_index_test");
	}

	@Contract(version = 1)
	public interface UpdateIndexContract1 extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index("INDEX_9")
		String FIELD_1 = "idx_1";

		@Column(Type.INTEGER)
		String FIELD_2 = "idx_2";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/update_index_test");
	}

	@Contract(version = 2)
	public interface UpdateIndexContract2 extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index("INDEX_9")
		String FIELD_1 = "idx_1";

		@Column(Type.INTEGER)
		@Index("INDEX_10")
		String FIELD_2 = "idx_2";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/update_index_test");
	}

	@Contract(version = 3)
	public interface UpdateIndexContract3 extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index("INDEX_9")
		String FIELD_1 = "idx_1";

		@Column(Type.INTEGER)
		String FIELD_2 = "idx_2";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/update_index_test");
	}
}
