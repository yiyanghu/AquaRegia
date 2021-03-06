package org.aquaregia.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.aquaregia.io.BitcoinURI;
import org.aquaregia.io.CSVExporter;
import org.aquaregia.wallet.ARWallet;
import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.AddressFormatException;
import com.google.bitcoin.core.InsufficientMoneyException;
import com.google.bitcoin.core.Transaction;

/**
 * Binds wallet implementation to interface
 * 
 * @author Stephen Halm and Yiyang Hu
 */
public class Controller implements WindowListener {

	private ARWallet mwallet;
	private WalletView view;

	public CopyHandler copyHandler;
	public SendCoinHandler sCHandler;
	public AddressSelectionHandler addressSelectionHandler;
	public MenuHandler mHandler;

	public Controller() {
		copyHandler = new CopyHandler();
		sCHandler = new SendCoinHandler();
		addressSelectionHandler = new AddressSelectionHandler();
		mHandler = new MenuHandler();
	}

	// Add Listener handlers here (with implements on this object)
	// also make calls to ARWallet based on such events from here
	// actions will NOT return UI changes to be made,
	// that will be routed to the view

	/**
	 * TODO <--- Make sure to bind this to the window closing of the app frame
	 * also bind closeApp() to File -> Quit
	 * 
	 * @param winEvt
	 */
	@Override
	public void windowClosing(WindowEvent winEvt) {
		closeApp();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// mwallet.viewOpened();
	}

	// UI init

	public void viewReady() {
		mwallet.viewOpened();
	}

	/**
	 * Properly shutdown application
	 */
	private void closeApp() {
		System.out.println("*** Caught exit request, shutting down properly.");
		// Clean up in the background
		view.setVisible(false);
		mwallet.close();
		System.exit(0);
	}

	// initialization code

	public void addModel(ARWallet mwallet) {
		this.mwallet = mwallet;
	}

	public void addView(WalletView view) {
		this.view = view;
	}

	public void initModel(Object initParam) {
		return;
	}

	// event handler for clicking on the address and displaying in textfield
	public class AddressSelectionHandler implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			JTable table = view.receive.table;
			String address = (String) table.getValueAt(table.getSelectedRow(),
					1);
			view.receive.address.setText(address);

		}

	}

	// event handler for generating new key button
	public class CopyHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			JTextField addrField = view.receive.address;
			String address = addrField.getText();
			// do not copy empty string
			if (address.length() == 0)
				return;

			String copyText = "";
			if (e.getSource().equals(view.receive.copyButton)) {
				copyText = address;
			} else if (e.getSource().equals(view.receive.uriButton)) {
				String amount = view.receive.amount.getText();
				boolean useAmount = true;
				if (amount.length() == 0)
					useAmount = false;
				BitcoinAmount amt = null;
				try {
					amt = new BitcoinAmount(BitcoinAmount.B.COIN, amount);
				} catch (Exception exc) {
					useAmount = false;
				}
				BitcoinURI uri;
				if (useAmount)
					uri = new BitcoinURI(address, amt);
				else
					uri = new BitcoinURI(address);
				copyText = uri.toString();
			}

			addrField.requestFocus();
			addrField.selectAll();
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Clipboard clipboard = toolkit.getSystemClipboard();
			StringSelection strSel = new StringSelection(copyText);
			clipboard.setContents(strSel, null);
		}

	}

	// event handler for menu bar
	public class MenuHandler implements ActionListener {

		private JPasswordField newPass;
		private JPasswordField confirmPass;
		private JPasswordField oldPass;

		@Override
		public void actionPerformed(ActionEvent e) {
			Menu menu = view.menuBar;
			// to-do: different items got selected
			String windowTitle;
			String toDisplay;
			String message;

			// close the app
			if (e.getSource().equals(view.menuBar.menuFileQuit)) {
				closeApp();
			}

			// open the wallet under selected file
			else if (e.getSource().equals(view.menuBar.menuFileOpen)) {
				Object[] fileAndDir = openWalletFile();
				if (fileAndDir == null)
					return;
				mwallet.switchWallet((String) fileAndDir[0],
						(File) fileAndDir[1]);
			}

			// initiate a new wallet
			else if (e.getSource().equals(view.menuBar.menuFileNew)) {
				Object[] fileAndDir = createFile("wallet", "Bitcoin Wallets",
						"Create Wallet",
						"You will PERMANENTLY lose any bitcoins in the "
								+ "wallet if you don't a backup.");
				if (fileAndDir == null)
					return;
				mwallet.switchWallet((String) fileAndDir[0],
						(File) fileAndDir[1]);
			}

			// display the determistic seed
			else if (e.getSource().equals(view.menuBar.menuWalletSeed)) {
				if (!mwallet.deterministic.isInitialized()) {
					noSeed();
					return;
				}
				if (mwallet.isEncrypted()) {
					JOptionPane.showMessageDialog(view,
							"Wallet seed is encrypted.", "Error",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				windowTitle = "Seed";
				message = "Your seed is ";
				toDisplay = mwallet.deterministic.seedMnemonic();
				popUpDisplay(windowTitle, message, toDisplay);
			}

			// display the master public key
			else if (e.getSource().equals(view.menuBar.menuWalletMPK)) {
				if (!mwallet.deterministic.isInitialized()) {
					noSeed();
					return;
				}
				windowTitle = "Master Public Key";
				message = "Your master public key is ";
				toDisplay = mwallet.deterministic.viewMasterPubKey();
				popUpDisplay(windowTitle, message, toDisplay);
			}

			// save the transaction history to CSV file or override an existing
			// CSV file
			else if (e.getSource().equals(view.menuBar.menuWalletExportHistory)) {
				Object[] fileAndDir = createFile("csv", "CSV Files",
						"Save History",
						"Important financial records may be lost");
				if (fileAndDir == null)
					return;
				File dest = new File((File) fileAndDir[1],
						((String) fileAndDir[0]) + ".csv");
				new CSVExporter(view.history.table).store(dest);
			}

			// set up the password if first time/change the password if it's
			// already been made
			else if (e.getSource().equals(view.menuBar.menuWalletPassword)) {

				JPanel background = new JPanel();
				background.setLayout(new MigLayout("wrap 4"));
				background.setPreferredSize(new Dimension(600, 130));
				boolean isEncrypted = mwallet.isEncrypted();

				if (isEncrypted) {

					// draw the panel with password settings
					JLabel oldPassword = new JLabel("Old password");
					oldPass = new JPasswordField(30);

					JLabel msg = new JLabel(
							"<html><p>Your wallet is encrypted. To disable the password, enter an empty new password</p></html>");
					background.add(msg, "span 4");
					background.add(oldPassword);
					background.add(oldPass, "wrap");

				}

				else {
					JLabel msg = new JLabel(
							"<html><p>Your wallet is not encrypted. Enter the password here.</p></html>");
					background.add(msg, "span 4");

				}

				drawNewPassword(background);
				int result = JOptionPane.showConfirmDialog(view, background,
						"Setting Password", JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.PLAIN_MESSAGE);

				if (result != JOptionPane.OK_OPTION) {
					return;
				}

				String nPassword = new String(newPass.getPassword());
				String cPassword = new String(confirmPass.getPassword());

				if (!nPassword.equals(cPassword)) {
					JOptionPane
							.showMessageDialog(
									view,
									"Your confirmed password did not match the new password.",
									"Error", JOptionPane.WARNING_MESSAGE);
					return;
				}

				if (isEncrypted) {
					if (!mwallet.decrypt(new String(oldPass.getPassword()))) {
						JOptionPane.showMessageDialog(view,
								"Your password is incorrect.", "Error",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
				}

				if (nPassword.length() == 0) {
					if (!mwallet.isEncrypted()) {
						JOptionPane.showMessageDialog(view,
								"Your wallet now is not encrypted.");
					} else {
						throw new RuntimeException(
								"The wallet should be decrypted here");
					}
				}

				else {
					mwallet.encrypt(nPassword);
					JOptionPane
							.showMessageDialog(view,
									"Your wallet has been protected with a new password.");
				}

			}

			// display the description of this AquaRegia project
			else if (e.getSource().equals(view.menuBar.menuHelpAbout)) {

				JOptionPane.showMessageDialog(view,
						"<html><body><p style = 'width: 200px;'>"
								+ Strings.appname + "  " + Strings.appversion
								+ "<br><br>" + Strings.description
								+ "</body><html>", "About",
						JOptionPane.INFORMATION_MESSAGE);
			}

			// click to the website
			else if (e.getSource().equals(view.menuBar.menuHelpWebsite)) {

				String url = "http://www.aquaregia.org";
				Desktop desktop = Desktop.isDesktopSupported() ? Desktop
						.getDesktop() : null;
				if (desktop != null
						&& desktop.isSupported(Desktop.Action.BROWSE)) {

					try {

						Desktop.getDesktop().browse(new URL(url).toURI());
						return;

					} catch (IOException | URISyntaxException e1) {
						e1.printStackTrace();

					}

				}
				popUpDisplay("Website", "Our website is", url);

			}

		}

		/**
		 * This function would display information when menu item "seed" or
		 * "master public key" is clicked
		 * 
		 * @param message
		 *            - either display seed or mpk
		 * @param toDisplay
		 *            - the actual data to be displayed
		 */
		private void popUpDisplay(String title, String message, String toDisplay) {

			JPanel background = new JPanel();
			background.setLayout(new BorderLayout());
			background.setPreferredSize(new Dimension(300, 200));

			JLabel msg = new JLabel(message);
			msg.setPreferredSize(new Dimension(50, 50));

			JTextArea info = new JTextArea(toDisplay);
			info.setLineWrap(true);
			info.setWrapStyleWord(true);
			info.setEditable(false);
			info.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent e) {
					((JTextArea) e.getSource()).selectAll();
				}
			});

			background.add(msg, BorderLayout.PAGE_START);
			background.add(info, BorderLayout.CENTER);

			JOptionPane.showMessageDialog(view, background, title,
					JOptionPane.INFORMATION_MESSAGE);
		}

		public void drawNewPassword(JPanel background) {

			// start the option pane with a panel

			JLabel newPassword = new JLabel("New password");
			newPass = new JPasswordField(30);
			JLabel confirmPassword = new JLabel("Confirm new password");
			confirmPass = new JPasswordField(30);

			background.add(newPassword);
			background.add(newPass, "wrap");
			background.add(confirmPassword);
			background.add(confirmPass, "wrap");

		}

		public void noSeed() {
			JOptionPane.showMessageDialog(view, "Wallet has no seed.", "Error",
					JOptionPane.WARNING_MESSAGE);
		}

		/**
		 * This function would open a wallet from the user or exit if nothing to
		 * be opened
		 * 
		 * @return the name of the opened wallet and directory
		 */
		public Object[] openWalletFile() {
			String wallet = "";
			File parentDirectory = null;

			// create a file chooser
			final JFileChooser fileDialog = new JFileChooser();
			fileDialog.setCurrentDirectory(new File("."));
			fileDialog.setAcceptAllFileFilterUsed(false);

			// set file filter
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					"Bitcoin wallet", new String[] { "wallet" });
			fileDialog.setFileFilter(filter);

			// in response to a button click:
			int returnVal = fileDialog.showOpenDialog(view);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				java.io.File file = fileDialog.getSelectedFile();
				parentDirectory = file.getParentFile();
				wallet = file.getName();

				if (!wallet.endsWith(".wallet")) {
					System.out
							.println("somehow user didn't choose a wallet file");
				} else {
					int suffixPosition = wallet.indexOf(".wallet");
					wallet = wallet.substring(0, suffixPosition);
				}
			} else {
				return null;
			}

			return new Object[] { wallet, parentDirectory };
		}

		
		/**
		 * This is the helper function for creating transaction history CSV file
		 * or a new wallet
		 * @param extension
		 * @param filterName: either .csv or .wallet
		 * @param action
		 * @param overwriteWarning
		 * @return
		 */
		public Object[] createFile(String extension, String filterName,
				String action, String overwriteWarning) {
			String wallet = "";
			File parentDirectory = null;

			// create a file chooser
			final JFileChooser fileDialog = new JFileChooser();
			fileDialog.setCurrentDirectory(new File("."));
			fileDialog.setAcceptAllFileFilterUsed(false);

			// set file filter
			FileNameExtensionFilter filter = new FileNameExtensionFilter(
					filterName, new String[] { extension });
			fileDialog.setFileFilter(filter);

			// in response to a button click:
			int returnVal = fileDialog.showDialog(view, action);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				java.io.File file = fileDialog.getSelectedFile();

				parentDirectory = file.getParentFile();
				wallet = file.getName();

				if (wallet.endsWith("." + extension)) {
					int suffixPosition = wallet.indexOf("." + extension);
					wallet = wallet.substring(0, suffixPosition);
				}

				File resFile = new File(parentDirectory, wallet + "."
						+ extension);

				if (resFile.exists()) {
					int result = JOptionPane.showConfirmDialog(view,
							"Do you want to overwrite the file " + resFile
									+ "?\n" + overwriteWarning, "Overwrite?",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE);
					switch (result) {
					case JOptionPane.YES_OPTION:
						resFile.delete();
						break;
					case JOptionPane.NO_OPTION:
					default:
						return null;

					}
				}

			} else {
				return null;
			}

			return new Object[] { wallet, parentDirectory };
		}

	}

	// event handler for send coin button in the send tab
	public class SendCoinHandler implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// send the address and amount
			String inputPassword = null;
			boolean isEncrypted = mwallet.isEncrypted();
			try {
				BitcoinAmount sendAmount = new BitcoinAmount(
						BitcoinAmount.B.COIN, view.send.amount.getText());
				String address = view.send.address.getText();
				BitcoinAmount fee = new BitcoinAmount(
						Transaction.REFERENCE_DEFAULT_MIN_TX_FEE);
				BitcoinAmount total = new BitcoinAmount(sendAmount.add(fee));
				int okSend = JOptionPane.showConfirmDialog(
						view,
						"Are you sure you want to send " + sendAmount.coins()
								+ " BTC, which has a usual fee of "
								+ fee.coins() + " BTC, for a total of "
								+ total.coins() + " BTC?", "Send Bitcoins?",
						JOptionPane.YES_NO_OPTION);
				if (okSend != JOptionPane.YES_OPTION)
					return;
				if (isEncrypted) {

					// pop up the window to ask the user's password
					JPanel background = new JPanel();
					background.setLayout(new MigLayout("wrap 3"));
					background.setPreferredSize(new Dimension(500, 70));

					JLabel msg = new JLabel(
							"<html><p>Please enter your password before the transaction.</p></html>");
					background.add(msg, "span 4");

					JLabel yourPassword = new JLabel("Your password");
					JPasswordField yPass = new JPasswordField(30);
					background.add(yourPassword);
					background.add(yPass);

					int result = JOptionPane.showConfirmDialog(view,
							background, "Setting Password",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.PLAIN_MESSAGE);
					if (result != JOptionPane.OK_OPTION) {
						return;
					}

					inputPassword = new String(yPass.getPassword());

					// if the password is incorrect
					if (!mwallet.decrypt(inputPassword)) {
						JOptionPane.showMessageDialog(view,
								"Your password is incorrect.", "Error",
								JOptionPane.WARNING_MESSAGE);
						return;
					}

				}
				mwallet.simpleSendCoins(sendAmount, address);
			} catch (NumberFormatException e1) {
				// error message window for wrong amount format
				JOptionPane.showMessageDialog(view,
						"Please check your amount is a valid number of BTC.",
						"Invalid Amount", JOptionPane.ERROR_MESSAGE);
			} catch (AddressFormatException e1) {
				// error message window for wrong address format
				JOptionPane.showMessageDialog(view,
						"Please check your address is in the correct format.",
						"Invalid Address", JOptionPane.ERROR_MESSAGE);
			} catch (InsufficientMoneyException e1) {
				// error message window for inefficient money
				BitcoinAmount missingValue = new BitcoinAmount(e1.missing);
				JOptionPane.showMessageDialog(view, "You are missing "
						+ missingValue.coins() + " BTC "
						+ " necessary to complete the transaction.",
						"Insufficient amount", JOptionPane.ERROR_MESSAGE);
			} finally {
				if (isEncrypted && !mwallet.isEncrypted()
						&& inputPassword != null) {
					mwallet.encrypt(inputPassword);
				}
			}
		}
	}

}
