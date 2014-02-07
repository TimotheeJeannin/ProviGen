package com.tjeannin.provigen.test.index;

import android.net.Uri;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.IndexType;

public class IndexProvider extends ProviGenProvider {

	public IndexProvider() throws InvalidContractException {
		super(new Class[] { IndexContract.class });
	}

	@Contract(version = 1)
	public interface IndexContract extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@Index
		String INDEX_COLUMN = "idx_1";

		@Column(Type.INTEGER)
		@Index(type = IndexType.UNIQUE)
		String UNIQUE_INDEX_COLUMN = "idx_2";

		@Column(Type.INTEGER)
		@Index(name = "INDEX_3")
		String INDEX_WITH_NAME = "idx_3";

		@Column(Type.INTEGER)
		@Index(type = IndexType.UNIQUE, name = "INDEX_4")
		String UNIQUE_INDEX_WITH_NAME = "idx_4";

		@Column(Type.INTEGER)
		@Index(name = "INDEX_5")
		String COMBIND_INDEX_1 = "idx_5";

		@Column(Type.INTEGER)
		@Index(name = "INDEX_5")
		String COMBINED_INDEX_2 = "idx_6";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/index_test");
	}
}
