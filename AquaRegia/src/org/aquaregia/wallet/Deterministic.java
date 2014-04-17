package org.aquaregia.wallet;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.xml.bind.*;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Utils;

public class Deterministic {
	
	public static final BigInteger N = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");
	
	public static byte[] doubleSHA256(byte[] input) {
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
	
	public static byte[] getPrivateKey(byte[] masterPrivateKey,int n, int isChangeAddr) {
		ECKey masterKeyPair = new ECKey(masterPrivateKey,null);	
		byte[] masterPublicKey = masterKeyPair.getPubKey();
		masterPublicKey = Arrays.copyOfRange(masterPublicKey, 1, masterPublicKey.length);
		String indexCode = Integer.toString(n)+":"+Integer.toString(isChangeAddr)+":";
		byte[] offset = doubleSHA256(byteArrayConcat(indexCode.getBytes(), masterPublicKey));
		return addPrivateKeys(masterPrivateKey, offset);
	}
	
	public static byte[] getPrivateKey(byte[] masterPrivateKey,int n) {
		return getPrivateKey(masterPrivateKey, n, 0);		
	}
	
	public static byte[] addPrivateKeys(byte[] key1, byte[] key2) {
		BigInteger key1Int = new BigInteger(1, key1);
		BigInteger key2Int = new BigInteger(1, key2);
		BigInteger sum = key1Int.add(key2Int);
		sum = sum.mod(N);
		return Utils.bigIntegerToBytes(sum, 32);
	}

	public static byte[] getMasterPrivateKey(byte[] seed) {
		byte[] original = seed;	
		// hash seed with SHA256 100,000 times
		for(int i = 0; i < 100000; i++) {
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("SHA-256");
			} catch (NoSuchAlgorithmException e) {
				return null;
			}
	
			md.update(byteArrayConcat(seed, original));
			seed = md.digest();
		}
		return seed;
	}
	
	public static byte[] byteArrayConcat(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second,0,result,first.length,second.length);
		return result;
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
		byte[] masterPrivateKey = getMasterPrivateKey(seed);
		ECKey masterKeyPair = new ECKey(masterPrivateKey,null);	
		byte [] result = masterKeyPair.getPubKey(); 
		result = Arrays.copyOfRange(result, 1, result.length);
		return result;
		
	}
	
	
	
	
}
