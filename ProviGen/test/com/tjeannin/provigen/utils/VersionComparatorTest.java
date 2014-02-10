package com.tjeannin.provigen.utils;

import java.util.Comparator;

import org.testng.Assert;
import org.testng.annotations.Test;

public class VersionComparatorTest {
	private static final Comparator<String> VERSION_COMPARATOR = new VersionComparator();

	@Test
	public void simpleCompare() {
		Assert.assertTrue(VERSION_COMPARATOR.compare("1", "2") < 0, "1 < 2");
		Assert.assertTrue(VERSION_COMPARATOR.compare("2", "2") == 0, "2 = 2");
		Assert.assertTrue(VERSION_COMPARATOR.compare("2", "1") > 0, "2 > 1");
	}

	@Test
	public void smallerCompare() {
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0", "2.0") < 0, "1.0 < 2.0");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0", "1.1") < 0, "1.0 < 1.1");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0", "1.0.1") < 0, "1.0 < 1.0.1");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0.1", "1.0.2") < 0, "1.0.1 < 1.0.2");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0.1", "1.1") < 0, "1.0.1 < 1.1");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0", "1.0.0") < 0, "1.0 < 1.0.0");
	}

	@Test
	public void greaterCompare() {
		Assert.assertTrue(VERSION_COMPARATOR.compare("2.0", "1.0") > 0, "2.0 > 1.0");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.1", "1.0") > 0, "1.1 > 1.0");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0.1", "1.0") > 0, "1.0.1 > 1.0");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.0.2", "1.0.1") > 0, "1.0.2 > 1.0.1");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.1", "1.0.1") > 0, "1.1 > 1.0.1");
		Assert.assertTrue(VERSION_COMPARATOR.compare("1.1.0", "1.1") > 0, "1.1.0 > 1.1");
	}

	@Test
	public void equalCompare() {
		Assert.assertTrue(VERSION_COMPARATOR.compare("2.0", "2.0") == 0, "2.0 = 2.0");
		Assert.assertTrue(VERSION_COMPARATOR.compare("2.0", "2.0.0") != 0, "2.0 != 2.0.0");
	}
}
