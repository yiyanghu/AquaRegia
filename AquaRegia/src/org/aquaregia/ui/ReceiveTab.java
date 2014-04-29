package org.aquaregia.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import org.aquaregia.wallet.addressbook.AddressBook;
import org.aquaregia.wallet.addressbook.AddressBookEntry;



/**
 * Draw the receive tab
 * @author Yiyang Hu
 */


public class ReceiveTab extends JPanel {
	
	private JButton sendButton;
	public JTextField address;
	public JTable table;
	private AddressTableModel tableModel;
	private String[] columnNames;
	
	/*
	 * This is the constructor for the receiving tab
	 * It displays the default address, asking for transaction description and amount
	 * It also shows the table of all receiving addresses
	 */
	
	
	public ReceiveTab(){
				
		this.setLayout(null);
		Insets insets = this.getInsets();
		
		addLabel(insets, "Address", 100, 40, 80, 30);

		address = addTextField(insets,"",200,40,250,30);
		
		
		sendButton = new JButton("Generate Address");
		sendButton.setBounds(500+insets.left,40+insets.top,150,30);
		this.add(sendButton);
		
		addLabel(insets, "Description", 100, 100, 80, 30);
		
		addTextField(insets,"describe the transaction here",200,100,350,30);
		
		addLabel(insets, "Amount", 100, 160, 100, 30);
		
		addTextField(insets,"",200,160,160,30);
		
		addLabel(insets, "BTC", 380, 160, 30, 30);
		
		addAddressTable(insets);
	
	}

	private void addLabel(Insets insets, String name, int left, int top,
			int width, int height) {
		JLabel label = new JLabel(name);
		label.setBounds(left+insets.left,top+insets.top,width,height);
		this.add(label);
	}
	
	private JTextField addTextField(Insets insets, final String name, int left, int top,
			int width, int height) {
		final JTextField text = new JTextField(name);
		text.setBounds(left+insets.left,top+insets.top,width,height);
		
		this.add(text);
		return text;
	}
	
	private void addAddressTable(Insets insets){
		columnNames = new String[] {"Description","Address"};
		
		Object[][] data = {};
		
		tableModel = new AddressTableModel();
		tableModel.setDataVector(data,columnNames);
		table = new JTable(tableModel);
		
		JPanel panel= new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBounds(100+insets.left,220+insets.top, 550,300);
		JScrollPane tableScrollPane=  new JScrollPane(table);
		table.setFillsViewportHeight(true);
		panel.add(tableScrollPane,BorderLayout.CENTER);
		this.add(panel);

	}
	
	public void updateTable(AddressBook addresses){
		Object[][] data = new Object[addresses.size()][];
		for (int i=0;i<addresses.size();i++){
			Object[] row={"",addresses.get(i).getAddress().toString()};
			data[i] = row;
			
		}
		tableModel.setDataVector(data,columnNames);
		
	}
	
	public void addController(Controller controller){
		sendButton.addActionListener(controller.gKHandler);
		table.getSelectionModel().addListSelectionListener(controller.addressSelectionHandler);
	}
	
	private class AddressTableModel extends DefaultTableModel {
		@Override
		public boolean isCellEditable(int row, int col){
			return false;		
		}
	}
		

}
