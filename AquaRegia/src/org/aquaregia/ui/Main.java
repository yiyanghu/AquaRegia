package org.aquaregia.ui;

import org.aquaregia.wallet.ARWallet;

public class Main {
	final public static Object INIT_PARAM = null;
	
	private ARWallet mwallet = new ARWallet();
	private WalletView view = new WalletView();
	
	public Main() {
		mwallet.addObserver(view);
		
		Controller controller = new Controller();
		controller.addModel(mwallet);
		controller.addView(view);
		controller.initModel(INIT_PARAM);
		
		view.addController(controller);
	}
}
