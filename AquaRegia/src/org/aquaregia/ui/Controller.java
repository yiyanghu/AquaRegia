package org.aquaregia.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.aquaregia.wallet.ARWallet;
import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.InsufficientMoneyException;


/**
 * Binds wallet implementation to interface
 * @author Stephen Halm
 */
public class Controller implements WindowListener {

	private ARWallet mwallet;
	private WalletView view;
	
	public GenerateKeyHandler gKHandler;
	public SendCoinHandler sCHandler;
	
	public Controller() {
		gKHandler = new GenerateKeyHandler();
		sCHandler = new SendCoinHandler();
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
		//mwallet.viewOpened();
	}
	
	// UI init
	
	public void viewReady() {
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

	// event handler for generating new key button
	public class GenerateKeyHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			mwallet.addAddress();
		}
		
	}
	
	// event handler for send coin button in the send tab
	public class SendCoinHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e){
			// send the address and amount
			BitcoinAmount sendAmount= new BitcoinAmount(BitcoinAmount.B.COIN,
											view.send.amount.getText());
			try {
				
				mwallet.simpleSendCoins(sendAmount, view.send.address.getText());
			} catch (AddressFormatException e1) {
				// error message window for wrong address format
				JOptionPane.showMessageDialog(view,"Invalid Address",
						"Please check your address is in the correct format",JOptionPane.ERROR_MESSAGE);	
			} catch (InsufficientMoneyException e1) {
				// error message window for inefficient money
				BitcoinAmount missingValue = new BitcoinAmount(e1.missing);
				JOptionPane.showMessageDialog(view,"Insufficient amount",
						"You are missing " + missingValue.coins()+ " BTC",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
