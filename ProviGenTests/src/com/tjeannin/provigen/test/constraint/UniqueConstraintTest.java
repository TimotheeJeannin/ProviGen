package com.tjeannin.provigen.test.constraint;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.constraint.UniqueConstraintProvider.ContractClass;

public class UniqueConstraintTest extends ExtendedProviderTestCase<UniqueConstraintProvider> {

	public UniqueConstraintTest() {
		super(UniqueConstraintProvider.class, "com.test.simple");
	}

	public void testUniqueAnnotation() throws InvalidContractException {

		getMockContentResolver().insert(ContractClass.CONTENT_URI, getContentValues(ContractClass.class));
		getMockContentResolver().insert(ContractClass.CONTENT_URI, getContentValues(ContractClass.class));
		getMockContentResolver().insert(ContractClass.CONTENT_URI, getContentValues(ContractClass.class));

		assertEquals(1, getRowCount(ContractClass.CONTENT_URI));
	}
}
