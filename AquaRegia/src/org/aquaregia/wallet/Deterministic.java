package org.aquaregia.wallet;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.xml.bind.*;

import com.google.bitcoin.core.ECKey;

public class Deterministic {
	
	public static final BigInteger N = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");
	
	public static byte[] bin_dbl_sha256(byte[] input) {
		for(int i=0;i<2;i++){
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
			md.update(input);
			input = md.digest();
		}
		return input;
	}
	
	public static byte[] getPrivateKey(byte[] masterPrivateKey,int n) {
		ECKey masterKey = new ECKey(masterPrivateKey,null);	
		byte[] masterPublicKey = masterKey.getPubKey(); 
		String indexCode = Integer.toString(n)+":0:"+bytesToHex(masterPublicKey);
		byte[] offset = bin_dbl_sha256(indexCode.getBytes());
		return addPrivateKeys(masterPrivateKey,offset);
		
	}
	
	public static byte[] addPrivateKeys(byte[] key1, byte[] key2) {
		BigInteger key1Int = new BigInteger(key1);
		BigInteger key2Int = new BigInteger(key2);
		BigInteger sum = key1Int.add(key2Int);
		sum = sum.mod(N);
		return sum.toByteArray();
	}

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
		byte [] result = masterKey.getPubKey(); 
		result = Arrays.copyOfRange(result, 1, result.length);
		return result;
		
	}
	
	
	
	
}
