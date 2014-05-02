package org.aquaregia.ui;

import javax.swing.*;

/**
 * Draws all the menu item
 * @author Yiyang
 *
 */

public class Menu extends JMenuBar {

	private final int ITEM_PLAIN = 0;
	private final int ITEM_RADIO = 1;
	private final int ITEM_CHECK = 2;

	//private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuWallet;
	private JMenu menuHelp;
	public JMenuItem menuFileNew;
	public  JMenuItem menuFileOpen;
	public JMenuItem menuFileQuit;
	public JMenuItem menuWalletSeed;
	public JMenuItem menuWalletPassword;
	public JMenuItem menuWalletMPK;
	public JMenuItem menuWalletExportHistory;
	public JMenuItem menuHelpAbout;
	public JMenuItem menuHelpWebsite;

	public Menu() {
		// create the file menu
		menuFile = new JMenu("File");
		menuFile.setMnemonic('F');
		this.add(menuFile);

		// build a file menu items
		menuFileNew = createMenuItem(menuFile, ITEM_PLAIN, "New", 'N');
		menuFileOpen = createMenuItem(menuFile,ITEM_PLAIN,"Open...",'O');
		menuFileQuit = createMenuItem(menuFile,ITEM_PLAIN,"Quit",'Q');
		
		// create the wallet menu
		menuWallet = new JMenu("Wallet");
		menuWallet.setMnemonic('W');
		this.add(menuWallet);
		
		// build the wallet menu options
		menuWalletSeed = createMenuItem(menuWallet,ITEM_PLAIN,"Seed",'S');
		menuWalletMPK = createMenuItem(menuWallet,ITEM_PLAIN,"Master Public Key",'M');
		menuWalletPassword = createMenuItem(menuWallet,ITEM_PLAIN,"Password",'P');
		menuWalletExportHistory = createMenuItem(menuWallet,ITEM_PLAIN,"Export history",'E');		
		
		// create the help menu
		menuHelp = new JMenu("Help");
		menuHelp.setMnemonic('H');
		this.add(menuHelp);
		
		// build the help menu options
		menuHelpAbout = createMenuItem(menuHelp,ITEM_PLAIN,"About",'A');
		menuHelpWebsite = createMenuItem(menuHelp,ITEM_PLAIN,"Website",'W');
	
		
	}

	public JMenuItem createMenuItem(JMenu menu, int type, String text,
			int mnemonic) {

		// create the item
		JMenuItem menuItem;
		
		switch(type){
		
		case ITEM_RADIO:
			menuItem = new JRadioButtonMenuItem();
			break;
		case ITEM_CHECK:
			menuItem = new JCheckBoxMenuItem();
			break;
		default:
			menuItem = new JMenuItem();
			break;
		
		}
		
		// add the item text
		menuItem.setText(text);
		
		// add the accelerator key
		if (mnemonic > 0)
			menuItem.setMnemonic(mnemonic);
		
		//menuItem.addActionListener((ActionListener) this);
		
		menu.add(menuItem);
		
		return menuItem;
			
	}
	
	/**
	 * Add controller to all the menu items
	 * @param controller for menu handler
 	 */
	public void addController(Controller controller) {
		
		menuFileNew.addActionListener(controller.mHandler);
		menuFileOpen.addActionListener(controller.mHandler);
		menuFileQuit.addActionListener(controller.mHandler);
		menuWalletSeed.addActionListener(controller.mHandler);
		menuWalletMPK.addActionListener(controller.mHandler);
		menuWalletPassword.addActionListener(controller.mHandler);
		menuWalletExportHistory.addActionListener(controller.mHandler);
		menuHelpAbout.addActionListener(controller.mHandler);
		menuHelpWebsite.addActionListener(controller.mHandler);
		
		
	}
	
	

}
