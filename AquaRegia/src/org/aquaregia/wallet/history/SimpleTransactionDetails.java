package org.aquaregia.wallet.history;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;
import com.google.bitcoin.core.Wallet;

/**
 * Stores basic transaction details in accessible format for a view
 * @author Stephen Halm
 */
public class SimpleTransactionDetails {
	/** Block depth where transaction becomes 'final' */
	public static final int CONFIRMED_NUMBER_CONF = 6;
	
	private BitcoinAmount eventTotal;
	private BitcoinAmount netBalanceChange;
	private Date txTime;
	private int confirmations;
	private TransactionConfidence.ConfidenceType confType;
	
	// Getters
	
	public BitcoinAmount getEventTotal() {
		return eventTotal;
	}

	public BitcoinAmount getNetBalanceChange() {
		return netBalanceChange;
	}

	public Date getTxTime() {
		return txTime;
	}

	public int getConfirmations() {
		return confirmations;
	}

	public TransactionConfidence.ConfidenceType getConfType() {
		return confType;
	}

	public String confidenceString() {
		String confStr = "";
		switch (confType) {
		case BUILDING:
			if (confirmations < CONFIRMED_NUMBER_CONF)
				confStr = "" + confirmations + " conf.";
			else
				confStr = "complete";
			break;
		case DEAD:
			confStr = "dead";
			break;
		case PENDING:
			confStr = "pending";
			break;
		case UNKNOWN:
			confStr = "unknown";
			break;
		default:
			break;
		}
		return confStr;
	}
	
	// Constructors
	
	public SimpleTransactionDetails(Transaction tx, Wallet wallet) {
		this(tx, wallet, new BigInteger("0"));
	}
	
	public SimpleTransactionDetails(Transaction tx, Wallet wallet, BigInteger prevTotal) {
		netBalanceChange = new BitcoinAmount(tx.getValue(wallet));
		eventTotal = new BitcoinAmount(prevTotal.add(netBalanceChange));
		txTime = tx.getUpdateTime();
		TransactionConfidence tc = tx.getConfidence();
		confType = tc.getConfidenceType();
		confirmations = tc.getDepthInBlocks();
	}

}
