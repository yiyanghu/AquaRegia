package org.aquaregia.wallet;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.aquaregia.ui.WalletView;
import org.aquaregia.ui.Strings;

import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.script.Script;


public class ARWallet extends Observable {
	public WalletView view;
	
	public static final String WALLET_DEFAULT = "default";
	
	// make the wallet work on main Bitcoin network
	private NetworkParameters params = MainNetParams.get();
	private Wallet wallet;
	private WalletInitializer walletGen;
	
	/**
	 * Open default.wallet
	 */
	public ARWallet() {
		initWallet(WALLET_DEFAULT);
	}
	
	/**
	 * Open given wallet
	 * @param walletName
	 */
	public ARWallet(String walletName) {
		initWallet(walletName);
	}
	
	/**
	 * Open wallet
	 * @param walletName
	 */
	private void initWallet(String walletName) {
		assert(walletName != null);
		walletGen = new WalletInitializer(params, new File("."),  walletName);
		
		// configure wallet service
		walletGen.setDownloadListener(new UIDownloadListener())
			.setBlockingStartup(false)
			.setUserAgent(Strings.appname, Strings.appversion);
		// and launch
		walletGen.startAsync();
		walletGen.awaitRunning();
		
		// post configuration
		walletGen.peerGroup().setMaxConnections(12);
		wallet = walletGen.wallet();
		wallet.allowSpendingUnconfirmedTransactions();
		wallet.addEventListener(new WalletEventHandler());
		uiInitData();
	}
	
	private void uiInitData() {
		pushBalance();
		pushHistory();
		pushOwnedAddresses();
		pushExchangeRate();
		// TODO inform UI about initial state
	}
	
	private void pushBalance() {
		Object[] update = new Object[2];
		update[0] = ModelUpdate.BALANCE;
		update[1] = wallet.getBalance();
		notifyObservers(update);
	}
	
	private void pushHistory() {
		List<Transaction> txs = wallet.getTransactionsByTime();
		Iterator<Transaction> txIterator = txs.listIterator();
		List<SimpleTransactionDetails> det = new ArrayList<SimpleTransactionDetails>();
		while (txIterator.hasNext()) {
			Transaction tx = txIterator.next();
			BigInteger netBalanceChange = tx.getValue(wallet);
			//...
			SimpleTransactionDetails td = null; // = new...
			det.add(td);
		}
		// TODO finish
	}

	private void pushOwnedAddresses() {
		// TODO
	}
	
	private void pushExchangeRate() {
		// TODO not goal for week 0
		Object[] update = new Object[2];
		update[0] = ModelUpdate.EXCHANGE_RATE;
		update[1] = 425.0; // TODO update proper
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

    /**
     * Call to properly close open wallet
     */
	public void close() {
		walletGen.stopAsync();
        walletGen.awaitTerminated();
	}
}
