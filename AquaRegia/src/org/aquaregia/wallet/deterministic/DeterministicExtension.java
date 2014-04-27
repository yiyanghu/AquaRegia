package org.aquaregia.wallet.deterministic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.util.Arrays;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletExtension;
import com.google.bitcoin.crypto.EncryptedPrivateKey;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterException;

public class DeterministicExtension implements WalletExtension {
	
	public static final int KEYLOOKAHEAD = 5;
	private Seed seed;
	private Wallet wallet;
	/* Number of keys in wallet (next index to use) */
	private int sequenceNum;
	private boolean initialized = false;

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
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(seed);
		} catch (IOException ioe) {
			// this is very bad
			ioe.printStackTrace();
			throw new RuntimeException("we couldn't serialize wallet seed");
		}
		
		return bos.toByteArray();
	}

	@Override
	public void deserializeWalletExtension(Wallet containingWallet, byte[] data)
			throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		wallet = containingWallet;
		
		ObjectInputStream ois = new ObjectInputStream(bis);
		seed = (Seed) ois.readObject();
		seed.generateMasterKeys();
		
		ensureFreeKeys();
		initialized = true;
	}
	
	public boolean isInitialized() {
		return initialized;
	}
	
	public void newSeedInit() {
		seed = new Seed(Deterministic.randomSeed());
		// We assume that this is a new not encrypted wallet
		// Besides, we can't encrypt without the password
		sequenceNum = 0;
		initialized = true;
		seed.generateMasterKeys();
		ensureFreeKeys();
	}
	
	public void recoverSeedInit() {
		// TODO
	}
	

	private void ensureFreeKeys() {
		Map<ECKey, Boolean> used = new HashMap<ECKey, Boolean>();
		
		List<ECKey> allKeys = wallet.getKeys();
		List<Transaction> txs = wallet.getTransactionsByTime();
		for (ECKey key : allKeys) {
			used.put(key, false);
		}
		
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
		
		int usedIndex = -1;
		for (int i = usedIndex; i <= usedIndex + KEYLOOKAHEAD; i++) {
			ECKey candidateKey = getKey(i);
			ECKey search = wallet.findKeyFromPubKey(candidateKey.getPubKey());
			if (search != null) {
				if (search.isPubKeyOnly() && !candidateKey.isPubKeyOnly()) {
					swap(search, candidateKey);
				}
				Boolean isUsed = used.get(search);
				if (isUsed != null && isUsed) {
					// move the 5 free keys goal post whenever a key is used
					usedIndex = i;
				}
			}
		}
		
	}

	public int getSequenceNumber() {
		return sequenceNum;
	}
	
	private void swap(ECKey original, ECKey replacement) {
		wallet.removeKey(original);
		wallet.addKey(replacement);
	}

	public ECKey nextKey() {
		ECKey key = getKey(sequenceNum);
		sequenceNum++;
		return key;
	}
	
	private ECKey getKey(int n) {
		byte[] keyBytes;
		if (seed.isEncrypted)
			keyBytes = Deterministic.getPublicKey(seed.masterPublicKey, n);
		else
			keyBytes = Deterministic.getPrivateKey(seed.masterPrivateKey, n);
		return Deterministic.keyConstruct(keyBytes);
	}
	
	public String seedMnemonic() {
		return Mnemonic.encode(seed.seedStr());
	}
	
	public String viewMasterPubKey() {
		return Utils.bytesToHexString(seed.masterPublicKey);
	}
	
	public void encrypt(KeyCrypter keyCrypter, KeyParameter aesKey) {
		seed.encrypt(keyCrypter, aesKey);
	}
	
	public boolean decrypt(KeyCrypter keyCrypter, KeyParameter aesKey) {
		return seed.decrypt(keyCrypter, aesKey);
	}
	
	public boolean isWatching() {
		return seed.isWatching();
	}
	
	private static class Seed implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1686378218914664675L;
		public byte[] iv;
		public byte[] data;
		public byte[] masterPublicKey;
		transient byte[] masterPrivateKey;
		public boolean isEncrypted;
		
		public Seed(String seed) {
			set(seed);
		}
		
		// Warning: may exist arbitrarily long in JVM
		public String seedStr() {
			return new String(data);
		}
		
		public void set(String seed) {
			data = seed.getBytes();
		}
		
		private void generateMasterKeys() {
			masterPrivateKey = Deterministic.getMasterPrivateKey(data);
			masterPublicKey = Deterministic.privateToPublic(masterPrivateKey);
		}
		
		public boolean isWatching() {
			return data == null;
		}
		
		public void encrypt(KeyCrypter keyCrypter, KeyParameter aesKey) {
			if (isWatching()) return;
			try {
		        EncryptedPrivateKey encryptedPrivateKey = keyCrypter.encrypt(data, aesKey);
		        iv = encryptedPrivateKey.getInitialisationVector();
		        data = encryptedPrivateKey.getEncryptedBytes();
		        isEncrypted = true;
		        masterPrivateKey = null;
			} catch (KeyCrypterException e) {
				e.printStackTrace();
				throw new RuntimeException("seed encryption failed");
			}
		}
		
		public boolean decrypt(KeyCrypter keyCrypter, KeyParameter aesKey) {
			if (isWatching()) return false;
			if (!isEncrypted) return false;
			try {
				EncryptedPrivateKey encryptedPrivateKey = new EncryptedPrivateKey(iv, data);
				byte[] decData = keyCrypter.decrypt(encryptedPrivateKey, aesKey);
				if (!Arrays.areEqual(Deterministic.getMasterPublicKey(decData), masterPublicKey))
						return false;
				data = decData;
				generateMasterKeys();
				isEncrypted = false;
				return true;
			} catch (KeyCrypterException e) {
				return false;
			}
		}
	}

}
