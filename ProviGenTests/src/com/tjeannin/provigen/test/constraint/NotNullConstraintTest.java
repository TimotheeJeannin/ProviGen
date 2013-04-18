package com.tjeannin.provigen.test.constraint;

import android.content.ContentValues;

import com.tjeannin.provigen.InvalidContractException;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.constraint.NotNullConstraintProvider.ContractClass;

public class NotNullConstraintTest extends ExtendedProviderTestCase<UniqueConstraintProvider> {

	public NotNullConstraintTest() {
		super(UniqueConstraintProvider.class, "com.test.simple");
	}

	public void testNotNullAnnotation() throws InvalidContractException {

		ContentValues contentValuesWithNull = getContentValues(ContractClass.class);
		contentValuesWithNull.putNull(ContractClass.AN_INT);

		ContentValues normalContentValues = getContentValues(ContractClass.class);

		assertEquals(0, getRowCount(ContractClass.CONTENT_URI));
		getMockContentResolver().insert(ContractClass.CONTENT_URI, contentValuesWithNull);
		assertEquals(0, getRowCount(ContractClass.CONTENT_URI));
		getMockContentResolver().insert(ContractClass.CONTENT_URI, normalContentValues);
		assertEquals(1, getRowCount(ContractClass.CONTENT_URI));
		getMockContentResolver().insert(ContractClass.CONTENT_URI, contentValuesWithNull);
		assertEquals(1, getRowCount(ContractClass.CONTENT_URI));
	}
}
