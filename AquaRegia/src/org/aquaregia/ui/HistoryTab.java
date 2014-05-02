package org.aquaregia.ui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import org.aquaregia.wallet.history.SimpleTransactionDetails;
import org.aquaregia.wallet.history.TransactionHistory;
import org.jdesktop.swingx.JXTable;


/**
 * Draw the history tab
 * @author Yiyang Hu
 */


public class HistoryTab extends JPanel {
	
	public JXTable table;
	private TransactionHistoryModel model;
	private String[] columnNames;
	
	public HistoryTab(){
		addHistoryTable();
	}
	
	/**
	 *  the helper function to draw the history tab with status, date, description, 
	 *  amount, and balance under Miglayout
	 */
	
	private void addHistoryTable()  {
		this.setLayout(new MigLayout());
		
		columnNames = new String[] {"Status","Date","Description","Amount (BTC)","Balance (BTC)"};
		
		Object[][] data = {};
		
		model = new TransactionHistoryModel();
		model.setDataVector(data,columnNames);
		table = new JXTable(model);	
		table.setSortable(false);
		table.setHorizontalScrollEnabled(true);
		table.setAutoCreateColumnsFromModel(false);
		table.getTableHeader().setReorderingAllowed(false);
		
		table.getColumn("Status").setMaxWidth(100);
		table.getColumn("Date").setMaxWidth(230);

		table.getColumn("Amount (BTC)").setMaxWidth(220);
		table.getColumn("Balance (BTC)").setMaxWidth(220);
		
		table.getColumn("Description").setMinWidth(310);
		table.getColumn("Description").setPreferredWidth(310);
		
		tableAdjust();
		JScrollPane tableScrollPane=  new JScrollPane(table);
		
		table.setFillsViewportHeight(true);
		this.add(tableScrollPane,"push, grow");
	}
	
	private void tableAdjust() {
		for (int i = 0; i < 5; i++) {
			if (i != 2)
				table.packColumn(i, 5);
		}
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
		tableAdjust();
	}
	
	private class TransactionHistoryModel extends DefaultTableModel {
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


