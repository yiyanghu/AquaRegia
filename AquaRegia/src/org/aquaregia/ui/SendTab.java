package org.aquaregia.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import org.aquaregia.ui.components.TextFieldUnits;

import net.miginfocom.swing.MigLayout;

/**
 * Draw the send tab
 * 
 * @author Yiyang Hu
 */

public class SendTab extends JPanel {

	private JButton sendButton;
	public JTextField address;
	public JTextField amount;
	private JTextField description;
	private TextFieldUnits amountFU;

	/*
	 * This is the constructor for the sending tab It has three components:
	 * address, description and amount It also has a send button
	 */
	public SendTab() {

		this.setLayout(new MigLayout("","[][][]","[]15[]15[]15[]5[]5[]"));
		add(new JLabel("Address:"));

		address = new JTextField("");
		address.setBackground(Color.WHITE);
		add(address,"growx, width :580:580 , wrap");
		
		add(new JLabel("Description:"));
		description = new JTextField("describe the transaction here");
		description.setEnabled(false); // <- disabled feature
		add(description, "wrap, growx, width :580:580, wrap");


		add(new JLabel("Amount:"));
		
		amount = new JTextField("");
		amountFU = new TextFieldUnits("", amount);
		amountFU.setText("BTC");
		add(amount,"growx, width :275:275, span, split 2");

		sendButton = new JButton("Send");
		add(sendButton,"");

	}


	public void addController(Controller controller) {
		sendButton.addActionListener(controller.sCHandler);
	}

}
