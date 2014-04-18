package org.aquaregia.wallet;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.xml.bind.*;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Utils;



/**
 * Deterministic Wallet
 * Generate Master Private Key or Mater Public Key
 * @author Yiyang Hu and Steve Halm
 */


public class Deterministic {
	
	public static final BigInteger N = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");
	public static final BigInteger P = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007908834671663");
	public static final BigInteger A = new BigInteger("0");
	
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

	/**
	 * This function generates master public key from a wallet seed
	 * @param seed - a wallet seed
	 * @return master public key (no 0x04 byte at front)
	 */
	public static byte[] getMasterPublicKey (byte[] seed) {
		byte[] masterPrivateKey = getMasterPrivateKey(seed);
		return privateToPublic(masterPrivateKey);
		
	}

	public static byte[] getPrivateKey(byte[] masterPrivateKey, int n, int isChangeAddr) {
		byte[] masterPublicKey = privateToPublic(masterPrivateKey);
		byte[] offset = offsetPrivatePartialKey(n, isChangeAddr, masterPublicKey);
		return addPrivateKeys(masterPrivateKey, offset);
	}
	
	public static byte[] getPrivateKey(byte[] masterPrivateKey,int n) {
		return getPrivateKey(masterPrivateKey, n, 0);		
	}
	
	public static byte[] getPublicKey(byte[] masterPublicKey, int n, int isChangeAddr) {
		byte[] offsetPrivate = offsetPrivatePartialKey(n, isChangeAddr, masterPublicKey);
		byte[] offsetPublic = privateToPublic(offsetPrivate);
		return addPublicKeys(masterPublicKey, offsetPublic);
	}
	
	public static byte[] getPublicKey(byte[] masterPublicKey, int n) {
		return getPublicKey(masterPublicKey, n, 0);
	}
	
	public static byte[] addPrivateKeys(byte[] key1, byte[] key2) {
		BigInteger key1Int = new BigInteger(1, key1);
		BigInteger key2Int = new BigInteger(1, key2);
		BigInteger sum = key1Int.add(key2Int);
		sum = sum.mod(N);
		return Utils.bigIntegerToBytes(sum, 32);
	}

	public static byte[] addPublicKeys(byte[] masterPublicKey, byte[] offsetPublic) {
		BigInteger mpk1 = new BigInteger(1, Arrays.copyOfRange(masterPublicKey, 0, 32));
		BigInteger mpk2 = new BigInteger(1, Arrays.copyOfRange(masterPublicKey, 32, 64));
		BigInteger opb1 = new BigInteger(1, Arrays.copyOfRange(offsetPublic, 0, 32));
		BigInteger opb2 = new BigInteger(1, Arrays.copyOfRange(offsetPublic, 32, 64));
		
		BigInteger[] masterPublicKeyDiv = {mpk1, mpk2};
		BigInteger[] offsetPublicDiv = {opb1, opb2};
		
		BigInteger[] resultDiv = pairAdd(masterPublicKeyDiv, offsetPublicDiv);
		byte[] resleft = Utils.bigIntegerToBytes(resultDiv[0], 32);
		byte[] resright = Utils.bigIntegerToBytes(resultDiv[1], 32);
		return byteArrayConcat(resleft, resright);
	}

	public static byte[] privateToPublic(byte[] privateKey) {
		ECKey keyPair = new ECKey(privateKey, null);	
		byte [] result = keyPair.getPubKey(); 
		return Arrays.copyOfRange(result, 1, result.length);
	}

	/**
	 * Construct a wallet key you can spend from if you input a public key.
	 * Construct a wallet key you can only watch if you input a private key.
	 * @param key - (public|private) key
	 * @return
	 */
	public static ECKey keyConstruct(byte[] key) {
		if (key.length == 32) {
			// private key
			return new ECKey(key, null);
		}
		else if (key.length == 64) {
			// public key
			final byte[] hex4 = {4};
			return new ECKey(null, byteArrayConcat(hex4, key));
		}
		else
			throw new RuntimeException("Key format not in a deterministic relevant format");
	}
	
	public static String bytesToHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes).toLowerCase();
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static byte[] doubleSHA256(byte[] input) {
		for (int i = 0; i < 2 ; i++) {
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
	
	private static byte[] byteArrayConcat(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second,0,result,first.length,second.length);
		return result;
	}

	private static byte[] offsetPrivatePartialKey(int n, int isChangeAddr, byte[] masterPublicKey) {
		String indexCode = Integer.toString(n)+":"+Integer.toString(isChangeAddr)+":";
		return doubleSHA256(byteArrayConcat(indexCode.getBytes(), masterPublicKey));
	}

	private static BigInteger[] pairAdd(BigInteger[] a, BigInteger[] b) {
		if (isinf(a)) {
			BigInteger[] res = {b[0],b[1]};
			return res;
		}
		if (isinf(b)) {
			BigInteger[] res = {a[0],a[1]};
			return res;
		}
		if (a[0].equals(b[0])) {
			if (a[1].equals(b[1])) {
				BigInteger[] q = {a[0],a[1]};
				return pairDouble(q);
			}
			else {
				BigInteger[] zeros = {BigInteger.ZERO, BigInteger.ZERO};
				return zeros;
			}
		}
		
		//m = ((b[1]-a[1]) * inv(b[0]-a[0],P)) % P
		BigInteger ml = b[1].subtract(a[1]);
		BigInteger mr = modInverse(b[0].subtract(a[0]), P);
		//BigInteger mr = b[0].subtract(a[0]).modInverse(P);
		BigInteger m = ml.multiply(mr).mod(P);
		
		//x = (m*m-a[0]-b[0]) % P
		BigInteger m2 = m.multiply(m);
		BigInteger diff = m2.subtract(a[0]).subtract(b[0]);
		BigInteger x = diff.mod(P);
		
		//y = (m*(a[0]-x)-a[1]) % P
		BigInteger adiff = a[0].subtract(x);
		BigInteger yl = m.multiply(adiff);
		BigInteger y = yl.subtract(a[1]).mod(P);
		
		BigInteger[] result = {x,y};
		return result;
	}
	
	private static BigInteger[] pairDouble(BigInteger[] a) {
		if (isinf(a)) {
			BigInteger[] zeros = {BigInteger.ZERO, BigInteger.ZERO};
			return zeros;
		}
		final BigInteger two = new BigInteger("2");
		final BigInteger three = new BigInteger("3");
		
		//m=((3*a[0]*a[0]+A)*inv(2*a[1],P)) % P
		BigInteger ml = three.multiply(a[0]).multiply(a[0]).add(A);
		BigInteger mr = modInverse(two.multiply(a[1]),P);
		BigInteger m = ml.multiply(mr).mod(P);
		
		//x = (m*m-2*a[0]) % P
		BigInteger m2 = m.multiply(m);
		BigInteger diff = m2.subtract(a[0].multiply(two));
		BigInteger x = diff.mod(P);
		
		//y = (m*(a[0]-x)-a[1]) % P
		BigInteger adiff = a[0].subtract(x);
		BigInteger yl = m.multiply(adiff);
		BigInteger y = yl.subtract(a[1]).mod(P);
		
		BigInteger[] result = {x,y};
		return result;
	}
	
	private static BigInteger modInverse(BigInteger a, BigInteger n) {
        BigInteger lm = BigInteger.ONE, hm = BigInteger.ZERO;
        BigInteger low = a.mod(n), high = n;
        
        while (low.compareTo(BigInteger.ONE) > 0) {
        	BigInteger r = high.divide(low);
        	BigInteger nm = hm.subtract(lm.multiply(r)), newi = high.subtract(low.multiply(r));
        	hm = lm;
        	lm = nm;
        	high = low;
        	low = newi;
        }
        return lm.mod(n);
	}
	
	private static boolean isinf(BigInteger[] p) {
		return (p[0].equals(BigInteger.ZERO) && p[1].equals(BigInteger.ZERO));
	}
	
}
