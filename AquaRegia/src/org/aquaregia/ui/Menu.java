package org.aquaregia.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Menu extends JMenuBar {

	private final int ITEM_PLAIN = 0;
	private final int ITEM_RADIO = 1;
	private final int ITEM_CHECK = 2;

	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenu menuWallet;
	private JMenu menuHelp;
	private JMenuItem menuFileNew;
	private JMenuItem menuFileOpen;
	private JMenuItem menuFileQuit;
	private JMenuItem menuWalletSeed;
	private JMenuItem menuWalletMPK;
	private JMenuItem menuWalletExportHistory;
	private JMenuItem menuHelpAbout;
	private JMenuItem menuHelpWebsite;

	public Menu() {



		// create the menu bar
		menuBar = new JMenuBar();

		// create the file menu
		menuFile = new JMenu("File");
		menuFile.setMnemonic('F');
		menuBar.add(menuFile);

		// build a file menu items
		menuFileNew = CreateMenuItem(menuFile, ITEM_PLAIN, "New", 'N');
		menuFileOpen = CreateMenuItem(menuFile,ITEM_PLAIN,"Open...",'O');
		menuFileQuit = CreateMenuItem(menuFile,ITEM_PLAIN,"Quit",'Q');
		menuFile.addSeparator();
		
		// create the wallet menu
		menuWallet = new JMenu("Wallet");
		menuWallet.setMnemonic('W');
		menuBar.add(menuWallet);
		
		// build the wallet menu options
		menuWalletSeed = CreateMenuItem(menuWallet,ITEM_PLAIN,"Seed",'S');
		menuWalletMPK = CreateMenuItem(menuWallet,ITEM_PLAIN,"Master Public Key",'M');
		menuWalletExportHistory = CreateMenuItem(menuWallet,ITEM_PLAIN,"Export history",'E');
		menuFile.addSeparator();
		
		// create the help menu
		menuHelp = new JMenu("Help");
		menuHelp.setMnemonic('H');
		menuBar.add(menuHelp);
		
		// build the help menu options
		menuHelpAbout = CreateMenuItem(menuHelp,ITEM_PLAIN,"About",'A');
		menuHelpWebsite = CreateMenuItem(menuHelp,ITEM_PLAIN,"Website",'W');
		menuHelp.addSeparator();
		
		
	}

	public JMenuItem CreateMenuItem(JMenu menu, int type, String text,
			int acceleratorKey) {

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
		if (acceleratorKey > 0)
			menuItem.setMnemonic(acceleratorKey);
		
		//menuItem.addActionListener((ActionListener) this);
		
		menu.add(menuItem);
		
		return menuItem;
			
	}
	
	

}
