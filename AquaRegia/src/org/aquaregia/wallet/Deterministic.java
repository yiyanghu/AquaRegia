package org.aquaregia.wallet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.*;

public class Deterministic {

	public static byte[] bin_slowsha(byte[] input) throws NoSuchAlgorithmException, IOException {
		byte[] result= null;
		byte[] original = input;	
		for(int i=0;i<100000;i++) {
			MessageDigest md= MessageDigest.getInstance("SHA-256");
			result = new byte[original.length+ input.length];
			System.arraycopy(input, 0, result, 0, input.length);
			System.arraycopy(original,0,result,input.length,original.length);
			input= result;		
			md.update(input);
			input = md.digest();
		}
		return input;
	}
	
	public static String bytesToHex(byte[] bytes) {
		
		return DatatypeConverter.printHexBinary(bytes).toLowerCase();
			
	}
	
	
	
	
}
