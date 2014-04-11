package org.aquaregia.wallet;

/**
 * Provides message types for Observer updates
 * Where o is the update object:
 * o[0] = ModelUpdate type
 * o[1]... = type specific
 * @author Stephen Halm
 */
public enum ModelUpdate {
	/** Wait for this to show UI */
	SHOW,
	/**
	 * o[1] = BitcoinAmount balance
	 */
	BALANCE,
	/**
	 * o[1] = TransactionHistory history
	 */
	HISTORY,
	/**
	 * o[1] = AddressBook addresses
	 */
	OWNED_ADDRESSES,
	/**
	 * o[1] = double USD per BTC (100m satoshi)
	 */
	EXCHANGE_RATE
}
