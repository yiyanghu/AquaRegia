package org.aquaregia.wallet;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestMnemonic {

	@Test
	public void testEncode() {
		String seed = "31c8bb7c4d660070584638a217a9db3a";
		String expectedEncode = "pathetic disguise burn fact gun forth control beside sneak game forward clothes";
		assertEquals(expectedEncode, Mnemonic.encode(seed));
	}

	@Test
	public void testDecode() {
		String encoded = "cheap hatred important monkey wait answer nose radio closet double belly wash";
		String expectedSeed = "9d1ae8702211db98f97ed93957f3b6ed";
		assertEquals(expectedSeed, Mnemonic.decode(encoded));
	}
	
	@Test
	public void testSymmetryProperty() {
		String seed = "274073afb98ce93fb9381da9ef9fd837";
		assertEquals(seed, Mnemonic.decode(Mnemonic.encode(seed)));
	}

}
