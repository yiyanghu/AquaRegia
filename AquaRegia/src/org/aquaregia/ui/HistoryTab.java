package org.aquaregia.ui;

import java.awt.BorderLayout;
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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Draw the history tab
 * @author Yiyang Hu
 */


public class HistoryTab extends JPanel {
	
	private JTable table;
	private TransactionHistoryModel model;
	
	public HistoryTab(){
		this.setLayout(null);
		Insets insets = this.getInsets();
		addHistoryTable(insets);
	}
	
	
	private void addHistoryTable(Insets insets){
		String[] columnNames = {"Status","Date","Description","Amount (BTC)","Balance (BTC)"};
		
		Object[][] data = {};
		
		JTable table= new JTable(data,columnNames);
		JTableHeader header = table.getTableHeader();
		//header.setBackground(Color.yellow);
		JPanel panel= new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(header,BorderLayout.NORTH);
		panel.add(table,BorderLayout.CENTER);
		panel.setBounds(5+insets.left,5+insets.top, 700,600);
		this.add(panel);

	}
	
	private class TransactionHistoryModel extends DefaultTableModel{
		@Override 
		public boolean isCellEditable(int row, int col){
				return false;
		}
	}
		
}


