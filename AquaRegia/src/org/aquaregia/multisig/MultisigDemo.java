package org.aquaregia.multisig;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.InsufficientMoneyException;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerGroup;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.script.Script;
import com.google.bitcoin.script.ScriptBuilder;
import com.google.common.collect.ImmutableList;

public class MultisigDemo {

	NetworkParameters params; // TODO set
	PeerGroup peerGroup; // TODO set
	Wallet wallet; // TODO set
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
	}

	void createMultiSig() {
		byte[] publicKeyBytes = null; // TODO set
		// Create a random key.
		ECKey clientKey = new ECKey();
		// We get the other parties public key from somewhere ...
		ECKey serverKey = new ECKey(null, publicKeyBytes);

		// Prepare a template for the contract.
		Transaction contract = new Transaction(params);
		List<ECKey> keys = ImmutableList.of(clientKey, serverKey);
		// Create a 2-of-2 multisig output script.
		Script script = ScriptBuilder.createMultiSigOutputScript(2, keys);
		// Now add an output for 0.50 bitcoins that uses that script.
		BigInteger amount = Utils.toNanoCoins(0, 50);
		contract.addOutput(amount, script);

		// We have said we want to make 0.5 coins controlled by us and them.
		// But it's not a valid tx yet because there are no inputs.
		Wallet.SendRequest req = Wallet.SendRequest.forTx(contract);
		try {
			wallet.completeTx(req);
		} catch (InsufficientMoneyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   // Could throw InsufficientMoneyException

		// Broadcast and wait for it to propagate across the network.
		// It should take a few seconds unless something went wrong.
		try {
			peerGroup.broadcastTransaction(req.tx).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
