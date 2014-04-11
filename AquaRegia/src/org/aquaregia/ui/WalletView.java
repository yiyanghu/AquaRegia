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

/**
 * Bitcoin wallet GUI window
 * 
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
		addTab();

		setTitle("Aqua Regia");
		setSize(700, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

	}

	private void addTab() {
		// creating tabbed pane and tabs and contents

		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent send = makeTextPanel("The contents of send");
		send.setLayout(null);
		tabbedPane.addTab("send", send);

		Insets insets = send.getInsets();
		// creating address with textfield
		JTextField addressInput = new JTextField("address should go here", 25);
		// addressInput.setPreferredSize(new Dimension(35,25));
		addressInput.setBounds(200 + insets.left, 40 + insets.top, 200, 30);
		send.add(addressInput);

		// creating description with textarea
		JTextArea description = new JTextArea("Describe the transaction");
		description.setBounds(200+insets.left,100+insets.top,300,300);
		send.add(description);

		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		JComponent receive = makeTextPanel("The conetents of receive");
		tabbedPane.addTab("receive", receive);
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

		JComponent history = makeTextPanel("The contents of transaction history");
		tabbedPane.addTab("history", history);
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);

		add(tabbedPane);

		// enable to use scrolling tabs
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

	}

	private void addSendContents() {

	}

	protected JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		// JLabel filler = new JLabel(text);
		// filler.setHorizontalAlignment(JLabel.CENTER);
		// panel.setLayout(new GridLayout(1,1));
		// panel.add(filler);
		return panel;

	}

	/**
	 * Called by model (ARWallet) to update state All from the application is
	 * received form here
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
