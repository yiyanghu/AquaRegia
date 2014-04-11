package org.aquaregia.wallet.addressbook;

import com.google.bitcoin.core.Address;

/**
 * Contains relevant info for an interface to describe an address
 * 
 * TODO add labels/description if possible
 * @author Stephen Halm
 */
public class AddressBookEntry {

	private Address address;
	
	public AddressBookEntry(Address address) {
		this.address = address;
	}
	
	public Address getAddress() {
		return address;
	}
}
