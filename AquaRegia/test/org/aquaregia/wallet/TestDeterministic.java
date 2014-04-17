package org.aquaregia.wallet;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class TestDeterministic {

	@Test
	public void testBin_slowsha() {
		String test1 = "This is something";
		String expected1 = "b409b15310c82f3144ec7d3807ed407eda676e9bffe64d58450cd69f59280639";
		slowshaHelper(expected1, test1);

		String test2 = "Aqua Regia is awesome!";
		String expected2 = "fd72dcf2bcc215fb06cbf42f789c0b446e2f58ddfefdc2c5ed6426b62cc995c5";
		slowshaHelper(expected2, test2);

	}
	
	@Test
	public void testGetMasterPublicKey() {
		String test1 = "484ccb566edb66c65dd0fd2e4d90ef65";
		String expected1 = "04484e42865b8e9a6ea8262fd1cde666b557393258ed598d842e563ad9e5e6c70a97e387eefdef123c1b8b4eb21fe210c6216ad7cc1e4186fbbba70f0e2c062c25";
		getMasterPublicKeyHelper(expected1,test1);	
	
	}

	public void slowshaHelper(String expected, String input) {
		byte[] inputBytes = input.getBytes();
		byte[] output;

		output = Deterministic.bin_slowsha(inputBytes);
		String result = Deterministic.bytesToHex(output);
		assertEquals(expected, result);

	}
	
	public void getMasterPublicKeyHelper(String expected, String input) {
		byte[] inputBytes = input.getBytes();
		byte[] output;
		
		output = Deterministic.getMasterPublicKey(inputBytes);
		String result = Deterministic.bytesToHex(output);
		assertEquals(expected,result);
		
	}
	
	

}
