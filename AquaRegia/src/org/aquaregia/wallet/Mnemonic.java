package org.aquaregia.wallet;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.google.bitcoin.core.Utils;

/**
 *  This class provides word choices
 *  and related functions that can encode and decode a message
 * @author Yiyang Hu and Steve Halm
 *
 */
public class Mnemonic {
	final public static String[] wordList = loadWordList();

	final public static int N = 1626;

	final public static String WORDLISTHASH = "c8da327d316f8ee758b790068e618077ac271a89fd77ec1250c59ae40e7b599e";
	
	private static String[] loadWordList() {
		InputStream is = Mnemonic.class.getResourceAsStream("deterministicWordList.txt");
		
		MessageDigest md = null;
		DigestInputStream dis;
		try {
			md = MessageDigest.getInstance("SHA-256");
			dis = new DigestInputStream(is, md);
		} catch (NoSuchAlgorithmException e1) {
			// should not happen
			throw new RuntimeException("Can't use SHA256!");
		}
		
		// and read the data to the list
		Scanner s = new Scanner(dis);
		ArrayList<String> wlist = new ArrayList<String>();
		while (s.hasNextLine()) {
			String line = s.nextLine();
			if (line.length() > 0 )
				wlist.add(line);
		}
		
		byte[] digest = md.digest();
		s.close();
		
		String modified = "Deterministic wordlist is likely modified from containing corect data";
		
		if (! Utils.bytesToHexString(digest).equals(WORDLISTHASH))
			throw new RuntimeException(modified);
		
		if (wlist.size() != N) {
			throw new RuntimeException(modified);
		}
		
		return wlist.toArray(new String[0]);
	}

	/**
	 * returns the string array of 12 words 
	 * @param seed - in hex format
	 * @return
	 */
	public static List<String> encodeToList(String seed) {
		ArrayList<String> out = new ArrayList<String>();
		int iteration = seed.length() / 8;
		for (int i = 0; i < iteration; i++) {
			String word = seed.substring(8 * i, 8 * i + 8);
			long index = Long.parseLong(word, 16);
			int index1 = (int) (index % N);
			int index2 = (int) (((index / N) + index1) % N);
			int index3 = (int) (((index / N / N) + index2) % N);
			out.add(wordList[index1]);
			out.add(wordList[index2]);
			out.add(wordList[index3]);
		}

		return out;
	}

	public static String encode(String seed) {
		return join(encodeToList(seed)," ");
	}

	/**
	 * 
	 * @param message - the given 12 words
	 * @return the string of the seed in hex format
	 */
	public static String decode(String message) {
		ArrayList<String> input = new ArrayList<String>(Arrays.asList(message.split(" ")));
		final ArrayList<String> wordListArray = new ArrayList<String>(Arrays.asList(wordList));
		String output = "";
		for(int i=0;i<input.size()/3;i++){
			String word1 = input.get(3*i);
			String word2 = input.get(3*i+1);
			String word3 = input.get(3*i+2);

			BigInteger p1 = new BigInteger("" + wordListArray.indexOf(word1));
			BigInteger p2 = new BigInteger("" + ((wordListArray.indexOf(word2))%N));
			BigInteger p3 = new BigInteger("" + ((wordListArray.indexOf(word3))%N));
			
			final BigInteger n = new BigInteger("" + N);
			
			BigInteger sum = p1.add(n.multiply(p2.subtract(p1).mod(n)))
					.add(n.multiply(n).multiply(p3.subtract(p2).mod(n)));
			
			output += Utils.bytesToHexString(Utils.bigIntegerToBytes(sum, 4));
		}
		return output;
	}
	
	
	/**
	 * 
	 * @param s - would the the collection of string
	 * @param delimiter - potentially adding the space in between
	 * @return the new long string with space between words
	 */
	public static String join(Collection<String> s, String delimiter) {
		StringBuffer buffer = new StringBuffer();
		Iterator<String> iter = s.iterator();
		while (iter.hasNext()) {
			buffer.append(iter.next());
			if (iter.hasNext()) {
				buffer.append(delimiter);
			}
		}
		return buffer.toString();
	}

}
