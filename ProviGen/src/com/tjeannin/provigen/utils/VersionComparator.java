package com.tjeannin.provigen.utils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * Comparator that compares two version strings.
 * For example {@code 2.0 < 2.0.1}, but {@code 2.0 <> 2.0.0}, it is {@code 2.0 < 2.0.0}.
 *
 * @author Michael Cramer <michael@bigmichi1.de>
 * @since 1.6
 */
public final class VersionComparator implements Comparator<String>, Serializable {
	/**
	 * Compare tow version strings.
	 */
	public static final Comparator<String> VERSION_COMPARATOR = new VersionComparator();

	private static final Pattern DOT = Pattern.compile("\\.");

	@Override
	public int compare(final String o1, final String o2) {
		final String[] vals1 = DOT.split(o1);
		final String[] vals2 = DOT.split(o2);
		int i = 0;
		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
			i++;
		}
		if (i < vals1.length && i < vals2.length) {
			final int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
			return Integer.signum(diff);
		}
		return Integer.signum(vals1.length - vals2.length);
	}
}
