package org.aquaregia.ui;

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
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.aquaregia.wallet.BitcoinAmount;
import org.aquaregia.wallet.ModelUpdate;

/**
 * Bitcoin wallet GUI window
 * Draw the background and tabs
 * @author Yiyang Hu
 */
public class WalletView extends JFrame implements Observer {

	// TODO initialize GUI here
	public WalletView() {
		initUI();
		this.setVisible(true);
	}

	private void initUI() {

		JPanel panel = new JPanel();
		getContentPane().add(panel);

		panel.setLayout(null);
		addTabs();

		setTitle("Aqua Regia");
		setSize(700, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	private void addTabs() {
		// creating tabbed pane and tabs and contents

		JTabbedPane tabbedPane = new JTabbedPane();
		
		JComponent send = new SendTab();
		tabbedPane.addTab("send", send);
	
		JComponent receive = new ReceiveTab();
		tabbedPane.addTab("receive", receive);

		JComponent history = new HistoryTab();
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
				// TODO send to UI
				break;
			case EXCHANGE_RATE:
				break;
			case HISTORY:
				break;
			case OWNED_ADDRESSES:
				break;
			case SHOW:
				break;
			default:
				break;
		}
	}

	public void addController(Controller controller) {
		// TODO bind all event handlers into controller
		// such as controller.addActionLister(buttonObj)
		// that means they need to be fields
	}
}
