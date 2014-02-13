package com.tjeannin.provigen.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.tjeannin.provigen.annotation.Index;
import com.tjeannin.provigen.annotation.IndexType;
import com.tjeannin.provigen.exceptions.IndexException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ContractHelperTest {

	@Test
	public void newIndex() throws IndexException {
		final Collection<IndexInformation> list = new ArrayList<IndexInformation>(1);
		final Index testee = new Testee("", IndexType.INDEX, 0);
		ContractHelper.addIndexInformation(list, "COL_1", testee);
		Assert.assertEquals(list.size(), 1);
		Assert.assertEquals(list.iterator().next().getIndexColumnNames().size(), 1);
		Assert.assertEquals(list.iterator().next().getIndexColumnNames().iterator().next(), "COL_1");
	}

	@Test
	public void newTwoUnnamedIndex() throws IndexException {
		final Collection<IndexInformation> list = new ArrayList<IndexInformation>(2);
		final Index testee1 = new Testee("", IndexType.INDEX, 0);
		final Index testee2 = new Testee("", IndexType.INDEX, 0);
		ContractHelper.addIndexInformation(list, "COL_1", testee1);
		ContractHelper.addIndexInformation(list, "COL_2", testee2);
		Assert.assertEquals(list.size(), 2);
		final Iterator<IndexInformation> iterator = list.iterator();
		final List<String> indexColumns1 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns1.size(), 1);
		Assert.assertEquals(indexColumns1.size(), 1);
		Assert.assertEquals(indexColumns1.iterator().next(), "COL_1");
		final List<String> indexColumns2 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns2.size(), 1);
		Assert.assertEquals(indexColumns2.iterator().next(), "COL_2");
	}

	@Test
	public void newTwoWeightedUnnamedIndex() throws IndexException {
		final Collection<IndexInformation> list = new ArrayList<IndexInformation>(2);
		final Index testee1 = new Testee("", IndexType.INDEX, 0);
		final Index testee2 = new Testee("", IndexType.INDEX, 2);
		ContractHelper.addIndexInformation(list, "COL_1", testee1);
		ContractHelper.addIndexInformation(list, "COL_2", testee2);
		Assert.assertEquals(list.size(), 2);
		final Iterator<IndexInformation> iterator = list.iterator();
		final List<String> indexColumns1 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns1.size(), 1);
		Assert.assertEquals(indexColumns1.size(), 1);
		Assert.assertEquals(indexColumns1.iterator().next(), "COL_1");
		final List<String> indexColumns2 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns2.size(), 1);
		Assert.assertEquals(indexColumns2.iterator().next(), "COL_2");
	}

	@Test
	public void newTwoWeightedNamedIndex() throws IndexException {
		final Collection<IndexInformation> list = new ArrayList<IndexInformation>(2);
		final Index testee1 = new Testee("IDX_1", IndexType.INDEX, 2);
		final Index testee2 = new Testee("IDX_1", IndexType.INDEX, 0);
		ContractHelper.addIndexInformation(list, "COL_1", testee1);
		ContractHelper.addIndexInformation(list, "COL_2", testee2);
		Assert.assertEquals(list.size(), 1);
		final Iterator<IndexInformation> iterator = list.iterator();
		final List<String> indexColumns1 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns1.size(), 2);
		final Iterator<String> indexColumnIterator1 = indexColumns1.iterator();
		Assert.assertEquals(indexColumnIterator1.next(), "COL_2");
		Assert.assertEquals(indexColumnIterator1.next(), "COL_1");
	}

	@Test(expectedExceptions = IndexException.class)
	public void newDifferentTypeSameNamedIndex() throws IndexException {
		final Collection<IndexInformation> list = new ArrayList<IndexInformation>(2);
		final Index testee1 = new Testee("IDX_1", IndexType.INDEX, 2);
		final Index testee2 = new Testee("IDX_1", IndexType.UNIQUE, 0);
		ContractHelper.addIndexInformation(list, "COL_1", testee1);
		ContractHelper.addIndexInformation(list, "COL_2", testee2);
	}

	@Test
	public void newNamedAndUnnamedIndex() throws IndexException {
		final Collection<IndexInformation> list = new ArrayList<IndexInformation>(3);
		final Index testee1 = new Testee("IDX_1", IndexType.INDEX, 2);
		final Index testee2 = new Testee("IDX_1", IndexType.INDEX, 0);
		final Index testee3 = new Testee("", IndexType.UNIQUE, 1);
		ContractHelper.addIndexInformation(list, "COL_1", testee1);
		ContractHelper.addIndexInformation(list, "COL_2", testee2);
		ContractHelper.addIndexInformation(list, "COL_3", testee3);
		Assert.assertEquals(list.size(), 2);
		final Iterator<IndexInformation> iterator = list.iterator();
		final List<String> indexColumns1 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns1.size(), 2);
		final Iterator<String> indexColumnIterator1 = indexColumns1.iterator();
		Assert.assertEquals(indexColumnIterator1.next(), "COL_2");
		Assert.assertEquals(indexColumnIterator1.next(), "COL_1");
		final List<String> indexColumns2 = iterator.next().getIndexColumnNames();
		Assert.assertEquals(indexColumns2.size(), 1);
		final Iterator<String> indexColumnIterator2 = indexColumns2.iterator();
		Assert.assertEquals(indexColumnIterator2.next(), "COL_3");
	}

	@Test
	public void compareIndexInformation() throws IndexException {
		final IndexInformation idx1 = new IndexInformation("IDX_1", IndexType.INDEX);
		final IndexInformation idx2 = new IndexInformation("IDX_1", IndexType.INDEX);
		Assert.assertEquals(idx1, idx2);

		final IndexInformation idx3 = new IndexInformation("IDX_1", IndexType.INDEX);
		final IndexInformation idx4 = new IndexInformation("IDX_2", IndexType.INDEX);
		Assert.assertNotEquals(idx3, idx4);

		final IndexInformation idx5 = new IndexInformation("IDX_1", IndexType.INDEX);
		final IndexInformation idx6 = new IndexInformation("IDX_1", IndexType.UNIQUE);
		Assert.assertNotEquals(idx5, idx6);

		final IndexInformation idx7 = new IndexInformation("IDX_1", IndexType.INDEX);
		final IndexInformation idx8 = new IndexInformation("IDX_2", IndexType.UNIQUE);
		Assert.assertNotEquals(idx7, idx8);

		final IndexInformation idx9 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx9.addNewIndexColumn("COL_1", 0, "");
		final IndexInformation idx10 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx10.addNewIndexColumn("COL_1", 0, "");
		Assert.assertEquals(idx9, idx10);

		final IndexInformation idx11 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx11.addNewIndexColumn("COL_1", 0, "");
		final IndexInformation idx12 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx12.addNewIndexColumn("COL_1", 1, "");
		Assert.assertEquals(idx11, idx12);

		final IndexInformation idx13 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx13.addNewIndexColumn("COL_1", 1, "");
		final IndexInformation idx14 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx14.addNewIndexColumn("COL_1", 0, "");
		Assert.assertEquals(idx13, idx14);

		final IndexInformation idx15 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx15.addNewIndexColumn("COL_1", 0, "");
		final IndexInformation idx16 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx16.addNewIndexColumn("COL_2", 0, "");
		Assert.assertNotEquals(idx15, idx16);

		final IndexInformation idx17 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx17.addNewIndexColumn("COL_1", 0, "");
		idx17.addNewIndexColumn("COL_2", 1, "");
		final IndexInformation idx18 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx18.addNewIndexColumn("COL_1", 0, "");
		idx18.addNewIndexColumn("COL_2", 1, "");
		Assert.assertEquals(idx17, idx18);

		final IndexInformation idx19 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx19.addNewIndexColumn("COL_1", 0, "");
		idx19.addNewIndexColumn("COL_2", 1, "");
		final IndexInformation idx20 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx20.addNewIndexColumn("COL_1", 6, "");
		idx20.addNewIndexColumn("COL_2", 9, "");
		Assert.assertEquals(idx19, idx20);

		final IndexInformation idx21 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx21.addNewIndexColumn("COL_1", 0, "");
		idx21.addNewIndexColumn("COL_2", 1, "");
		final IndexInformation idx22 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx22.addNewIndexColumn("COL_1", 9, "");
		idx22.addNewIndexColumn("COL_2", 1, "");
		Assert.assertNotEquals(idx21, idx22);

		final IndexInformation idx23 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx23.addNewIndexColumn("COL_1", 0, "");
		idx23.addNewIndexColumn("COL_2", 1, "");
		final IndexInformation idx24 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx24.addNewIndexColumn("COL_1", 9, "");
		idx24.addNewIndexColumn("COL_3", 1, "");
		Assert.assertNotEquals(idx23, idx24);

		final IndexInformation idx25 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx25.addNewIndexColumn("COL_1", 0, "");
		idx25.addNewIndexColumn("COL_2", 1, "");
		final IndexInformation idx26 = new IndexInformation("IDX_1", IndexType.INDEX);
		idx26.addNewIndexColumn("COL_1", 9, "");
		Assert.assertNotEquals(idx25, idx26);

		final IndexInformation idx27 = new IndexInformation("", IndexType.INDEX);
		final IndexInformation idx28 = new IndexInformation("", IndexType.INDEX);
		final Collection<IndexInformation> set = new HashSet<IndexInformation>(2);
		set.add(idx27);
		set.add(idx28);
        Assert.assertEquals(idx27, idx28);
        Assert.assertEquals(set.size(), 1);
    }
	private static class Testee implements Index {
		private final IndexType m_type;
		private final String m_name;
		private final int m_position;

		private Testee(final String name, final IndexType type, final int position) {
			m_type = type;
			m_name = name;
			m_position = position;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return Index.class;
		}

		@Override
		public IndexType type() {
			return m_type;
		}

		@Override
		public String value() {
			return m_name;
		}

		@Override
		public int position() {
			return m_position;
		}

		@Override
		public String expr() {
			return "";
		}

		@Override
		public String toString() {
			return "Testee{" +
					"name='" + m_name + '\'' +
					", position=" + m_position +
					", type=" + m_type +
					'}';
		}
	}
}
