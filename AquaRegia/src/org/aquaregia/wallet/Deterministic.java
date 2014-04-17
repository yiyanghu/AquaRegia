package org.aquaregia.wallet;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.*;
import com.google.bitcoin.core.ECKey;

public class Deterministic {

	public static byte[] bin_slowsha(byte[] input) {
		byte[] result= null;
		byte[] original = input;	
		for(int i=0;i<100000;i++) {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
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
	
	/**
	 * This function generates master public key from a wallet seed
	 * @param seed - a wallet seed
	 * @return master public key
	 */
	public static byte[] getMasterPublicKey (byte[] seed) {
		
		byte[] masterPrivateKey= bin_slowsha(seed);
		ECKey masterKey = new ECKey(masterPrivateKey,null);	
		return masterKey.getPubKey(); 
		
	}
	
	
	
	
}
