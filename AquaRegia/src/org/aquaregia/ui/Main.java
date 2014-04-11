package org.aquaregia.ui;

import java.util.concurrent.Executor;

import javax.swing.SwingUtilities;

import org.aquaregia.wallet.ARWallet;

import com.google.bitcoin.utils.BriefLogFormatter;
import com.google.bitcoin.utils.Threading;

/**
 * Launches AquaRegia
 * @author Stephen Halm
 */
public class Main {
	final public static Object INIT_PARAM = null;
	
	/** Network related events need to be routed to UI thread */
	public static Executor runInUIThread = new Executor() {
	    @Override public void execute(Runnable runnable) {
	        SwingUtilities.invokeLater(runnable);   // For Swing.  
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
	
	public static void main(String[] args) {
		BriefLogFormatter.init();
		new Main();
	}
}
