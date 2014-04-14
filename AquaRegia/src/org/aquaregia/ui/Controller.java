package org.aquaregia.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.aquaregia.wallet.ARWallet;

/**
 * Binds wallet implementation to interface
 * @author Stephen Halm
 */
public class Controller implements WindowListener {

	private ARWallet mwallet;
	private WalletView view;
	
	public GenerateKeyHandler gKHandler;
	
	public Controller() {
		gKHandler = new GenerateKeyHandler();
	}
	
	// Add Listener handlers here (with implements on this object)
	// also make calls to ARWallet based on such events from here
	// actions will NOT return UI changes to be made,
	// that will be routed to the view
	
	/**
	 * TODO <--- Make sure to bind this to the window closing of the app frame
	 * also bind closeApp() to File -> Quit
	 * @param winEvt
	 */
	@Override
	public void windowClosing(WindowEvent winEvt) {
		closeApp((JFrame) winEvt.getSource());
    }
	
	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		mwallet.viewOpened();
	}
	
	/**
	 * Properly shutdown application
	 */
	private void closeApp(JFrame f) {
		System.out.println("*** Caught exit request, shutting down properly.");
		// Clean up in the background
        f.setVisible(false);
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

	public class GenerateKeyHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			mwallet.addAddress();
		}
		
	}
	
}
