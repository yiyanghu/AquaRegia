package org.aquaregia.ui;

import java.util.Observable;
import java.util.Observer;

/**
 * Bitcoin wallet GUI window
 * @author Yiyang Hu
 */
public class WalletView implements Observer {
	
	public WalletView() {
		// TODO initialize GUI here
	}

	/**
	 * Called by model (ARWallet) to update state
	 * All from the application is received form here
	 */
	@Override
	public void update(Observable o, Object update) {
		// TODO implement updates to UI
		// see ModelUpdate for format of the update object
	}

	public void addController(Controller controller) {
		// TODO bind all event handlers into controller
		// such as controller.addActionLister(buttonObj)
		// that means they need to be fields
	}
}
