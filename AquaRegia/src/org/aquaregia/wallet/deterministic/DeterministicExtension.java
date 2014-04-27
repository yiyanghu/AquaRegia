package org.aquaregia.wallet.deterministic;

import org.spongycastle.util.Arrays;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletExtension;

public class DeterministicExtension implements WalletExtension {
	
	private static final int ENCRYPTED = 0x1;
	private String seed;
	private boolean isEncrypted;
	private Wallet wallet;
	private int sequenceNum;

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
		
		determineSequenceNumber();
	}
	
	private void determineSequenceNumber() {
		// TODO determine next wallet key index
		
	}

	public ECKey nextKey() {
		// TODO get next key
		return null;
	}
	
	public void encrypt(CharSequence pw) {
		// TODO encrypt seed
	}
	
	public void decrypt(CharSequence pw) {
		// TODO decrypt seed
		// TODO detect pubkey only ECKeys in the wallet & upgrade them
	}
	

}
