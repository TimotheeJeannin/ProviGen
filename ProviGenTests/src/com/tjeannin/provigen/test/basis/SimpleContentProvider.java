package com.tjeannin.provigen.test.basis;

import android.net.Uri;

import com.tjeannin.provigen.Constraint;
import com.tjeannin.provigen.exception.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.ProviGenProvider;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.NotNull;

public class SimpleContentProvider extends ProviGenProvider {

	public SimpleContentProvider() throws InvalidContractException {
		super(ContractOne.class);
	}

	@Contract(version = 1)
	public interface ContractOne extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		String MY_INT = "int";

		@ContentUri
		Uri CONTENT_URI = Uri.parse("content://com.test.simple/table_name_simple");

	}

	@Contract(version = 2)
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
