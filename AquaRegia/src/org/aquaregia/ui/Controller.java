package org.aquaregia.ui;

import java.awt.event.WindowEvent;

import org.aquaregia.wallet.ARWallet;

/**
 * Binds wallet implementation to interface
 * @author Stephen Halm
 */
public class Controller {

	private ARWallet mwallet;
	private WalletView view;
	
	// Add Listener handlers here (with implements on this object)
	// also make calls to ARWallet based on such events from here
	// actions will NOT return UI changes to be made,
	// that will be routed to the view
	
	/**
	 * TODO <--- Make sure to bind this to the window closing of the app frame
	 * also bind closeApp() to File -> Quit
	 * @param winEvt
	 */
	public void windowClosing(WindowEvent winEvt) {
		closeApp();
    }
	
	/**
	 * Properly shutdown application
	 */
	private void closeApp() {
		System.out.println("*** Caught exit request, shutting down properly.");
        mwallet.close();
        System.exit(0);
	}
	
	// initialization code

	public void addModel(ARWallet mwallet) {
		this.mwallet = mwallet;
	}

	public void addView(WalletView view) {
		this.view = view;
	}

	public void initModel(Object initParam) {
		return;
	}

}
