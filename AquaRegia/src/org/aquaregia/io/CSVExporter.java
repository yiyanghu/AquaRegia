package org.aquaregia.io;

import java.io.*;

import javax.swing.*;

/**
 * refer to https://community.oracle.com/thread/1357495?start=0&tstart=0 Exports
 * the current JTable with transaction history information to CSV format
 * 
 * @author Yiyang and Steve
 * 
 */

public class CSVExporter extends Object {

	public JTable source;

	public CSVExporter(JTable source) {
		this.source = source;
	}

	/**
	 * Write the contents of the table to a file
	 * 
	 * @param destination
	 *            of the file storate
	 */
	public void store(File dest) {
		String csvData = "";
		int count = source.getModel().getColumnCount();
		for (int i = 0; i < count; i++) {
			csvData += source.getModel().getColumnName(i);
			if (i != count - 1)
				csvData += ",";
			else
				csvData += "\n";
		}

		// traverse the contents and add "," between variables while delete
		// extra "," within
		for (int i = 0; i < source.getModel().getRowCount(); i++) {
			for (int x = 0; x < source.getModel().getColumnCount(); x++) {
				int col = source.convertColumnIndexToView(x);
				Object cell = source.getModel().getValueAt(i, col);
				String curVal;
				if (cell == null) {
					curVal = "";
				} else {
					curVal = cell.toString();
				}

				csvData += removeAnyCommas(curVal);

				if (x == source.getModel().getColumnCount() - 1) {
					csvData += "\n";

				} else {
					csvData += ",";
				}

			}
		}

		new FileSaverThread(dest, csvData).start();
	}

	/**
	 * 
	 * @param the source to remove the comma from
	 * @return modified source without extra comma in between
	 */
	private String removeAnyCommas(String src) {
		if (src == null) {
			return "";
		}

		for (int i = 0; i < src.length(); i++) {
			if (src.charAt(i) == ',') {
				src = src.substring(0, i) + src.substring(i + 1, src.length());
			}
		}

		return src;
	}

	private class FileSaverThread extends Thread {

		private File dest;
		private String data;

		public FileSaverThread(File dest, String data) {
			this.dest = dest;
			this.data = data;
		}

		@Override
		public void run() {
			boolean success;
			try {
				FileWriter writer = new FileWriter(dest);
				writer.write(data);
				writer.flush();
				writer.close();
				success = true;
			} catch (IOException ioe) {
				success = false;
			}

			SwingUtilities.invokeLater(new FileSaverStatus(success));
		} // end run()

		private class FileSaverStatus implements Runnable {
			private boolean success;

			public FileSaverStatus(boolean success) {
				this.success = success;
			}

			@Override
			public void run() {
				if (success) {
					JOptionPane.showMessageDialog(source,
							"Your file has been saved successfully.",
							"File saved", JOptionPane.INFORMATION_MESSAGE);

				} else {
					JOptionPane.showMessageDialog(source,
							"Error Writing File.\n" + dest + "\nPerhaps retry",
							"Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
}
