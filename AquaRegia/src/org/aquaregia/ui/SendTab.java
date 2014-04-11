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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * Draw the send tab
 * @author Yiyang Hu
 */

public class SendTab extends JPanel{


	/*
	 * This is the constructor for the sending tab
	 * It has three components: address, description and amount
	 */
	public SendTab(){
		
		this.setLayout(null);
		Insets insets = this.getInsets();
		
		JLabel address = new JLabel("Address");
		address.setBounds(120+insets.left,40+insets.top,80,30);
		this.add(address);
		
		// creating address with textfield
		JTextField addressInput = new JTextField("address should go here", 25);
		// addressInput.setPreferredSize(new Dimension(35,25));
		addressInput.setBounds(200 + insets.left, 40 + insets.top, 250, 30);
		this.add(addressInput);
		
		JLabel description = new JLabel("Description");
		description.setBounds(120+ insets.left, 100+ insets.top, 80,30);
		this.add(description);
		
		// creating description with textfield
		JTextField descriContents = new JTextField("describe the transaction here");
		descriContents.setBounds(200+insets.left,100+insets.top,350,30);
		this.add(descriContents);
		
		//creating the amount with textfield
		//TO-DO: hide the equation for $ if net connecting to the network
		JLabel amount = new JLabel("Amount");
		amount.setBounds(120 + insets.left,160+insets.top,100,30 );
		this.add(amount);
		
		JTextField transactionAmount = new JTextField();
		transactionAmount.setBounds(200+insets.left, 160+insets.top, 160,30);
		this.add(transactionAmount);

		JLabel bitcoin = new JLabel("BTC");
		bitcoin.setBounds(380+insets.left,160+insets.top, 30,30);
		this.add(bitcoin);
		
		JButton sendButton = new JButton("send");
		sendButton.setBounds(420+insets.left,160+insets.top,70,30);
		this.add(sendButton);
	
	}
}
