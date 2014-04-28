package org.aquaregia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.aquaregia.wallet.BitcoinAmount;
import org.aquaregia.wallet.ModelUpdate;
import org.aquaregia.wallet.addressbook.AddressBook;
import org.aquaregia.wallet.history.SimpleTransactionDetails;
import org.aquaregia.wallet.history.TransactionHistory;

/**
 * Bitcoin wallet GUI window
 * Draw the background and tabs
 * @author Yiyang Hu
 */
public class WalletView extends JFrame implements Observer {
	

	private JLabel balance;
	public Menu menuBar;
	public SendTab send;
	public ReceiveTab receive;
	public HistoryTab history;

	// TODO initialize GUI here
	public WalletView() {
		initUI();
	}
	
	private void showWindow() {
		this.setVisible(true);
	}

	private void initUI() {

		JPanel panel = new JPanel();
		menuBar = new Menu();
		this.setJMenuBar(menuBar);
		
		panel.setLayout(null);
		addBalance();
		addTabs();

		setTitle(Strings.appname);
		setSize(700, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}
	
	private void addBalance(){
		balance = new JLabel("");
		Insets insets = this.getInsets();
		balance.setBounds(15+insets.left,4+insets.top,200,38);
		add(balance);
	}
	
	private void updateBalance(BitcoinAmount amt){
		balance.setText("Balance  "+amt.coins()+"  BTC");
	}
	
	private void updateName(String name) {
		String title = Strings.appname + " " + Strings.appversion + " - " + name;
		this.setTitle(title);
	}

	private void addTabs() {
		// creating tabbed pane and tabs and contents

		JTabbedPane tabbedPane = new JTabbedPane();
		
		send = new SendTab();
		tabbedPane.addTab("send", send);
	
		receive = new ReceiveTab();
		tabbedPane.addTab("receive", receive);

		history = new HistoryTab();
		tabbedPane.addTab("history", history);

		add(tabbedPane);

		// enable to use scrolling tabs
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	}


	/**
	 * Called by model (ARWallet) to update state All from the application is
	 * received form here
	 */
	@Override
	public void update(Observable obsv, Object update) {
		// TODO implement updates to UI
		// see ModelUpdate for format of the update object
		Object[] up = (Object[]) update;
		ModelUpdate type = (ModelUpdate) up[0];
		switch(type) {
			case BALANCE:
				BitcoinAmount bal = (BitcoinAmount) up[1];
				updateBalance(bal);
				break;
			case NAME:
				updateName((String) up[1]);
				break;
			case EXCHANGE_RATE:
				break;
			case HISTORY:
				TransactionHistory trans = (TransactionHistory) up[1];
				history.updateTransactionTable(trans);
				break;
			case OWNED_ADDRESSES:
				AddressBook addresses = (AddressBook) up[1];
				receive.updateTable(addresses);		
				break;
			case SHOW:
				showWindow();
				break;
			default:
				break;
		}
	}

	public void addController(Controller controller) {
		// TODO bind all event handlers into controller
		// such as buttonObj.addActionLister(controller)
		// that means they need to be fields
		this.addWindowListener(controller);
		receive.addController(controller);
		send.addController(controller);
		menuBar.addController(controller);
		controller.viewReady();
	}
}
