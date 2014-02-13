package com.tjeannin.provigen.test.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.mock.MockContentResolver;
import com.tjeannin.provigen.exceptions.InvalidContractException;
import com.tjeannin.provigen.test.ExtendedProviderTestCase;
import com.tjeannin.provigen.utils.IndexInformation;
import com.tjeannin.provigen.utils.IndexUtils;

import static com.tjeannin.provigen.test.index.IndexProvider.ChangeIndexColumnsContract1;
import static com.tjeannin.provigen.test.index.IndexProvider.ChangeIndexColumnsContract2;
import static com.tjeannin.provigen.test.index.IndexProvider.FailingDuplicateNameContract;
import static com.tjeannin.provigen.test.index.IndexProvider.FailingEmptyNameContract;
import static com.tjeannin.provigen.test.index.IndexProvider.INDEX_1;
import static com.tjeannin.provigen.test.index.IndexProvider.INDEX_2;
import static com.tjeannin.provigen.test.index.IndexProvider.INDEX_3;
import static com.tjeannin.provigen.test.index.IndexProvider.INDEX_4;
import static com.tjeannin.provigen.test.index.IndexProvider.INDEX_5;
import static com.tjeannin.provigen.test.index.IndexProvider.IndexContract;
import static com.tjeannin.provigen.test.index.IndexProvider.ModifyIndexContract1;
import static com.tjeannin.provigen.test.index.IndexProvider.ModifyIndexContract2;
import static com.tjeannin.provigen.test.index.IndexProvider.ModifyIndexContract3;
import static com.tjeannin.provigen.test.index.IndexProvider.PartialIndexContract;
import static com.tjeannin.provigen.test.index.IndexProvider.UniqueAndUniqueIndexContract;
import static com.tjeannin.provigen.test.index.IndexProvider.UpdateIndexContract1;
import static com.tjeannin.provigen.test.index.IndexProvider.UpdateIndexContract2;
import static com.tjeannin.provigen.test.index.IndexProvider.UpdateIndexContract3;
import static com.tjeannin.provigen.test.index.IndexProvider.WeightedIndexContract;

/**
 * Test class for testing the {@link com.tjeannin.provigen.annotation.Index} processing during
 * creation and upgrade of a table.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public class IndexTest extends ExtendedProviderTestCase<IndexProvider> {
    private static final String MSG_INDEX_NOT_IN_LIST = "expected index '%s' not in list";
    private static final String MSG_COUNT_OF_INDEX = "expected count of index does not match";
    private static final String MSG_COLUMN_COUNT = "column count in index does not match";
    private static final String MSG_COLUMN_POSITION = "column position in index is wrong";
    public static final String DUPLICATE_INDEX_READ_FROM_TABLE = "duplicate index read from table";

    private MockContentResolver contentResolver;

    public IndexTest() {
        super(IndexProvider.class, "com.test.simple");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        contentResolver = getMockContentResolver();
    }

    /**
     * Test the creation of the index during table creation.
     */
    public void testIndexCreation() {
        contentResolver.insert(IndexContract.CONTENT_URI, getContentValues(IndexContract.class));

        final List<String> indexNames = loadIndexNames(IndexContract.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 5, indexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), indexNames.contains(INDEX_1));
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_2), indexNames.contains(INDEX_2));
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_3), indexNames.contains(INDEX_3));
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_4), indexNames.contains(INDEX_4));
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_5), indexNames.contains(INDEX_5));
    }

    /**
     * Test the creation of an unique index on an unique column during table creation.
     */
    public void testUniqueAndIndex() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { UniqueAndUniqueIndexContract.class });
        contentResolver.insert(UniqueAndUniqueIndexContract.CONTENT_URI, getContentValues(UniqueAndUniqueIndexContract.class));

        final List<String> indexNames = loadIndexNames(UniqueAndUniqueIndexContract.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, indexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), indexNames.contains(INDEX_1));
    }

    /**
     * Test the creation of an partial index during table creation. This one should not fail if database engine
     * doesn't understand the partial index syntax.
     */
    public void testPartialIndex() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { PartialIndexContract.class });
        contentResolver.insert(PartialIndexContract.CONTENT_URI, getContentValues(PartialIndexContract.class));

        final List<String> indexNames = loadIndexNames(PartialIndexContract.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, indexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), indexNames.contains(INDEX_1));
    }

    /**
     * Test the creation of an combined index over two columns during table creation.
     */
    public void testWeightedIndex() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { WeightedIndexContract.class });
        contentResolver.insert(WeightedIndexContract.CONTENT_URI, getContentValues(WeightedIndexContract.class));

        final List<String> indexNames = loadIndexNames(WeightedIndexContract.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, indexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), indexNames.contains(INDEX_1));
        final List<String> columnNames = loadIndexColumns(WeightedIndexContract.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 2, columnNames.size());
        assertEquals(MSG_COLUMN_POSITION, WeightedIndexContract.COMBIND_INDEX_2, columnNames.get(0));
        assertEquals(MSG_COLUMN_POSITION, WeightedIndexContract.COMBIND_INDEX_1, columnNames.get(1));
    }

    /**
     * Test appending of an index on a already present table during upgrade.
     */
    public void testUpdateIndex() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { UpdateIndexContract1.class });
        contentResolver.insert(UpdateIndexContract1.CONTENT_URI, getContentValues(UpdateIndexContract1.class));

        final List<String> indexNames = loadIndexNames(UpdateIndexContract1.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, indexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), indexNames.contains(INDEX_1));
        final List<String> columnNames = loadIndexColumns(UpdateIndexContract1.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 1, columnNames.size());
        assertEquals(MSG_COLUMN_POSITION, UpdateIndexContract1.FIELD_1, columnNames.get(0));

        getProvider().setContractClasses(new Class[] { UpdateIndexContract2.class });
        contentResolver.insert(UpdateIndexContract2.CONTENT_URI, getContentValues(UpdateIndexContract2.class));
        final List<String> newIndexNames = loadIndexNames(UpdateIndexContract2.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 2, newIndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), newIndexNames.contains(INDEX_1));
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_2), newIndexNames.contains(INDEX_2));
        final List<String> newColumnNamesIdx9 = loadIndexColumns(UpdateIndexContract2.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 1, newColumnNamesIdx9.size());
        assertEquals(MSG_COLUMN_POSITION, UpdateIndexContract2.FIELD_1, newColumnNamesIdx9.get(0));
        final List<String> newColumnNamesIdx10 = loadIndexColumns(UpdateIndexContract2.CONTENT_URI, INDEX_2);
        assertEquals(MSG_COLUMN_COUNT, 1, newColumnNamesIdx10.size());
        assertEquals(MSG_COLUMN_POSITION, UpdateIndexContract2.FIELD_2, newColumnNamesIdx10.get(0));
    }

    /**
     * Test removing of an index on a already present table during upgrade.
     */
    public void testDowngradeIndex() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { UpdateIndexContract2.class });
        contentResolver.insert(UpdateIndexContract2.CONTENT_URI, getContentValues(UpdateIndexContract2.class));
        final List<String> newIndexNames = loadIndexNames(UpdateIndexContract2.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 2, newIndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), newIndexNames.contains(INDEX_1));
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_2), newIndexNames.contains(INDEX_2));
        final List<String> newColumnNamesIdx9 = loadIndexColumns(UpdateIndexContract2.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 1, newColumnNamesIdx9.size());
        assertEquals(MSG_COLUMN_POSITION, UpdateIndexContract2.FIELD_1, newColumnNamesIdx9.get(0));
        final List<String> newColumnNamesIdx10 = loadIndexColumns(UpdateIndexContract2.CONTENT_URI, INDEX_2);
        assertEquals(MSG_COLUMN_COUNT, 1, newColumnNamesIdx10.size());
        assertEquals(MSG_COLUMN_POSITION, UpdateIndexContract2.FIELD_2, newColumnNamesIdx10.get(0));

        getProvider().setContractClasses(new Class[] { UpdateIndexContract3.class });
        contentResolver.insert(UpdateIndexContract3.CONTENT_URI, getContentValues(UpdateIndexContract3.class));
        final List<String> indexNames = loadIndexNames(UpdateIndexContract3.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, indexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), indexNames.contains(INDEX_1));
        final List<String> columnNames = loadIndexColumns(UpdateIndexContract3.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 1, columnNames.size());
        assertEquals(MSG_COLUMN_POSITION, UpdateIndexContract3.FIELD_1, columnNames.get(0));
    }

    /**
     * Test modification of an index on a already present table during upgrade. First a table with one index
     * is created, first upgrade adds a new index, second upgrade removes an index.
     */
    public void testModifyIndex() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { ModifyIndexContract1.class });
        contentResolver.insert(ModifyIndexContract1.CONTENT_URI, getContentValues(ModifyIndexContract1.class));
        final List<String> contract1IndexNames = loadIndexNames(ModifyIndexContract1.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, contract1IndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), contract1IndexNames.contains(INDEX_1));
        final List<String> contract1Idx1Columns = loadIndexColumns(ModifyIndexContract1.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 1, contract1Idx1Columns.size());
        assertEquals(MSG_COLUMN_POSITION, ModifyIndexContract1.FIELD_1, contract1Idx1Columns.get(0));

        getProvider().setContractClasses(new Class[] { ModifyIndexContract2.class });
        contentResolver.insert(ModifyIndexContract2.CONTENT_URI, getContentValues(ModifyIndexContract2.class));
        final List<String> contract2IndexNames = loadIndexNames(ModifyIndexContract2.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, contract2IndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), contract2IndexNames.contains(INDEX_1));
        final List<String> contract2Idx1Columns = loadIndexColumns(ModifyIndexContract2.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 2, contract2Idx1Columns.size());
        assertEquals(MSG_COLUMN_POSITION, ModifyIndexContract2.FIELD_1, contract2Idx1Columns.get(0));
        assertEquals(MSG_COLUMN_POSITION, ModifyIndexContract2.FIELD_2, contract2Idx1Columns.get(1));

        getProvider().setContractClasses(new Class[] { ModifyIndexContract3.class });
        contentResolver.insert(ModifyIndexContract3.CONTENT_URI, getContentValues(ModifyIndexContract3.class));
        final List<String> contract3IndexNames = loadIndexNames(ModifyIndexContract3.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, contract3IndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), contract3IndexNames.contains(INDEX_1));
        final List<String> contract3Idx1Columns = loadIndexColumns(ModifyIndexContract3.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 1, contract3Idx1Columns.size());
        assertEquals(MSG_COLUMN_POSITION, ModifyIndexContract3.FIELD_1, contract3Idx1Columns.get(0));
    }

    /**
     * Test modification of an combined index on a already present table during upgrade. First a table with
     * a combined index is created, first upgrade changes the order of the columns in the index.
     */
    public void testChangeIndexColumnOrder() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { ChangeIndexColumnsContract1.class });
        contentResolver.insert(ChangeIndexColumnsContract1.CONTENT_URI, getContentValues(ChangeIndexColumnsContract1.class));
        final List<String> contract1IndexNames = loadIndexNames(ChangeIndexColumnsContract1.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, contract1IndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), contract1IndexNames.contains(INDEX_1));
        final List<String> contract1Idx1Columns = loadIndexColumns(ChangeIndexColumnsContract1.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 2, contract1Idx1Columns.size());
        assertEquals(MSG_COLUMN_POSITION, ChangeIndexColumnsContract1.FIELD_1, contract1Idx1Columns.get(1));
        assertEquals(MSG_COLUMN_POSITION, ChangeIndexColumnsContract1.FIELD_2, contract1Idx1Columns.get(0));

        getProvider().setContractClasses(new Class[] { ChangeIndexColumnsContract2.class });
        contentResolver.insert(ChangeIndexColumnsContract2.CONTENT_URI, getContentValues(ChangeIndexColumnsContract2.class));
        final List<String> contract2IndexNames = loadIndexNames(ChangeIndexColumnsContract2.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 1, contract2IndexNames.size());
        assertTrue(String.format(MSG_INDEX_NOT_IN_LIST, INDEX_1), contract2IndexNames.contains(INDEX_1));
        final List<String> contract2Idx1Columns = loadIndexColumns(ChangeIndexColumnsContract2.CONTENT_URI, INDEX_1);
        assertEquals(MSG_COLUMN_COUNT, 2, contract2Idx1Columns.size());
        assertEquals(MSG_COLUMN_POSITION, ChangeIndexColumnsContract2.FIELD_1, contract2Idx1Columns.get(0));
        assertEquals(MSG_COLUMN_POSITION, ChangeIndexColumnsContract2.FIELD_2, contract2Idx1Columns.get(1));
    }

    public void testDuplicateName() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { FailingDuplicateNameContract.class });
        contentResolver.insert(FailingDuplicateNameContract.CONTENT_URI, getContentValues(FailingDuplicateNameContract.class));
        final List<String> contract2IndexNames = loadIndexNames(FailingDuplicateNameContract.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 0, contract2IndexNames.size());
    }

    public void testEmptyName() throws InvalidContractException {
        getProvider().setContractClasses(new Class[] { FailingEmptyNameContract.class });
        contentResolver.insert(FailingEmptyNameContract.CONTENT_URI, getContentValues(FailingEmptyNameContract.class));
        final List<String> contract2IndexNames = loadIndexNames(FailingEmptyNameContract.CONTENT_URI);
        assertEquals(MSG_COUNT_OF_INDEX, 0, contract2IndexNames.size());
    }

    private List<String> loadIndexColumns(final Uri uri, final String indexName) {
        final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
        final List<IndexInformation> information = IndexUtils.getIndexInformationForTable(sqLiteDatabase, uri.getLastPathSegment());
        assertEquals(DUPLICATE_INDEX_READ_FROM_TABLE, information.size(), new HashSet<IndexInformation>(information).size());
        for (final IndexInformation indexInformation : information) {
            if (indexInformation.getIndexName().equals(indexName)) {
                return indexInformation.getIndexColumnNames();
            }
        }
        return Collections.emptyList();
    }

    private List<String> loadIndexNames(final Uri uri) {
        final SQLiteDatabase sqLiteDatabase = getMockContext().openOrCreateDatabase("ProviGenDatabase", Context.MODE_PRIVATE, null);
        final List<IndexInformation> information = IndexUtils.getIndexInformationForTable(sqLiteDatabase, uri.getLastPathSegment());
        assertEquals(DUPLICATE_INDEX_READ_FROM_TABLE, information.size(), new HashSet<IndexInformation>(information).size());
        final List<String> result = new ArrayList<String>(information.size());
        for (final IndexInformation indexInformation : information) {
            result.add(indexInformation.getIndexName());
        }
        return result;
    }
}
