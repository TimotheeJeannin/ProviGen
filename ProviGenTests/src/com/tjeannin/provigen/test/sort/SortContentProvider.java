package com.tjeannin.provigen.test.sort;

import android.net.Uri;
import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.SortOrder;

public class SortContentProvider extends ProviGenProvider {

	public SortContentProvider() throws InvalidContractException {
		super(new Class[]{ContractOne.class, ContractTwo.class});
	}

	@Contract(version = 1)
	public interface ContractOne extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@SortOrder(order = SortOrder.Order.ASC)
		String MY_INT = "integer";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_sort");
	}

	@Contract(version = 1)
	public interface ContractTwo extends ProviGenBaseContract {
		@Column(Type.INTEGER)
		@SortOrder(order = SortOrder.Order.ASC, weight = 1)
		String MY_INT = "integer";

		@Column(Type.TEXT)
		@SortOrder(order = SortOrder.Order.ASC, weight = 2)
		String MY_STRING = "string";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_sort_2");
	}
}
