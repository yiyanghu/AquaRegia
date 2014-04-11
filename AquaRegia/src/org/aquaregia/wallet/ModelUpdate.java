package org.aquaregia.wallet;

/**
 * Provides message types for Observer updates
 * Where o is the update object:
 * o[0] = ModelUpdate type
 * o[1]... = type specific
 * @author Stephen Halm
 */
public enum ModelUpdate {
	/** Wait for this show UI */
	SHOW,
	/**
	 * o[1] = BigInteger balance
	 */
	BALANCE,
	/**
	 * TODO spec
	 */
	HISTORY,
	/**
	 * TODO spec
	 */
	OWNED_ADDRESSES,
	/**
	 * o[1] = double USD per BTC (100m satoshi)
	 */
	EXCHANGE_RATE
}
