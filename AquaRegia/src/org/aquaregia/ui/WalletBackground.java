package org.aquaregia.ui;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.*;



public class WalletBackground extends JFrame{
	
	public WalletBackground(){
		initUI();
	}
	
	private void initUI(){
		
		JPanel panel = new JPanel();
		getContentPane().add(panel);
		
		panel.setLayout(null);
		
		JButton quitButton = new JButton("Cancel");
		quitButton.setBounds(420,520,80,30);
		
		quitButton.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event){
				System.exit(0);
			}
		});
		
		panel.add(quitButton);
		addTab();
		
		setTitle("Aqua Regia");
		setSize(700,600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		
	}
	
	private void addTab(){
		// creating tabbed pane and tabs and contents
		
		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent send = makeTextPanel("The contents of send");
		send.setLayout(null);
		tabbedPane.addTab("send", send);
		
		Insets insets= send.getInsets();
		// creating address with textfield
		JTextField addressInput= new JTextField("address should go here",25);
		//addressInput.setPreferredSize(new Dimension(35,25));
		addressInput.setBounds(200+insets.left,40+insets.top,80,50);
		send.add(addressInput);
		
		// creating description with textarea
		JTextArea description= new JTextArea("Describe the transaction");
		description.setPreferredSize(new Dimension(100,100));
		send.add(description);
		
		
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		
		JComponent receive= makeTextPanel("The conetents of receive");
		tabbedPane.addTab("receive", receive);
		tabbedPane.setMnemonicAt(1,	KeyEvent.VK_2);
		
		JComponent history = makeTextPanel("The contents of transaction history");
		tabbedPane.addTab("history",history);
		tabbedPane.setMnemonicAt(2,KeyEvent.VK_3);
		
		add(tabbedPane);
		
		// enable to use scrolling tabs
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		
	}
	
	private void addSendContents(){
		
	}
	
	protected JComponent makeTextPanel( String text){
		JPanel panel = new JPanel(false);
		//JLabel filler = new JLabel(text);
		//filler.setHorizontalAlignment(JLabel.CENTER);
		//panel.setLayout(new GridLayout(1,1));
		//panel.add(filler);
		return panel;
		
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				WalletBackground ex = new WalletBackground();
				ex.setVisible(true);
			}
		});
	}

}
