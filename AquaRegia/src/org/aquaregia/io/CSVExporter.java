package org.aquaregia.io;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.*;

/**
 * refer to https://community.oracle.com/thread/1357495?start=0&tstart=0
 * 
 * @author Yiyang
 * 
 */

public class CSVExporter extends Object {

	public JTable source;
	public File csvFile;

	public CSVExporter(JTable source) {
		this.source = source;
	}

	/*
	 * public CSVExporter(JTable source, boolean isDefault) { super();
	 * this.source = source; this.isDefault = isDefault; obtainFileName(); }
	 * 
	 * private void obtainFileName() { cancelOp = false; FileNameExtensionFilter
	 * filter = new FileNameExtensionFilter( "Excel format (CSV)", "csv"); }
	 */

	public void storeTableAsCSV(File target, JTable src) {
		String csvData = "";
		int count = src.getModel().getColumnCount();
		for (int i = 0; i < count - 1; i++) {
			csvData += src.getModel().getColumnName(i) + ",";
		}

		csvData += src.getModel().getColumnName(count - 1) + "\n\n";

		for (int i = 0; i < src.getModel().getRowCount(); i++) {
			for (int x = 0; x < src.getModel().getColumnCount(); x++) {
				int col = src.convertColumnIndexToView(x);
				String curVal = (String) src.getModel().getValueAt(i, col);

				if (curVal == null) {
					curVal = "";
				}

				csvData = csvData + removeAnyCommas(curVal) + ",";

				if (x == src.getModel().getColumnCount() - 1) {
					csvData = csvData + "\n";

				}

			}

			try {
				FileWriter writer = new FileWriter(target);
				writer.write(csvData);
				writer.flush();
				writer.close();
			} catch (IOException ioe) {
				JOptionPane.showMessageDialog(source,
						"Error Writing File.\nFile"
								+ "\nCheck and try re-exporting",
						"Export Error", JOptionPane.ERROR_MESSAGE);
			}

		}
	}

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

}
