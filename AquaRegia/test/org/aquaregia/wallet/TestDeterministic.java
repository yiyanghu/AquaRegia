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
		slowshaHelper(expected1,test1);
		
		String test2 = "Aqua Regia is awesome!";
		String expected2 = "fd72dcf2bcc215fb06cbf42f789c0b446e2f58ddfefdc2c5ed6426b62cc995c5";
		slowshaHelper(expected2,test2);
		
		
	}
	
	public void slowshaHelper(String expected, String input) {
		byte[] inputBytes = input.getBytes();
		byte[] output;
		try {
			output = Deterministic.bin_slowsha(inputBytes);
			String result= Deterministic.bytesToHex(output);
			assertEquals(expected,result);
		} catch (NoSuchAlgorithmException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("This does not work for sha256");
		}
	}

}
