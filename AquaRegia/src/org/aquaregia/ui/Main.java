package org.aquaregia.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executor;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.aquaregia.wallet.ARWallet;

import com.google.bitcoin.utils.BriefLogFormatter;
import com.google.bitcoin.utils.Threading;

/**
 * Launches AquaRegia
 * 
 * @author Stephen Halm and Yiyang Hu
 */
public class Main {
	final public static Object INIT_PARAM = null;

	/** Network related events need to be routed to UI thread */
	public static Executor runInUIThread = new Executor() {
		@Override
		public void execute(Runnable runnable) {
			SwingUtilities.invokeLater(runnable); // For Swing.
		}
	};

	private ARWallet mwallet = new ARWallet();
	private WalletView view = new WalletView();

	public Main() {
		Threading.USER_THREAD = runInUIThread;
		mwallet.addObserver(view);

		Controller controller = new Controller();
		controller.addModel(mwallet);
		controller.addView(view);
		controller.initModel(INIT_PARAM);

		view.addController(controller);
	}

	/**
	 * This function would open a wallet from the user
	 * or exit if nothing to be opened
	 * @return the name of the opened wallet
	 */
	public String openWalletFile() {
		String wallet = "";
		File parentDirectory;
		

		// create a file chooser
		final JFileChooser fileDialog = new JFileChooser();
		fileDialog.setCurrentDirectory(new File("."));
		
		// set file filter
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Bitcoin wallet",new String[] {"wallet"});
		fileDialog.setFileFilter(filter);
		
		// in response to a button click:
		int returnVal = fileDialog.showOpenDialog(null);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			java.io.File file = fileDialog.getSelectedFile();
			parentDirectory = file.getParentFile();
			wallet = file.getName();
			
			if (! wallet.endsWith(".wallet")){
				System.exit(0);
			}
			else{
				int suffixPosition = wallet.indexOf(".wallet");
				wallet = wallet.substring(0, suffixPosition);
			}

		} else {
			System.exit(0);
		}

		return wallet;
	}

	public static void main(String[] args) {
		BriefLogFormatter.init();
		new Main();
	}

}
