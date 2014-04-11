package org.aquaregia.wallet;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.aquaregia.ui.WalletView;
import org.aquaregia.ui.Strings;
import org.aquaregia.wallet.addressbook.AddressBook;
import org.aquaregia.wallet.history.SimpleTransactionDetails;
import org.aquaregia.wallet.history.TransactionHistory;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.InsufficientMoneyException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.Wallet.SendResult;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.script.Script;

/**
 * Manages a Bitcoin Wallet
 * @author sStephen Halm
 */
public class ARWallet extends Observable {
	public WalletView view;
	
	public static final String WALLET_DEFAULT = "default";
	
	// make the wallet work on main Bitcoin network
	private NetworkParameters params = MainNetParams.get();
	private PeerGroup peerGroup;
	private Wallet wallet;
	private WalletInitializer walletGen;
	
	/**
	 * Initialize with default.wallet
	 */
	public ARWallet() {
		initWallet(WALLET_DEFAULT);
	}
	
	/**
	 * Initialize with given wallet
	 * @param walletName - open walletName + '.wallet'
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
		peerGroup = walletGen.peerGroup();
		peerGroup.setMaxConnections(12);
		wallet = walletGen.wallet();
		wallet.allowSpendingUnconfirmedTransactions();
		wallet.addEventListener(new WalletEventHandler());
		uiInitData();
	}
	
	/**
	 * Sends bitcoin
	 * @param btc - amount of bitcoin to send
	 * @param destAddress - what address to send to
	 * @throws AddressFormatException
	 * @throws InsufficientMoneyException
	 */
	public void simpleSendCoins(BitcoinAmount btc, String destAddress) 
			throws AddressFormatException, InsufficientMoneyException {
		Address destination = new Address(params, destAddress);
		
		SendResult res = wallet.sendCoins(peerGroup, destination, btc);
		
	}
	
	private void uiInitData() {
		pushBalance();
		pushHistory();
		pushOwnedAddresses();
		pushExchangeRate();
		// and the UI is ready
		pushShow();
		// TODO complete informing UI about initial state
	}
	
	private void pushShow() {
		Object[] update = {ModelUpdate.SHOW};
		notifyObservers(update);
	}
	
	private void pushBalance() {
		Object[] update = {
			ModelUpdate.BALANCE,
			new BitcoinAmount(wallet.getBalance())
		};
		notifyObservers(update);
	}
	
	private void pushHistory() {
		Object[] update = {
				ModelUpdate.HISTORY,
				new TransactionHistory(wallet)
		};
		notifyObservers(update);
	}

	private void pushOwnedAddresses() {
		Object[] update = {
				ModelUpdate.OWNED_ADDRESSES,
				new AddressBook(wallet.getKeys(), params)
		};
		notifyObservers(update);
	}
	
	private void pushExchangeRate() {
		// TODO not goal for week 0
		Object[] update = new Object[2];
		update[0] = ModelUpdate.EXCHANGE_RATE;
		update[1] = 425.0; // TODO update proper instead of dummy value
		notifyObservers(update);
	}
	
	/**
	 * Returns new blockchain download listener that updates UI on progress
	 */
    private class UIDownloadListener extends DownloadListener {
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
    private class WalletEventHandler implements WalletEventListener {

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
