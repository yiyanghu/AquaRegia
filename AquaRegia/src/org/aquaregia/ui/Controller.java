package org.aquaregia.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.aquaregia.wallet.ARWallet;
import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.InsufficientMoneyException;
import com.google.bitcoin.core.Transaction;


/**
 * Binds wallet implementation to interface
 * @author Stephen Halm and Yiyang Hu
 */
public class Controller implements WindowListener {

	private ARWallet mwallet;
	private WalletView view;
	
	public GenerateKeyHandler gKHandler;
	public SendCoinHandler sCHandler;
	public AddressSelectionHandler addressSelectionHandler;
	public MenuHandler mHandler;
	
	
	public Controller() {
		gKHandler = new GenerateKeyHandler();
		sCHandler = new SendCoinHandler();
		addressSelectionHandler = new AddressSelectionHandler();
		mHandler = new MenuHandler();
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
	
	// event handler for clicking on the address and displaying in textfield
	public class AddressSelectionHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			JTable table = view.receive.table;
			String address= (String) table.getValueAt(table.getSelectedRow(), 1);
			view.receive.address.setText(address);
			
		}

	}
	

	// event handler for generating new key button
	public class GenerateKeyHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			mwallet.addAddress();
		}
		
	}
	
	// event handler for menu bar
	public class MenuHandler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			Menu menu = view.menuBar;	
			// to-do: different items got selected
			
			if(e.getSource().equals(view.menuBar.menuFileQuit)){
				System.exit(0);
			}
			
			else if (e.getSource().equals(view.menuBar.menuFileOpen)){
				// to do
			}
			
			else if (e.getSource().equals(view.menuBar.menuWalletSeed)){
				JOptionPane seedWindow = new JOptionPane();
				seedWindow.setMessage("Your seed is");
				seedWindow.setMessageType(JOptionPane.INFORMATION_MESSAGE);
				seedWindow.setPreferredSize(new Dimension(500,400));
				
				JTextArea seedInfo = new JTextArea("default seed should be displayed");
				seedInfo.setEditable(false);
				JDialog dialog = seedWindow.createDialog(null,"Seed");
				dialog.setVisible(true);
				
				seedWindow.add(seedInfo);
				
			}
			
		}
		
		
	}
	
	// event handler for send coin button in the send tab
	public class SendCoinHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e){
			// send the address and amount

			try {
				BitcoinAmount sendAmount= new BitcoinAmount(BitcoinAmount.B.COIN,
											view.send.amount.getText());
				String address = view.send.address.getText();
				BitcoinAmount fee = new BitcoinAmount(Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
				BitcoinAmount total = new BitcoinAmount(sendAmount.add(fee));
				int okSend = JOptionPane.showConfirmDialog(view, "Are you sure you want to send "
						+ sendAmount.coins() + " BTC, which has a usual fee of " + fee.coins()
						+ " BTC, for a total of " + total.coins() + " BTC?",
						"Send Bitcoins?", JOptionPane.YES_NO_OPTION);
				if (okSend != JOptionPane.YES_OPTION)
					return;
				mwallet.simpleSendCoins(sendAmount, address);
			} catch (NumberFormatException e1) {
				// error message window for wrong amount format
				JOptionPane.showMessageDialog(view,"Please check your amount is a valid number of BTC.",
						"Invalid Amount", JOptionPane.ERROR_MESSAGE);
			} catch (AddressFormatException e1) {
				// error message window for wrong address format
				JOptionPane.showMessageDialog(view,"Please check your address is in the correct format.",
						"Invalid Address", JOptionPane.ERROR_MESSAGE);	
			} catch (InsufficientMoneyException e1) {
				// error message window for inefficient money
				BitcoinAmount missingValue = new BitcoinAmount(e1.missing);
				JOptionPane.showMessageDialog(view,"You are missing " + missingValue.coins()+ " BTC " +
						" necessary to complete the transaction.","Insufficient amount", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
}
