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
	
	// Elliptic curve constants for secp256k1
	
	public static final BigInteger N = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");
	public static final BigInteger P = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007908834671663");
	public static final BigInteger A = new BigInteger("0");
	
	/**
	 * Generate a master private key from a wallet seed
	 * @param seed - wallet seed (hex string)
	 * @return private key (do not store funds in this master key!)
	 */
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
	 * Generate a master public key from a wallet seed
	 * @param seed - a wallet seed
	 * @return master public key (no 0x04 byte at front)
	 */
	public static byte[] getMasterPublicKey (byte[] seed) {
		byte[] masterPrivateKey = getMasterPrivateKey(seed);
		return privateToPublic(masterPrivateKey);
		
	}

	/**
	 * Get the nth private key based from the master private key.
	 * @param masterPrivateKey - master private key for the wallet
	 * @param n - the index into the key chain
	 * @param isChangeAddr - (whether to use the receive or change address chain)
	 * @return nth private key
	 */
	public static byte[] getPrivateKey(byte[] masterPrivateKey, int n, int isChangeAddr) {
		byte[] masterPublicKey = privateToPublic(masterPrivateKey);
		byte[] offset = offsetPrivatePartialKey(n, isChangeAddr, masterPublicKey);
		return addPrivateKeys(masterPrivateKey, offset);
	}
	
	/**
	 * Get the nth private key based form the master private key. (In receive chain)
	 * @param masterPrivateKey - master private key for the wallet
	 * @param n - the index into the key chain.
	 * @return nth private key
	 */
	public static byte[] getPrivateKey(byte[] masterPrivateKey,int n) {
		return getPrivateKey(masterPrivateKey, n, 0);		
	}
	
	/**
	 * Get the nth public key based from the master public key.
	 * @param masterPublicKey - master public key for the wallet
	 * @param n - the index into the key chain
	 * @param isChangeAddr - (whether to use the receive or change address chain)
	 * @return nth public key
	 */
	public static byte[] getPublicKey(byte[] masterPublicKey, int n, int isChangeAddr) {
		byte[] offsetPrivate = offsetPrivatePartialKey(n, isChangeAddr, masterPublicKey);
		byte[] offsetPublic = privateToPublic(offsetPrivate);
		return addPublicKeys(masterPublicKey, offsetPublic);
	}
	
	/**
	 * Get the nth public key based from the master public key.
	 * @param masterPublicKey - master public key for the wallet
	 * @param n - the index into the key chain
	 * @return nth public key
	 */
	public static byte[] getPublicKey(byte[] masterPublicKey, int n) {
		return getPublicKey(masterPublicKey, n, 0);
	}
	
	/**
	 * Add two private keys together (k1 + k2) mod N
	 * @param key1 - private key 1
	 * @param key2 - private key 2
	 * @return private key sum
	 */
	public static byte[] addPrivateKeys(byte[] key1, byte[] key2) {
		BigInteger key1Int = new BigInteger(1, key1);
		BigInteger key2Int = new BigInteger(1, key2);
		BigInteger sum = key1Int.add(key2Int);
		sum = sum.mod(N);
		return Utils.bigIntegerToBytes(sum, 32);
	}

	/**
	 * "Add" two public key together (more complicated than actual addition)
	 * @param masterPublicKey - the master public key
	 * @param offsetPublic - offset key
	 * @return sum of the two keys
	 */
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

	/**
	 * Gets the public key corresponding to the input private key (with no 0x04 header byte)
	 * @param privateKey - input private key
	 * @return public key (no 0x04 header byte)
	 */
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
	
	/**
	 * Helper function to turn byte array into hex string
	 * @param bytes - byte array
	 * @return hex string
	 */
	public static String bytesToHex(byte[] bytes) {
		return DatatypeConverter.printHexBinary(bytes).toLowerCase();
	}
	
	/**
	 * Helper function to turn hex string into byte array
	 * @param s - hex string
	 * @return byte array
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	/**
	 * Apply SHA256(SHA256(input))
	 * @param input - data to hash
	 * @return double SHA256 hash
	 */
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
	
	/**
	 * Helper function to concatenate two byte arrays
	 * @param first - 1st byte array
	 * @param second - 2nd byte array
	 * @return joined array
	 */
	private static byte[] byteArrayConcat(byte[] first, byte[] second) {
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second,0,result,first.length,second.length);
		return result;
	}

	/**
	 * Get the offset private key basted on wallet chain parameters
	 * @param n - nth key
	 * @param isChangeAddr - 0=receive 1=change
	 * @param masterPublicKey - coresponding master public key
	 * @return ofsset private key
	 */
	private static byte[] offsetPrivatePartialKey(int n, int isChangeAddr, byte[] masterPublicKey) {
		String indexCode = Integer.toString(n)+":"+Integer.toString(isChangeAddr)+":";
		return doubleSHA256(byteArrayConcat(indexCode.getBytes(), masterPublicKey));
	}

	/**
	 * Add two secp256k1 elliptic curve keys (each key has two element numbers "pairs")
	 * @param a - first key
	 * @param b - second key
	 * @return key sum
	 */
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
	
	/**
	 * When a secp256k1 key is added to itself, we use this method to double it.
	 * @param a - key
	 * @return doubled key
	 */
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
	
	/**
	 * Find modular inverse of at = 1 mod n
	 * @param a in the above formula
	 * @param n in the above formula
	 * @return t in the above formula
	 */
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
	
	/**
	 * Trivial condition on secp256k1 public key math 
	 * @param p - key
	 * @return both elements of pair are zero
	 */
	private static boolean isinf(BigInteger[] p) {
		return (p[0].equals(BigInteger.ZERO) && p[1].equals(BigInteger.ZERO));
	}
	
}
