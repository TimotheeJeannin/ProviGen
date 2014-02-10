package com.tjeannin.provigen.test.contract;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.tjeannin.provigen.exceptions.InvalidContractException;
import com.tjeannin.provigen.ProviGenBaseContract;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.ContentUri;
import com.tjeannin.provigen.annotation.Contract;
import com.tjeannin.provigen.annotation.Id;

public class InvalidContractsTest extends AndroidTestCase {

	public static interface ContractWithMissingContractAnnotation extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");
	}

	@Contract(version = 1)
	public static interface ContractWithMissingContentUri extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";
	}

	@Contract(version = 1)
	public static interface ContractWithMultipleContentUri extends ProviGenBaseContract {

		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI_1 = Uri.parse("content://com.test.simple/unique_constraint_test_1");

		@ContentUri
		public static final Uri CONTENT_URI_2 = Uri.parse("content://com.test.simple/unique_constraint_test_2");
	}

	@Contract(version = 1)
	public static interface ContractWithSeveralId extends ProviGenBaseContract {

		@Id
		@Column(Type.INTEGER)
		public static final String AN_INT = "another_int";

		@ContentUri
		public static final Uri CONTENT_URI = Uri.parse("content://com.test.simple/unique_constraint_test");
	}

	@SuppressWarnings("rawtypes")
	public static final Class[] INVALID_CONTRACTS = new Class[] {
	  ContractWithMissingContractAnnotation.class,
	  ContractWithMissingContentUri.class,
	  ContractWithMultipleContentUri.class,
	  ContractWithSeveralId.class
	};

	@SuppressWarnings("rawtypes")
	public void testInvalidContracts() {

		for (Class clazz : INVALID_CONTRACTS) {
			try {
				Class obj = Class.forName("com.tjeannin.provigen.ContractHolder");
				obj.getConstructors()[0].newInstance(clazz);
				fail("No exception thrown.");
			} catch (Exception exception) {
				// We need to get the original cause exception as the reflection API wraps exceptions.
				assertTrue(exception.getCause() instanceof InvalidContractException);
			}
		}
	}
}
