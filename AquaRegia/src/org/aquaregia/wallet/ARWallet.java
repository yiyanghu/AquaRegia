package org.aquaregia.wallet;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.params.MainNetParams;
import com.google.bitcoin.params.RegTestParams;


public class ARWallet {

	// make the wallet work on main bitcoin network
	NetworkParameters params = MainNetParams.get();
	
	public ARWallet() {
		
	}
}
