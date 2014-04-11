package org.aquaregia.ui;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.TextField;
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
	 * It also has a send button
	 */
	public SendTab(){
		
		this.setLayout(null);
		Insets insets = this.getInsets();
	
		addLabel(insets, "Address", 100, 40, 80, 30);
		
		addTextField(insets,"address should go here",200,40,250,30);
		
		addLabel(insets, "Description", 100, 100, 80, 30);
		
		addTextField(insets,"describe the transaction here",200,100,350,30);
		
		//creating the amount with textfield
		//TO-DO: hide the equation for $ if net connecting to the network
		addLabel(insets, "Amount", 100, 160, 100, 30);

		addTextField(insets,"",200,160,160,30);

		addLabel(insets, "BTC", 380, 160, 30, 30);
		
		JButton sendButton = new JButton("send");
		sendButton.setBounds(420+insets.left,160+insets.top,70,30);
		this.add(sendButton);
	
	}

	private void addLabel(Insets insets, String name, int left, int top,
			int width, int height) {
		JLabel label = new JLabel(name);
		label.setBounds(left+insets.left,top+insets.top,width,height);
		this.add(label);
	}
	
	private void addTextField(Insets insets, String name, int left, int top,
			int width, int height) {
		JTextField text = new JTextField(name);
		text.setBounds(left+insets.left,top+insets.top,width,height);
		this.add(text);
	}
	
	
	
	
}
