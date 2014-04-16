package org.aquaregia.wallet.history;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.ScriptException;
import com.google.bitcoin.core.Transaction;
import com.google.bitcoin.core.TransactionConfidence;
import com.google.bitcoin.core.TransactionInput;
import com.google.bitcoin.core.TransactionOutput;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.core.Wallet;

/**
 * Stores basic transaction details in accessible format for a view
 * @author Stephen Halm
 */
public class SimpleTransactionDetails {
	/** Block depth where transaction becomes 'final' */
	public static final int CONFIRMED_NUMBER_CONF = 6;
	
	private Transaction tx;
	private Wallet wallet;
	private NetworkParameters params;
	
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
	
	/**
	 * Gets description of transaction (currently just send from/receive to where)
	 * @return description
	 */
	public String description() {
		String desc = "";
		boolean out1 = false;
		for (TransactionOutput out : tx.getOutputs()) {
			if (out.isMine(wallet)) {
				desc += ">" + out.getScriptPubKey().getToAddress(params).toString();
				if (!out1)
					out1 = true;
				else {
					desc += "+...";
					break;
				}
			}
		}
		boolean in1 = false;
		for (TransactionInput in : tx.getInputs()) {
			try {
				byte[] pubkey = in.getScriptSig().getPubKey();
				if (wallet.isPubKeyMine(pubkey)) {
					if (desc.length() > 0)
						desc += " ";
					desc += "<" + new Address(params, Utils.sha256hash160(pubkey)).toString();
					if (!in1)
						in1 = true;
					else {
						desc += "+...";
						break;
					}
				}
			}
			catch (ScriptException e) { }
		}
		if (desc.length() > 0)
			return desc;
		else
			return "(?) Unknown relatedness to your addresses";
	}
	
	
	// Constructors
	
	public SimpleTransactionDetails(Transaction tx, Wallet wallet, NetworkParameters params) {
		this(tx, wallet, params, new BigInteger("0"));
	}
	
	public SimpleTransactionDetails(Transaction tx, Wallet wallet, NetworkParameters params, BigInteger prevTotal) {
		this.tx = tx;
		this.wallet = wallet;
		this.params = params;
		netBalanceChange = new BitcoinAmount(tx.getValue(wallet));
		eventTotal = new BitcoinAmount(prevTotal.add(netBalanceChange));
		txTime = tx.getUpdateTime();
		TransactionConfidence tc = tx.getConfidence();
		confType = tc.getConfidenceType();
		confirmations = tc.getDepthInBlocks();
	}

}
