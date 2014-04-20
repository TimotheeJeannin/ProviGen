package com.tjeannin.provigen.test.multiple;

import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.test.multiple.MultipleContractContentProvider.ContractOne;
import com.tjeannin.provigen.test.multiple.MultipleContractContentProvider.ContractTwo;

public class MultipleContractContentTest extends ExtendedProviderTestCase<MultipleContractContentProvider> {

    private MockContentResolver contentResolver;

    public MultipleContractContentTest() {
        super(MultipleContractContentProvider.class, "com.test.simple");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentResolver = getMockContentResolver();
    }

    public void testMultipleContractInsertDontOverlap() {

        // Check there is no data.
        assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
        assertEquals(0, getRowCount(ContractTwo.CONTENT_URI));

        contentResolver.insert(ContractTwo.CONTENT_URI, getContentValues(ContractTwo.class));

        assertEquals(0, getRowCount(ContractOne.CONTENT_URI));
        assertEquals(1, getRowCount(ContractTwo.CONTENT_URI));

        contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));

        assertEquals(1, getRowCount(ContractOne.CONTENT_URI));
        assertEquals(1, getRowCount(ContractTwo.CONTENT_URI));
    }

    public void testAddingAnotherContractLater() {

        contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
        assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

        resetContractClasses(new Class[]{ContractTwo.class, ContractOne.class});
        assertEquals(0, getRowCount(ContractTwo.CONTENT_URI));
        assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

        contentResolver.insert(ContractTwo.CONTENT_URI, getContentValues(ContractTwo.class));
        assertEquals(1, getRowCount(ContractTwo.CONTENT_URI));
        assertEquals(1, getRowCount(ContractOne.CONTENT_URI));

        contentResolver.insert(ContractOne.CONTENT_URI, getContentValues(ContractOne.class));
        assertEquals(1, getRowCount(ContractTwo.CONTENT_URI));
        assertEquals(2, getRowCount(ContractOne.CONTENT_URI));
    }
}
