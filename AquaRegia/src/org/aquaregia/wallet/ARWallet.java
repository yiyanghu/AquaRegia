package org.aquaregia.wallet;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;
import javax.swing.SwingUtilities;

import org.aquaregia.ui.Main;
import org.aquaregia.ui.WalletView;
import org.aquaregia.ui.Strings;
import org.aquaregia.wallet.addressbook.AddressBook;
import org.aquaregia.wallet.deterministic.DeterministicExtension;
import org.aquaregia.wallet.exchange.BitstampUpdater;
import org.aquaregia.wallet.exchange.ExchangeRateUpdateTask;
import org.aquaregia.wallet.history.SimpleTransactionDetails;
import org.aquaregia.wallet.history.TransactionHistory;
import org.spongycastle.crypto.params.KeyParameter;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.InsufficientMoneyException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.core.Wallet.SendRequest;
import com.google.bitcoin.core.Wallet.SendResult;
import com.google.bitcoin.core.WalletEventListener;
import com.google.bitcoin.core.WalletExtension;
import com.google.bitcoin.crypto.KeyCrypter;
import com.google.bitcoin.crypto.KeyCrypterException;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.utils.Threading;

/**
 * Manages a Bitcoin Wallet
 * @author Stephen Halm
 */
public class ARWallet extends Observable {
	//public WalletView view;
	
	public static final String WALLET_DEFAULT = "default";
	
	// make the wallet work on main Bitcoin network
	private NetworkParameters params;
	private PeerGroup peerGroup;
	private Wallet wallet;
	private WalletInitializer walletGen;
	private WalletEventHandler weventh;
	private String name;

	public DeterministicExtension deterministic;

	private BlockChain chain;
	
	private ExchangeRateUpdateTask exchangeRateTask;
	
	/**
	 * Initialize with default.wallet
	 */
	public ARWallet() {
		this(WALLET_DEFAULT);
	}
	
	/**
	 * Initialize with given wallet
	 * @param walletName - open walletName + '.wallet'
	 */
	public ARWallet(String walletName) {
		this(walletName, new File("."));
	}
	
	/**
	 * Initialize a given wallet in selected directory
	 * @param walletName - open walletName + '.wallet'
	 * @param parentDirectory - (of file)
	 */
	public ARWallet(String walletName, File parentDirectory) {
		initWallet(walletName, parentDirectory);
		initGeneric();
	}
	
	private void initGeneric() {
		exchangeRateTask = new ExchangeRateUpdateTask(new BitstampUpdater(), new ExchangeHandler());
	}
	
	/**
	 * Open wallet
	 * @param walletName
	 */
	private void initWallet(String walletName, File directory) {
		assert(walletName != null);
		if (walletName.toLowerCase().startsWith("testnet")) {
			params = TestNet3Params.get();
			System.out.println(walletName + " is a testnet wallet");
		}
		else {
			params = MainNetParams.get();
		}
		walletGen = new WalletInitializer(params, directory,  walletName);
		
		// configure wallet service
		walletGen.setDownloadListener(new UIDownloadListener())
			.setBlockingStartup(false)
			.setUserAgent(Strings.appname, Strings.appversion);
		// and launch
		walletGen.startAsync();
		walletGen.awaitRunning();
		
		// post configuration
		name = walletGen.getName();
		peerGroup = walletGen.peerGroup();
		chain = walletGen.chain();
		peerGroup.setMaxConnections(12);
		wallet = walletGen.wallet();
		wallet.allowSpendingUnconfirmedTransactions();
		weventh = new WalletEventHandler();
		wallet.addEventListener(weventh);
		// ensure we have atleast one address
		//if (wallet.getKeychainSize() < 1)
		//	addAddress();
		Map<String,WalletExtension> extensions = wallet.getExtensions();
		deterministic = (DeterministicExtension) extensions.get(DeterministicExtension.getExtensionIDStatic());
		if (walletGen.newWallet)
			extensionsInit();
	}
	
	public void switchWallet(String walletName, File directory) {
		if (weventh != null)
			wallet.removeEventListener(weventh);
		if (wallet != null)
			close();
		initWallet(walletName, directory);
		uiInitData();
	}
	
	private void extensionsInit() {
		deterministic.newSeedInit(wallet);
	}
	
	// Commands available for UI
	
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
		
		// TODO do something about failed sends
		SendResult res = wallet.sendCoins(peerGroup, destination, btc);
		res.broadcastComplete.addListener(new Runnable() {
			@Override
			public void run() {
				// TODO inform UI that transaction is sent
			}
			
		}, Threading.USER_THREAD);
	}
	
	/**
	 * Add a new receiving address to the wallet
	 * @return address that was added
	 */
	@Nullable
	public Address addAddress() {
		if (deterministic.isInitialized()) {
			return null;
		}
		ECKey key = new ECKey();
		wallet.addKey(key);
		return key.toAddress(params);
	}
	
	/**
	 * Encrypt wallet
	 * @param password
	 */
	public void encrypt(String password) {
		KeyParameter kp = wallet.encrypt(password);
		if (deterministic.isInitialized()) {
			deterministic.encrypt(wallet.getKeyCrypter(), kp);
		}
	}
	
	/**
	 * Check if wallet is encrypted
	 * @return true if wallet is encrypted
	 */
	public boolean isEncrypted() {
		return wallet.isEncrypted();
	}
	
	/**
	 * Attempts to decrypt the wallet with the input password
	 * @param password
	 * @return true if wallet was decrypted, else false
	 */
	public boolean decrypt(String password) {
		KeyCrypter kc = wallet.getKeyCrypter();
		KeyParameter decryptingKey = kc.deriveKey(password);
		try {
			wallet.decrypt(decryptingKey);
			if (deterministic.isInitialized()) {
				deterministic.decrypt(kc, decryptingKey);
			}
		} catch (KeyCrypterException kce) {
			if (deterministic.isInitialized() && !wallet.isEncrypted() && deterministic.isEncrypted()) {
				// ensure both parts are encrypted
				wallet.encrypt(kc, decryptingKey);
			}
			return false;
		}
		return true;
	}
	
	// UI updating

	public void viewOpened() {
		uiInitData();
	}
	
	private void uiInitData() {
			pushName();
			pushBalance();
			pushHistory();
			pushOwnedAddresses();
			// and the UI is ready
			pushShow();
	}
	
	private void pushShow() {
		Object[] update = {ModelUpdate.SHOW};
		setChanged();
		notifyObservers(update);
	}
	
	private void pushName() {
		Object[] update = {
				ModelUpdate.NAME,
				name
			};
		setChanged();
		notifyObservers(update);
	}
	
	private void pushBalance() {
		Object[] update = {
			ModelUpdate.BALANCE,
			new BitcoinAmount(wallet.getBalance())
		};
		setChanged();
		notifyObservers(update);
	}
	
	private void pushHistory() {
		Object[] update = {
				ModelUpdate.HISTORY,
				new TransactionHistory(wallet, params)
		};
		setChanged();
		notifyObservers(update);
	}

	private void pushOwnedAddresses() {
		Object[] update = {
				ModelUpdate.OWNED_ADDRESSES,
				new AddressBook(wallet.getKeys(), params)
		};
		setChanged();
		notifyObservers(update);
	}
	
	private void pushExchangeRate() {
		pushExchangeRate(BigDecimal.ZERO, "", "");
	}
	
	private void pushExchangeRate(BigDecimal rate, String symbol, String source) {
		Object[] update = new Object[4];
		update[0] = ModelUpdate.EXCHANGE_RATE;
		update[1] = rate;
		update[2] = symbol;
		update[3] = source;
		setChanged();
		notifyObservers(update);
	}
	
	public class ExchangeHandler implements ExchangeRateUpdateTask.ExchangeRateHandler {
		@Override
		public void update(BigDecimal exchangeRate, String symbol, String source) {
			// we are in the swing thread already
			pushExchangeRate(exchangeRate, symbol, source);
		}
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
			// seems to run every 1%. not in UI thread

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
    		System.out.println("ensure keys");
			deterministic.ensureFreeKeys();
		}

		@Override
		public void onCoinsSent(Wallet wallet, Transaction tx,
				BigInteger prevBalance, BigInteger newBalance) {
			System.out.println("ensure keys");
			deterministic.ensureFreeKeys();
		}

		@Override
		public void onKeysAdded(Wallet wallet, List<ECKey> keys) {
			pushOwnedAddresses();
		}

		@Override
		public void onReorganize(Wallet wallet) {
			System.out.println("ensure keys");
			deterministic.ensureFreeKeys();
		}

		@Override
		public void onScriptsAdded(Wallet wallet, List<Script> scripts) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTransactionConfidenceChanged(Wallet wallet, Transaction tx) {
			// see onWalletChanged
		}

		@Override
		public void onWalletChanged(Wallet wallet) {
			//System.out.flush();
			
			pushBalance();
			pushHistory();
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
