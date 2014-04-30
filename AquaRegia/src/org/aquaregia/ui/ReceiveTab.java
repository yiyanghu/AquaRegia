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

import net.miginfocom.swing.MigLayout;

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
	
	
	public ReceiveTab() {
				
		this.setLayout(new MigLayout());
		
		add(new JLabel("Address"));

		address = new JTextField("");
		add(address,"wrap, growx, pushx, align left");
		
		sendButton = new JButton("Generate Address");

		add(new JLabel("Description"));
		
		add(new JTextField("describe the transaction here"), "wrap, growx, align left");
		
		add(new JLabel("Amount"));
		
		add(new JTextField(""),"growx");
		
		add(new JLabel("BTC"),"wrap");
		
		addAddressTable();
	
	}

	private void addAddressTable(){
		columnNames = new String[] {"Description","Address"};
		
		Object[][] data = {};
		
		tableModel = new AddressTableModel();
		tableModel.setDataVector(data,columnNames);
		table = new JTable(tableModel);
		
		JPanel panel= new JPanel();
		panel.setLayout(new BorderLayout());
		JScrollPane tableScrollPane=  new JScrollPane(table);
		table.setFillsViewportHeight(true);
		panel.add(tableScrollPane,BorderLayout.CENTER);
		this.add(panel,"span, grow, pushy");

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
