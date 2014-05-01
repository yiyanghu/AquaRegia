package org.aquaregia.wallet.exchange;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

/**
 * A periodic exchange rate refresher for any input ExchangeRateUpdater.
 * @author Stephen Halm
 */
public class ExchangeRateUpdateTask {

	final Runnable taskRunner;
	final ExchangeRateUpdater updater;
	final ExchangeRateHandler exhandler;
	private ScheduledFuture<?> periodicCheck;
	/** Seconds between site requests */
	final static private int SECONDS_INT = 60;
	
	public ExchangeRateUpdateTask(ExchangeRateUpdater eru, ExchangeRateHandler handler) {
		updater = eru;
		exhandler = handler;
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		taskRunner = new Runnable() {
			@Override
			public void run() {
				final BigDecimal rate = updater.update();
				final String symbol = updater.getSymbol();
				final String source = updater.getSourceName();
				
				if (rate == null) return;
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						exhandler.update(rate, symbol, source);
					}

				});
			}
		};
		
		periodicCheck = scheduler.scheduleWithFixedDelay(taskRunner, 0, SECONDS_INT, TimeUnit.SECONDS);
		
	}
	
	public interface ExchangeRateHandler {
		public void update(BigDecimal exchangeRate, String symbol, String source);
	}
	
	public void end() {
		periodicCheck.cancel(true);
	}
}
