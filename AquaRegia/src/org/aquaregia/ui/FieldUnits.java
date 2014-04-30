package org.aquaregia.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * The FieldUnits class suggests units to a JTextField.
 * 
 * Adapted from: http://tips4java.wordpress.com/2009/11/29/text-prompt/
 * to always hold text
 */
public class FieldUnits extends JLabel {

	private JTextComponent component;

	public FieldUnits(String text, JTextComponent component) {
		this.component = component;

		setText(text);
		setFont(component.getFont());
		setForeground(component.getForeground());
		setBorder(new EmptyBorder(component.getInsets()));
		setHorizontalAlignment(JLabel.LEADING);

		// defaults
		setHorizontalAlignment(JLabel.RIGHT);
		changeAlpha(0.75f);
		changeStyle(Font.BOLD);
		
		component.setLayout(new BorderLayout());
		component.add(this);
	}

	/**
	 * Convenience method to change the alpha value of the current foreground
	 * Color to the specifice value.
	 * 
	 * @param alpha
	 *            value in the range of 0 - 1.0.
	 */
	public void changeAlpha(float alpha) {
		changeAlpha((int) (alpha * 255));
	}

	/**
	 * Convenience method to change the alpha value of the current foreground
	 * Color to the specifice value.
	 * 
	 * @param alpha
	 *            value in the range of 0 - 255.
	 */
	public void changeAlpha(int alpha) {
		alpha = alpha > 255 ? 255 : alpha < 0 ? 0 : alpha;

		Color foreground = getForeground();
		int red = foreground.getRed();
		int green = foreground.getGreen();
		int blue = foreground.getBlue();

		Color withAlpha = new Color(red, green, blue, alpha);
		super.setForeground(withAlpha);
	}

	/**
	 * Convenience method to change the style of the current Font. The style
	 * values are found in the Font class. Common values might be: Font.BOLD,
	 * Font.ITALIC and Font.BOLD + Font.ITALIC.
	 * 
	 * @param style
	 *            value representing the the new style of the Font.
	 */
	public void changeStyle(int style) {
		setFont(getFont().deriveFont(style));
	}

}