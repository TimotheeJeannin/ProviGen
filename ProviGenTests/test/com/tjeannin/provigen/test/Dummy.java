package com.tjeannin.provigen.test;

import junit.framework.TestCase;

/**
 * This class must be present to get emma4it report working when running a mvn site:site, because it tries to
 * do lookup in target/test-classes. When this directory is not present the plugin fails to execute and breaks the build.
 */
public class Dummy extends TestCase {
	public void testCompile() {
		assertTrue(true);
	}
}
