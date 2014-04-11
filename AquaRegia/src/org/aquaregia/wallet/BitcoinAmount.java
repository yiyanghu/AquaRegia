package org.aquaregia.wallet;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Flexible Bitcoin amount representation
 * The intrinsic BigInteger properties are about the amount in satoshi
 * @author Stephen Halm
 */
public class BitcoinAmount extends BigInteger {
	
	/**
	 * Initial version
	 */
	private static final long serialVersionUID = 1L;

	enum B {
		COINS(8), MILLI(5), MICRO(2), SATOSHI(0);
		public final int scale;
		B(int s) {
			scale = s;
		}
	}
	
	public BitcoinAmount(BigInteger i) {
		super(i.toString());
	}
	
	public BitcoinAmount(B base, String s) {
		super(new BigDecimal(s).setScale(base.scale).unscaledValue().toString());
	}
	
	public BigDecimal coins() {
		return new BigDecimal(this, 8).stripTrailingZeros();
	}
	
	public BigDecimal milliCoins() {
		return new BigDecimal(this, 5).stripTrailingZeros();
	}
	
	public BigDecimal microCoins() {
		return new BigDecimal(this, 2).stripTrailingZeros();
	}
}
