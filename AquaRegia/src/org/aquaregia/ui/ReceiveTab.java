package org.aquaregia.ui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.JTableHeader;



/**
 * Draw the receive tab
 * @author Yiyang Hu
 */


public class ReceiveTab extends JPanel {
	
	/*
	 * This is the constructor for the receiving tab
	 * It displays the default address, asking for transaction description and amount
	 * It also shows the table of all receiving addresses
	 */
	
	public ReceiveTab(){
				
		this.setLayout(null);
		Insets insets = this.getInsets();
		
		addLabel(insets, "Address", 100, 40, 80, 30);

		addTextField(insets,"To-do: this should display automatically of the address",200,40,250,30);
		
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
	
	private void addTextField(Insets insets, String name, int left, int top,
			int width, int height) {
		JTextField text = new JTextField(name);
		text.setBounds(left+insets.left,top+insets.top,width,height);
		this.add(text);
	}
	
	private void addAddressTable(Insets insets){
		String[] columnNames = {"Description","Address"};
		
		Object[][] data = {{"random comment","0101010101"},{"second legit comment","987654321"}};
		
		JTable table= new JTable(data,columnNames);
		JTableHeader header = table.getTableHeader();
		//header.setBackground(Color.yellow);
		JPanel panel= new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(header,BorderLayout.NORTH);
		panel.add(table,BorderLayout.CENTER);
		panel.setBounds(100+insets.left,220+insets.top, 500,300);
		this.add(panel);

	}
		

}
