package org.aquaregia.ui;

import java.text.NumberFormat;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

public class Common {

	public static class DecimalFilter extends DocumentFilter {
		private static final String REGEX_DEC = "^(\\d)*(\\.)?(\\d)*$";
		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset,
				String string, AttributeSet attr) throws BadLocationException {

			Document doc = fb.getDocument();
			StringBuilder fulltext = new StringBuilder(doc.getText(0, doc.getLength()));
			fulltext.insert(offset, string);
			String newStr = fulltext.toString();
			if (!newStr.matches(REGEX_DEC)) {
				fail();
				return;
			}
			super.insertString(fb, offset, string, attr);
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset,
				int length, String text, AttributeSet attrs)
				throws BadLocationException {
			Document doc = fb.getDocument();
			StringBuilder fulltext = new StringBuilder(doc.getText(0, doc.getLength()));
			fulltext.replace(offset, offset+length, text);
			String newStr = fulltext.toString();
			if (!newStr.matches(REGEX_DEC)) {
				fail();
				return;
			}
			super.replace(fb, offset, length, text, attrs);
		}
		
		private void fail() {
			
		}
	}
}
