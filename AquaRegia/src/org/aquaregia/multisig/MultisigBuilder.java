package org.aquaregia.multisig;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;

import static com.google.bitcoin.script.ScriptOpCodes.*;

public class MultisigBuilder {

	private List<ECKey> ownedKeys = new ArrayList<ECKey>();
	private List<ECKey> otherKeys = new ArrayList<ECKey>();
	private List<ECKey> allKeys = new ArrayList<ECKey>();
	private boolean finalized = false;
	private int sigsTospend = -1;
	private int expectedTotal = -1;
	
	private Script redeemScript = null;
	private Script outputP2SHScript = null;
	
	private static final int NETWORK_MULTI_MAX = 3; 
	private static int maxKeys = NETWORK_MULTI_MAX;
	private byte[] p2sh_hash;
	
	public MultisigBuilder() {
		
	}
	
	public MultisigBuilder(int sigReq, int keyTotal) {
		sigsTospend = sigReq;
		expectedTotal = keyTotal;
	}
	
	public MultisigBuilder(int sigReq, int keyTotal, List<ECKey> owned, List<ECKey> others) {
		this(sigReq, keyTotal);
		for (ECKey key : owned)
			addOwnedKey(key);
		for (ECKey key : others)
			addOtherKey(key);
		complete();
	}
	
	public boolean addOwnedKey(ECKey key) {
		if (isFull() || finalized)
			return false;
		if (key.isPubKeyOnly())
			throw new RuntimeException("Key pair does not have private key");
		ownedKeys.add(key);
		return true;
	}
	
	public boolean addOtherKey(ECKey key) {
		if (isFull() || finalized)
			return false;
		if (! key.isPubKeyOnly()) {
			System.out.println("Warning, a key we control is used in multisig as third party/unowned");
		}
		otherKeys.add(key);
		return true;
	}
	
	public void complete() {
		if (finalized) return;
		List<ECKey> keys = fullList();
		if (expectedTotal != -1 && keys.size() != expectedTotal)
			throw new RuntimeException("multisig address construction is not completable");
		finalized = true;
		allKeys = keys;
		prepareRedeemScript();
		prepareOutputScript();

	}
	
	private boolean isFull() {
		return (ownedKeys.size() + otherKeys.size()) >= maxKeys;
	}
	
	private List<ECKey> fullList() {
		List<ECKey> keys = new ArrayList<ECKey>();
		keys.addAll(ownedKeys);
		keys.addAll(otherKeys);
		
		// Consistent key order
		// Sort by byte representation of public keys where first bytes take priority in comparison
		// and longer keys (uncompressed go first)
		Collections.sort(keys, new Comparator<ECKey>(){
			@Override
			public int compare(ECKey key1, ECKey key2) {
				BigInteger i1 = new BigInteger(1, key1.getPubKey());
				BigInteger i2 = new BigInteger(1, key2.getPubKey());
				return i1.compareTo(i2);
			}	
		});
		return keys;
	}
	
	private void prepareRedeemScript() {
		if (!finalized)
			throw new RuntimeException("address is not completed");
		redeemScript = ScriptBuilder.createMultiSigOutputScript(sigsTospend, allKeys);	
	}
	
	private void prepareOutputScript() {
		p2sh_hash = Utils.sha256hash160(getRedeemScript());
		
		ScriptBuilder sb = new ScriptBuilder();
		sb.op(OP_HASH160);
		sb.data(p2sh_hash);
		sb.op(OP_EQUAL);
		
		outputP2SHScript = sb.build();
	}

	public static int getKeyLimit() {
		return maxKeys;
	}
	
	public static void setKeyLimit(int limit) {
		if (limit >= maxKeys)
			maxKeys = limit;
		else
			throw new UnsupportedOperationException("decreasing key limit is unaccable for existing instances");
	}

	public int getSigsTospend() {
		return sigsTospend;
	}

	public void setSigsTospend(int sigsTospend) {
		this.sigsTospend = sigsTospend;
	}

	public int getExpectedTotal() {
		return expectedTotal;
	}

	public void setExpectedTotal(int expectedTotal) {
		this.expectedTotal = expectedTotal;
	}
	
	public byte[] getRedeemScript() {
		if (!finalized)
			throw new RuntimeException("address not complete");
		return redeemScript.getProgram();
	}
	
	public Script getOutputScrupt() {
		return new Script(outputP2SHScript.getProgram());
	}
	
	public Address getAddress(NetworkParameters params) {
		if (!finalized)
			throw new RuntimeException("address not complete");
		return Address.fromP2SHHash(params, p2sh_hash);
	}
	
	public byte[] getP2SHhash() {
		return Arrays.copyOf(p2sh_hash, p2sh_hash.length);
	}
	
	public void addOutputToWith(Transaction tx, BigInteger amount){
		tx.addOutput(amount, outputP2SHScript);
	}
	
	public void recordKeep(Transaction tx, Wallet wallet) {
		List<Script> scripts = new ArrayList<Script>();
		for (TransactionOutput txo : tx.getOutputs()) {
			Script outScript = txo.getScriptPubKey();
			byte[] hash = outScript.getPubKeyHash();
			if (Arrays.equals(p2sh_hash, hash))
				scripts.add(outScript);
		}
		if (!scripts.isEmpty()) {
			int count = scripts.size();
			int added = wallet.addWatchedScripts(scripts);
			if (count != added)
				throw new RuntimeException("failed to add all scripts");
		}
	}
	
	public void chooseInputs(Wallet wallet, BigInteger amount) {
		List<Script> allScripts = wallet.getWatchedScripts();
		for (Script script : allScripts) {
			try 
			{
				byte[] hash = script.getPubKeyHash();
				if (Arrays.equals(p2sh_hash, hash)); // <-------------
					//
			} catch (ScriptException se) { }
		}
	}
	
	public void sigInput(Transaction tx) {
		
	}
}
