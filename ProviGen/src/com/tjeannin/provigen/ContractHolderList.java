package com.tjeannin.provigen;

import android.net.Uri;

import java.util.ArrayList;

public class ContractHolderList extends ArrayList<ContractHolder> {

	private static final long serialVersionUID = 2449365634235483191L;

	/**
	 * @param uri The {@link Uri} to be matched.
	 * @return A {@link ContractHolder} matching the given {@link Uri}.
	 */
	public ContractHolder findMatching(Uri uri) {
		for (ContractHolder contract : this) {
			if (contract.getTable().equals(uri.getPathSegments().get(0))) {
				return contract;
			}
		}
		return null;
	}

}
