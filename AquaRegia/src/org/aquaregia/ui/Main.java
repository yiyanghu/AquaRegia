package org.aquaregia.ui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

	private ARWallet mwallet;
	private WalletView view;

	public Main() {
		Threading.USER_THREAD = runInUIThread;
		mwallet = new ARWallet();
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				@Override
				public void run() {
					view = new WalletView();
					
					mwallet.addObserver(view);

					Controller controller = new Controller();
					controller.addModel(mwallet);
					controller.addView(view);
					controller.initModel(INIT_PARAM);

					view.addController(controller);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
	}

	

	public static void main(String[] args) {
		BriefLogFormatter.init();
		new Main();
	}

}
