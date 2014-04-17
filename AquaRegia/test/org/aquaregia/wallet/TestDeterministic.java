package org.aquaregia.wallet;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

public class TestDeterministic {

	@Test
	public void testGetMasterPrivateKey() {
		String test1 = "This is something";
		String expected1 = "b409b15310c82f3144ec7d3807ed407eda676e9bffe64d58450cd69f59280639";
		getMasterPrivateKeyHelper(expected1, test1);

		String test2 = "Aqua Regia is awesome!";
		String expected2 = "fd72dcf2bcc215fb06cbf42f789c0b446e2f58ddfefdc2c5ed6426b62cc995c5";
		getMasterPrivateKeyHelper(expected2, test2);

	}
	
	@Test
	public void doubleSHA256 () {
		String in = "this is test";
		String exp = "714c9da279846da919ede4ba4b90f9928fb3b501db0443301de1a6013d3af7fd";
		byte[] dblhash = Deterministic.doubleSHA256(in.getBytes());
		String res = Deterministic.bytesToHex(dblhash);
		assertEquals(exp , res);
	}
	
	@Test
	public void testGetMasterPublicKey() {
		String test1 = "484ccb566edb66c65dd0fd2e4d90ef65";
		String expected1 = "484e42865b8e9a6ea8262fd1cde666b557393258ed598d842e563ad9e5e6c70a97e387eefdef123c1b8b4eb21fe210c6216ad7cc1e4186fbbba70f0e2c062c25";
		getMasterPublicKeyHelper(expected1,test1);	
	
	}
	
	@Test
	public void testGetPrivateKey() {
		String seed = "0123456789abcdef0123456789abcdef";
		String expected1 = "e2a8eb8c2c246060f5fba60243d926855c088d9ab2e5a86b0c7b658c6be2ca9d";
		getPrivateKeyHelper(expected1,seed);
		
	}

	private void getPrivateKeyHelper(String expected, String seed) {
		byte[] seedBytes = seed.getBytes();
		byte[] output;
		byte[] masterPrivateKey = Deterministic.getMasterPrivateKey(seedBytes);
		
		output = Deterministic.getPrivateKey(masterPrivateKey,3);
		String result = Deterministic.bytesToHex(output);
		assertEquals(expected,result);
		
	}

	public void getMasterPrivateKeyHelper(String expected, String input) {
		byte[] inputBytes = input.getBytes();
		byte[] output;

		output = Deterministic.getMasterPrivateKey(inputBytes);
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
