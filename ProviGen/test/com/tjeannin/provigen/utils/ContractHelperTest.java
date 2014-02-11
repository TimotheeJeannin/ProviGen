package com.tjeannin.provigen.utils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
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
		public String name() {
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
