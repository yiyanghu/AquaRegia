package org.aquaregia.wallet.addressbook;

import java.util.ArrayList;
import java.util.List;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;

/**
 * Presents addresses in a useful way for building an interface.
 * 
 * Useful for personal address lists and contacts
 * @author Stephen Halm
 */
public class AddressBook extends ArrayList<AddressBookEntry> {
	
	/**
	 * Initial version
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Generate from private keys
	 * @param keys
	 * @param params
	 */
	public AddressBook(List<ECKey> keys, NetworkParameters params) {
		super();
		for (ECKey key : keys) {
			this.add(new AddressBookEntry(key.toAddress(params)));
		}
	}
}
