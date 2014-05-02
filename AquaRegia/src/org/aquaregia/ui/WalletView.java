package org.aquaregia.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
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

import net.miginfocom.swing.MigLayout;

import org.aquaregia.wallet.BitcoinAmount;
import org.aquaregia.wallet.ModelUpdate;
import org.aquaregia.wallet.addressbook.AddressBook;
import org.aquaregia.wallet.history.SimpleTransactionDetails;
import org.aquaregia.wallet.history.TransactionHistory;
import org.h2.expression.Comparison;

/**
 * Bitcoin wallet GUI window
 * Draw the background and tabs
 * @author Yiyang Hu
 */
public class WalletView extends JFrame implements Observer {
	

	private JLabel balance;
	private JLabel exchangeRate;
	private String exchSymbol = "";
	private BigDecimal exchRate = BigDecimal.ZERO;
	private BitcoinAmount curBalance = new BitcoinAmount(BigInteger.ZERO);
	public Menu menuBar;
	public SendTab send;
	public ReceiveTab receive;
	public HistoryTab history;
	
	private static final String UP = "<font color='green'>\u25B2</font>";
	private static final String DOWN = "<font color='red'>\u25BC</font>";

	// TODO initialize GUI here
	public WalletView() {
		initUI();
	}
	
	private void showWindow() {
		this.setVisible(true);
	}

	private void initUI() {

		setLayout(new MigLayout());
		menuBar = new Menu();
		this.setJMenuBar(menuBar);
		

		addBalance();
		addTabs();

		setTitle(Strings.appname);
		setSize(1024, 768);
		setMinimumSize(new Dimension(520, 350));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		

	}
	
	private void addBalance(){
		balance = new JLabel("");
		add(balance,"");
		exchangeRate = new JLabel("");
		add(exchangeRate, "pushx, align right, wrap");
	}
	
	private void updateBalance(BitcoinAmount amt){
		String text = "Balance:  "+amt.coins()+"  BTC";
		if (!exchRate.equals(BigDecimal.ZERO)) {
			text += " (" + exchSymbol;
			text += amt.scale(BitcoinAmount.B.COIN).multiply(exchRate).setScale(2, RoundingMode.HALF_EVEN);
			text += ")";
		}
		curBalance = amt;
		balance.setText(text);
	}
	
	private void updateExchangeRate(BigDecimal unitsPerBTC, String symbol, String source) {
		String diffChar = "";
		switch (unitsPerBTC.compareTo(exchRate)) {
			case -1:
				diffChar = DOWN;
				break;
			case 1:
				diffChar = UP;
				break;
			default:
				break;
		}
		String text = "Exchange rate: ";
		text += symbol + unitsPerBTC.setScale(2, RoundingMode.HALF_EVEN);
		text += "/" + "BTC";
		if (!exchRate.equals(BigDecimal.ZERO) && diffChar.length() > 0)
			text += " " + diffChar;
		if (source != null)
			text += " (" + source + ")";
		if (unitsPerBTC.equals(BigDecimal.ZERO))
			text = "";
		exchangeRate.setText("<html>"+text+"</html>");
		exchangeRate.setToolTipText("Retrieved at: "+ new Date());
		exchRate = unitsPerBTC;
		exchSymbol = symbol;
		updateBalance(curBalance);
	}
	
	private void updateName(String name) {
		String title = Strings.appname + " " + Strings.appversion + " - " + name;
		this.setTitle(title);
	}

	private void addTabs() {
		// creating tabbed pane and tabs and contents

		JTabbedPane tabbedPane = new JTabbedPane();
		
		send = new SendTab();
		tabbedPane.addTab("Send", send);
	
		receive = new ReceiveTab();
		tabbedPane.addTab("Receive", receive);

		history = new HistoryTab();
		tabbedPane.addTab("History", history);

		add(tabbedPane,"grow, push, span");

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
				BigDecimal exchangeRate = (BigDecimal) up[1];
				String symbol = (String) up[2];
				String source = (String) up[3];
				updateExchangeRate(exchangeRate, symbol, source);
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
