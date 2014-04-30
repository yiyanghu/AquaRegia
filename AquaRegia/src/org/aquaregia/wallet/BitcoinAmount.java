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

	public enum B {
		COIN(8), MILLI(5), MICRO(2), SATOSHI(0);
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
	
	public BigDecimal scale(B base) {
		return stripTrailingZerosBugFix(new BigDecimal(this, base.scale));
	}
	
	public String coins() {
		return scale(B.COIN).toPlainString();
	}
	
	public String milliCoins() {
		return scale(B.MILLI).toPlainString();

	}
	
	public String microCoins() {
		return scale(B.MICRO).toPlainString();
	}
	
	/**
	 * Workaround for http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6480539
	 * @param d - decimal number
	 * @return trailing zero stripped fix
	 */
	private BigDecimal stripTrailingZerosBugFix(BigDecimal d) {
		if (d.compareTo(BigDecimal.ZERO) == 0)
			return BigDecimal.ZERO;
		else
			return d.stripTrailingZeros();
	}
}
