package org.aquaregia.ui;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import org.aquaregia.wallet.ARWallet;

import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.script.Script;

/**
 * Binds wallet implementation to interface
 * @author Stephen Halm
 */
public class Controller {

	private ARWallet mwallet;
	private WalletView view;
	
	// Add Listener handlers here (with implements on this object)
	// also make calls to ARWallet based on such events from here

	public void addModel(ARWallet mwallet) {
		this.mwallet = mwallet;
	}

	public void addView(WalletView view) {
		this.view = view;
	}

	public void initModel(Object initParam) {
		return;
	}

}
