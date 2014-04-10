package org.aquaregia.wallet;

import java.io.File;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.kits.WalletAppKit;


public class ARWallet {
	public static final String WALLET_DEFAULT = "default";
	
	// make the wallet work on main Bitcoin network
	NetworkParameters params = MainNetParams.get();
	WalletAppKit bitcoin;
	
	public ARWallet() {
		this(WALLET_DEFAULT);
	}
	
	public ARWallet(String walletName) {
		assert(walletName != null);
		bitcoin = new WalletAppKit(params, new File("."),  walletName);
		//
	}
}
