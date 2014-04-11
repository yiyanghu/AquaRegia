package org.aquaregia.wallet;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.Test;

/**
 * Testing BitcoinAmount
 * @author Stephen Halm
 */
public class TestBitcoinAmount {

	@Test
	public void testBitcoinAmountBigInteger() {
		String amount = "700000";
		BitcoinAmount s = new BitcoinAmount(new BigInteger(amount));
		
		assertEquals(amount, s.toString());
		assertEquals("0.007", s.coins());
		assertEquals("7", s.milliCoins());
		assertEquals("7000", s.microCoins());
	}

	
	@Test
	public void testBitcoinAmountBaseString() {
		BitcoinAmount b = new BitcoinAmount(BitcoinAmount.B.MICRO, "3.4");
		assertEquals("0.0000034", b.coins());
		assertEquals("340", b.toString());
	}

	@Test
	public void testCoins() {
		String amount = "8500000";
		BitcoinAmount s = new BitcoinAmount(new BigInteger(amount));
		assertEquals("0.085", s.coins());
	}

	@Test
	public void testMilliCoins() {
		String amount = "4100000";
		BitcoinAmount s = new BitcoinAmount(new BigInteger(amount));
		assertEquals("41", s.milliCoins());
	}

	@Test
	public void testMicroCoins() {
		String amount = "90";
		BitcoinAmount s = new BitcoinAmount(new BigInteger(amount));
		assertEquals("0.9", s.microCoins());
	}

	@Test
	public void testAdd() {
		String amount1 = "8500000";
		BitcoinAmount s1 = new BitcoinAmount(new BigInteger(amount1));
		String amount2 = "9500000";
		BitcoinAmount s2 = new BitcoinAmount(new BigInteger(amount2));
		assertEquals("0.18", new BitcoinAmount(s1.add(s2)).coins());
	}

}
