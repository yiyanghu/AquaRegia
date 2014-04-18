package org.aquaregia.wallet;

import java.io.File;

import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.kits.WalletAppKit;

/**
 * Potentially modified WalletAppKit
 * @author Stephen Halm
 */
public class WalletInitializer extends WalletAppKit {
    public WalletInitializer(NetworkParameters params, File directory, String filePrefix) {
    	super(params, directory, filePrefix);
    }
    
    //@Override
    
    
}
