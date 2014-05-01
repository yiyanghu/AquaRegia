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
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import net.miginfocom.swing.MigLayout;

import org.aquaregia.wallet.BitcoinAmount;
import org.aquaregia.wallet.addressbook.AddressBook;
import org.aquaregia.wallet.history.SimpleTransactionDetails;
import org.aquaregia.wallet.history.TransactionHistory;


/**
 * Draw the history tab
 * @author Yiyang Hu
 */


public class HistoryTab extends JPanel {
	
	private JTable table;
	private TransactionHistoryModel model;
	private String[] columnNames;
	
	public HistoryTab(){
		addHistoryTable();
	}
	
	private void addHistoryTable()  {
		this.setLayout(new MigLayout());
		
		columnNames = new String[] {"Status","Date","Description","Amount (BTC)","Balance (BTC)"};
		
		Object[][] data = {};
		
		model = new TransactionHistoryModel();
		model.setDataVector(data,columnNames);
		table = new JTable(model);	
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateColumnsFromModel(false);
		
		/**/
		
		table.getColumn("Status").setPreferredWidth(100);
		table.getColumn("Date").setPreferredWidth(230);
		table.getColumn("Description").setPreferredWidth(300);
		table.getColumn("Amount (BTC)").setPreferredWidth(100);
		table.getColumn("Balance (BTC)").setPreferredWidth(100);
				
		
		JScrollPane tableScrollPane=  new JScrollPane(table);
		
		table.setFillsViewportHeight(true);
		this.add(tableScrollPane,"push, grow");
	}
	
	public void updateTransactionTable (TransactionHistory history){
		Object[][] data = new Object[history.size()][];
		for (int i=0;i<history.size();i++){
			SimpleTransactionDetails trans= history.get(i);
			String netTrans =  trans.getNetBalanceChange().coins();
			String balance = trans.getEventTotal().coins();
			Object[] row ={trans.confidenceString(),trans.getTxTime(),
					trans.description(),netTrans,balance};
			data[i] = row;
		}
		
		model.setDataVector(data, columnNames);
	}
		

	
	private class TransactionHistoryModel extends DefaultTableModel{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override 
		public boolean isCellEditable(int row, int col){
				return false;
		}
	}
		
}


