package org.aquaregia.multisig;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.AbstractWalletEventListener;
import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.InsufficientMoneyException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.crypto.TransactionSignature;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.RegTestParams;
import com.google.bitcoin.params.TestNet3Params;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.bitcoin.utils.BriefLogFormatter;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;

public class MultisigDemo {

	public static BigInteger TX_FEE = new BitcoinAmount(BitcoinAmount.B.MILLI, "0.1");
	
	public static String FINAL_DEST = "19hScjRQEcz7y2bpqFJfVs21CGv8YLu9t9";
	
	public static NetworkParameters params; // TODO set
	public static PeerGroup peerGroup; // TODO set
	public static Wallet walletMain, walletServer; // TODO set
	public static BlockChain chain;
	
	
	
    private static Address forwardingAddress;
    private static WalletAppKit mainKit, serverKit;
    
    private static final ScheduledExecutorService worker = 
    		  Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {
        // This line makes the log output more compact and easily read, especially when using the JDK log adapter.
        BriefLogFormatter.init();

        params = MainNetParams.get();

        // Parse the address given as the first parameter.
        //forwardingAddress = new Address(params, args[0]);

        // Start up a basic app using a class that automates some boilerplate.
        mainKit = new WalletAppKit(params, new File("."), "multi-mainWallet");
        //serverKit = new WalletAppKit(params, new File("."), "multi-serverWallet");

        // Download the block chain and wait until it's done.
        mainKit.startAsync();
        mainKit.awaitRunning();
        
        //serverKit.startAsync();
        //serverKit.awaitRunning();
        
        peerGroup = mainKit.peerGroup();
        
        walletMain = mainKit.wallet();
        
        File vWalletFile = new File(new File("."), "multi-serverWallet" + ".wallet");
        
        if (vWalletFile.exists()) {
        	System.out.println("found server wallet");
            FileInputStream walletStream = new FileInputStream(vWalletFile);
            walletServer = new Wallet(params);
            //addWalletExtensions(); // All extensions must be present before we deserialize
            new WalletProtobufSerializer().readWallet(WalletProtobufSerializer.parseToProto(walletStream), walletServer);
            //if (shouldReplayWallet)
            //   vWallet.clearTransactions(0);
        } else {
            walletServer = new Wallet(params);
            walletServer.addKey(new ECKey());
            //addWalletExtensions();
        }
        walletServer.autosaveToFile(vWalletFile, 1, TimeUnit.SECONDS, null);
        chain = mainKit.chain();
        chain.addWallet(walletServer);
        
        ///walletServer = serverKit.wallet();
        
        walletMain.allowSpendingUnconfirmedTransactions();
        walletServer.allowSpendingUnconfirmedTransactions();

        if (walletMain.getKeychainSize() < 1) {
        	walletMain.addKey(new ECKey());
        	walletServer.addKey(new ECKey());
        }
        
		ECKey sendKey = walletMain.getKeys().get(0);
        // We want to know when we receive money.
        mainKit.wallet().addEventListener(new AbstractWalletEventListener() {
            private ECKey clientKey;
			private ECKey serverKey;
			private Transaction multisigFund;

			@Override
            public void onCoinsReceived(Wallet w, Transaction tx, BigInteger prevBalance, BigInteger newBalance) {
                // Runs in the dedicated "user thread" (see bitcoinj docs for more info on this).
                //
                // The transaction "tx" can either be pending, or included into a block (we didn't see the broadcast).
                BigInteger value = tx.getValueSentToMe(w);
                System.out.println("Received tx for " + Utils.bitcoinValueToFriendlyString(value) + ": " + tx);
                System.out.println("Transaction will now be forwarded");
                
                BigInteger fundAmt = value.subtract(Wallet.SendRequest.DEFAULT_FEE_PER_KB);
                
        		clientKey = walletMain.getKeys().get(0);
                serverKey = walletServer.getKeys().get(0);
                byte[] publicKeyBytes = serverKey.getPubKey();
        		// Create a random key.

        		// We get the other parties public key
        		ECKey serverPubKey = new ECKey(null, publicKeyBytes);
                
        		multisigFund = createMultiSig(clientKey, serverPubKey, fundAmt);
        		
        		
        		System.out.println("Multisig Transaction sent, so we will wait a bit");
        		/*
        		try {
					Thread.sleep(15000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println("sleep problem");
					e.printStackTrace();
					return;
				}
        		*/
        		Runnable task = new Runnable() {

					@Override
					public void run() {
						System.out.println("done waiting");
		        		Address dest;
						try {
							dest = new Address(params, FINAL_DEST);
						} catch (AddressFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
		        		System.out.println("Begin multisig redeem");
		        		ECKey.ECDSASignature serverSig = serverAddSpend(dest, multisigFund, serverKey);
		        		clientFinishSpend(multisigFund, serverSig, clientKey);
		        		System.out.println("*** Done ***");
						
					}
        			
        		};
        		worker.schedule(task, 15, TimeUnit.SECONDS);
        		

            }
        });
        
        Address sendToAddress = sendKey.toAddress(params);
        System.out.println("Send coins to: " + sendToAddress);
        
    }

	public static Transaction createMultiSig(ECKey mainKey, ECKey serverKey, BigInteger amount) {
		// Prepare a template for the contract.
		Transaction contract = new Transaction(params);
		List<ECKey> keys = ImmutableList.of(mainKey, serverKey);
		// Create a 2-of-2 multisig output script.
		Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
		// Now add an output for 0.50 bitcoins that uses that script.
		contract.addOutput(amount, script);

		Object o = Wallet.SendRequest.DEFAULT_FEE_PER_KB;
		// We have said we want to make 0.5 coins controlled by us and them.
		// But it's not a valid tx yet because there are no inputs.
		Wallet.SendRequest req = Wallet.SendRequest.forTx(contract);
		try {
			walletMain.completeTx(req);
		} catch (InsufficientMoneyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   // Could throw InsufficientMoneyException

		// Broadcast and wait for it to propagate across the network.
		// It should take a few seconds unless something went wrong.
		try {
			System.out.println("tx prepared");
			peerGroup.broadcastTransaction(req.tx).get();
			System.out.println("Sent");
			System.out.println("Multisig fund tx: " + req.tx.getHash());
			return(req.tx);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public static ECKey.ECDSASignature serverAddSpend(Address dest, Transaction multisigFund, ECKey serverKey) {
		System.out.println("server sig");
		// Assume we get the multisig transaction we're trying to spend from 
		// somewhere, like a network connection.
		TransactionOutput multisigOutput = multisigFund.getOutput(0);
		Script multisigScript = multisigOutput.getScriptPubKey();
		// Is the output what we expect?
		System.out.print("checking the tx output we selected is multisig...");
		assert(multisigScript.isSentToMultiSig());
		System.out.println("OK");
		BigInteger value = multisigOutput.getValue();

		// OK, now build a transaction that spends the money back to the client.
		Transaction spendTx = new Transaction(params);
		spendTx.addOutput(value, dest);
		spendTx.addInput(multisigOutput);

		// It's of the right form. But the wallet can't sign it. So, we have to
		// do it ourselves.
		Sha256Hash sighash = spendTx.hashForSignature(0, multisigScript, Transaction.SigHash.ALL, false);
		ECKey.ECDSASignature signature = serverKey.sign(sighash);
		// We have calculated a valid signature, so send it back to the client:
		return signature;
	}
	
	public static void clientFinishSpend(Transaction multisigFund, ECKey.ECDSASignature serverSignature, ECKey clientKey) {
		System.out.println("client sig");
		// Client side code.
		TransactionOutput multisigOutput = multisigFund.getOutput(0);
		Script multisigScript = multisigOutput.getScriptPubKey();
		
		Transaction spendTx = new Transaction(params);
		BigInteger value = multisigOutput.getValue().subtract(Wallet.SendRequest.DEFAULT_FEE_PER_KB);

		spendTx.addOutput(value, clientKey);

		TransactionInput input = spendTx.addInput(multisigOutput);
		Sha256Hash sighash = spendTx.hashForSignature(0, multisigScript, Transaction.SigHash.ALL, false);
		ECKey.ECDSASignature mySignature = clientKey.sign(sighash);

		TransactionSignature ts1 = new TransactionSignature(serverSignature, Transaction.SigHash.ALL, false);
		TransactionSignature ts2 = new TransactionSignature(mySignature, Transaction.SigHash.ALL, false);
		
		// Create the script that spends the multi-sig output.
		Script inputScript = ScriptBuilder.createMultiSigInputScript(
		    ImmutableList.of(ts1, ts2));
		// Add it to the input.
		input.setScriptSig(inputScript);

		// We can now check the server provided signature is correct, of course...
		try {
			input.verify(multisigOutput);  // Throws an exception if the script doesn't run.
		}
		catch (Exception e) {
			System.out.println("problem with multisig redeem");
		}

		// It's valid! Let's take back the money.
		try {
			System.out.println("tx prepared");
			peerGroup.broadcastTransaction(spendTx).get();
			System.out.println("Multisig fund tx: " + spendTx.getHash());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		// Wallet now has the money back in it.
	}
}
