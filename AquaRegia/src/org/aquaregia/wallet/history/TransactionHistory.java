package org.aquaregia.wallet.history;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.Wallet;

/**
 * A list of transactions in the wallet's history
 * @author Stephen Halm
 */
public class TransactionHistory extends ArrayList<SimpleTransactionDetails> {

	/**
	 * Constructs a history of all transactions from the wallet new to old.
	 * @param wallet to examine
	 * @return list of transactions (in simple details format)
	 */
	public TransactionHistory(Wallet wallet, NetworkParameters params) {
		// new -> old transactions
		List<Transaction> txs = wallet.getTransactionsByTime();
		// old -> new transactions
		Collections.reverse(txs);
		Iterator<Transaction> txIterator = txs.listIterator();

		BigInteger total = new BigInteger("0");
		// query constructor with running total
		while (txIterator.hasNext()) {
			Transaction tx = txIterator.next();
			SimpleTransactionDetails td = new SimpleTransactionDetails(tx, wallet, params, total);
			this.add(td);
		}
		// new -> old simple transaction details
		Collections.reverse(this);
	}
}
