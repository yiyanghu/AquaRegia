package org.aquaregia.wallet;

import java.util.Date;
import java.util.List;
import java.util.Observable;

import org.aquaregia.ui.Controller;
import org.aquaregia.ui.WalletView;

import java.io.File;
import java.math.BigInteger;

import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.kits.WalletAppKit;


public class ARWallet extends Observable {
	public WalletView view;
	
	public static final String WALLET_DEFAULT = "default";
	
	// make the wallet work on main Bitcoin network
	NetworkParameters params = MainNetParams.get();
	Wallet wallet;
	WalletAppKit walletGen;
	
	public ARWallet() {
		this(WALLET_DEFAULT);
	}
	
	public ARWallet(String walletName) {
		assert(walletName != null);
		walletGen = new WalletInitializer(params, new File("."),  walletName);
		
		walletGen.setDownloadListener(null);
/*
 *         bitcoin.setDownloadListener(controller.progressBarUpdater())
               .setBlockingStartup(false)
               .setUserAgent(APP_NAME, "1.0");
        bitcoin.startAsync();
        bitcoin.awaitRunning();
        // Don't make the user wait for confirmations for now, as the intention is they're sending it their own money!
        bitcoin.wallet().allowSpendingUnconfirmedTransactions();
        bitcoin.peerGroup().setMaxConnections(11);
        System.out.println(bitcoin.wallet());
        controller.onBitcoinSetup();
        mainWindow.show();
 */
		
		wallet = walletGen.wallet();
		wallet.addEventListener(new WalletEventHandler());
	}

	/**
	 * Returns new blockchain download listener that updates UI on progress
	 */
    public class UIDownloadListener extends DownloadListener {
        @Override
        protected void progress(double pct, int blocksSoFar, Date date) {
            super.progress(pct, blocksSoFar, date);
            // TODO:  update progress bar UI element with new percentage
            // ensure bar is unhidden?

        }

        @Override
        protected void doneDownload() {
            super.doneDownload();
            // TODO: show download is done (maybe hide progress bar)
        }
    }
    
    /**
     * Update UI on network events
     */
    public class WalletEventHandler implements WalletEventListener {

		@Override
		public void onCoinsReceived(Wallet wallet, Transaction tx,
				BigInteger prevBalance, BigInteger newBalance) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onCoinsSent(Wallet wallet, Transaction tx,
				BigInteger prevBalance, BigInteger newBalance) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onKeysAdded(Wallet wallet, List<ECKey> keys) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onReorganize(Wallet wallet) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onScriptsAdded(Wallet wallet, List<Script> scripts) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onWalletChanged(Wallet wallet) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}
