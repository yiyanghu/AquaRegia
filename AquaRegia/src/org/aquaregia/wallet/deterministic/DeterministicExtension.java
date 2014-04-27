package org.aquaregia.wallet.deterministic;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongycastle.util.Arrays;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletExtension;

public class DeterministicExtension implements WalletExtension {
	
	private static final int ENCRYPTED = 0x1;
	private String seed;
	private boolean isEncrypted;
	private Wallet wallet;
	/* Number of keys in wallet (next index to use) */
	private int sequenceNum;
	private boolean initialized = false;
	
	private byte[] masterPrivateKey;
	private byte[] masterPublicKey;

	@Override
	public String getWalletExtensionID() {
		return "org.aquaregia.wallet.deterministic";
	}

	@Override
	public boolean isWalletExtensionMandatory() {
		return true;
	}

	@Override
	public byte[] serializeWalletExtension() {
		// handle config flags
		byte[] cfg = new byte[1];
		cfg[0] &= isEncrypted ? ENCRYPTED : 0;
		
		char[] seedChars = Arrays.copyOf(seed.toCharArray(), 32);
		byte[] seedBytes = new byte[32];
		for (int i = 0; i < seedChars.length; i++) {
			seedBytes[i] = (byte) seedChars[i];
		}
		return Arrays.concatenate(seedBytes, cfg);
	}

	@Override
	public void deserializeWalletExtension(Wallet containingWallet, byte[] data)
			throws Exception {
		wallet = containingWallet;
		byte[] seedBytes = Arrays.copyOf(data, 32);
		char[] seedChars = new char[32];
		for (int i = 0; i < seedBytes.length; i++) {
			seedChars[i] = (char) seedBytes[i];
		}
		seed = new String(seedChars);
		
		byte[] cfg = Arrays.copyOfRange(data, 32, data.length);
		isEncrypted = (cfg[0] & ENCRYPTED) != 0;
		
		generateMasterKeys();
		
		determineSequenceNumber();
		initialized = true;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void newSeedInit() {
		seed = Deterministic.randomSeed();
		// We assume that this is a new not encrypted wallet
		// Besides, we can't encrypt without the password
		sequenceNum = 0;
		isEncrypted = false; 
		initialized = true;
		generateMasterKeys();
		ensureFreeKeys();
	}
	
	public void recoverSeedInit() {
		// TODO
	}
	
	
	private void generateMasterKeys() {
		masterPrivateKey = Deterministic.getMasterPrivateKey(seed.getBytes());
		masterPublicKey = Deterministic.privateToPublic(masterPrivateKey);
	}

	private void ensureFreeKeys() {
		// TODO ensure there are 5 unused keys in wallet
		Map<ECKey, Boolean> used = new HashMap<ECKey, Boolean>();
		
		List<ECKey> allKeys = wallet.getKeys();
		for (ECKey key : allKeys) {
			used.put(key, false);
		}
		
		List<Transaction> txs = wallet.getTransactionsByTime();
		for (Transaction tx : txs) {
			for (TransactionOutput txo : tx.getOutputs()) {
				// if (txo.isWatched(wallet)) {
				byte[] hash160 = txo.getScriptPubKey().getPubKeyHash();
				ECKey loc = wallet.findKeyFromPubHash(hash160);
				if (loc != null) {
					Boolean b = used.put(loc, true);
				}
				//}
			}
			for (TransactionInput txi : tx.getInputs()) {
				ECKey loc = wallet.findKeyFromPubKey(txi.getScriptSig().getPubKey());
				if (loc != null) {
					Boolean b = used.put(loc, true);
				}
			}
		}
		
		// TODO
		
	}

	private void determineSequenceNumber() {
		// TODO determine next wallet key index
		
	}

	public ECKey nextKey() {
		// TODO get next key
		return null;
	}
	
	public String seedMnemonic() {
		return Mnemonic.encode(seed);
	}
	
	public void encrypt(CharSequence pw) {
		// TODO encrypt seed
	}
	
	public void decrypt(CharSequence pw) {
		// TODO decrypt seed
		// TODO detect pubkey only ECKeys in the wallet & upgrade them
	}
	

}
