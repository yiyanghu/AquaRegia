package org.aquaregia.ui;

import java.util.Observable;
import java.util.Observer;

/**
 * Bitcoin wallet GUI window
 * @author 
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
	public void update(Observable o, Object arg) {
		// TODO 
		// There needs to be some kind of message passing
		// such as arg[0] = type of update string
		// arg[1..n] parameters as in weekly specification
	}

	public void addController(Controller controller) {
		// TODO bind all event handlers into controller
		// such as controller.addActionLister(buttonObj)
	}
}
