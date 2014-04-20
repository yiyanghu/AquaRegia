package org.aquaregia.wallet;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.Test;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.params.MainNetParams;

public class TestDeterministic {

	@Test
	public void testRandomSeed() {
		String seed = Deterministic.randomSeed();
		assertTrue(seed.matches("^[0-9a-f]{32}$"));
	}
	
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
		getPrivateKeyHelper(expected1,seed, 3);
		
	}

	
	@Test
	public void testGetPublicKey() {
		String masterPublicKeyHex = "7a4a6acb200cb895e5c518fe1d5c094de578cf4e86daf61e4e283a22456551cf3a25f28e9ed6316de40e5cb6e33313e80898e1f3e0011e84dcaeb6c6e260328a";
		String expectedPublicKey = "b0d852dd745291d16cc2938636a92ec363e10fcfcea2cf772035cf5d639bfa7f7ec76f2dae08db6aade0b11932f676ff811378eea9dee62c7767915b2fdad351";
		getPublicKeyHelper(expectedPublicKey, masterPublicKeyHex, 4);
	}
	
	@Test
	public void keyAdditionProperty() {
		String seed = "0123456789abcdef0123456789abcdef";
		int addrNum = 4;
		byte[] masterPrivateKey = Deterministic.getMasterPrivateKey(seed.getBytes());
		byte[] masterPublicKey = Deterministic.privateToPublic(masterPrivateKey);
		
		// work from master private key to Nth public key
		byte[] nthPrivateKey = Deterministic.getPrivateKey(masterPrivateKey, addrNum);
		byte[] nthPublicKeyM1 = Deterministic.privateToPublic(nthPrivateKey);
		
		// work from master public key to get Nth public key
		byte[] nthPublicKeyM2 = Deterministic.getPublicKey(masterPublicKey, addrNum);
		
		assertTrue(Arrays.equals(nthPublicKeyM1, nthPublicKeyM2));
	}
	
	private void getPublicKeyHelper(String expected, String masterPublicKeyHex, int n) {
		byte[] masterPublicKey = Deterministic.hexStringToByteArray(masterPublicKeyHex);
		byte[] publicKey = Deterministic.getPublicKey(masterPublicKey, n);
		String result = Deterministic.bytesToHex(publicKey);
		assertEquals(expected, result);
	}
	
	private void getPrivateKeyHelper(String expected, String seed, int n) {
		byte[] seedBytes = seed.getBytes();
		byte[] output;
		byte[] masterPrivateKey = Deterministic.getMasterPrivateKey(seedBytes);
		
		output = Deterministic.getPrivateKey(masterPrivateKey, n);
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
