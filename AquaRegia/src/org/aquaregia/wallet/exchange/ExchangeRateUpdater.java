package org.aquaregia.wallet.exchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.Scanner;

import javax.annotation.Nullable;

/**
 * Specification to extend for classes that can get exchange rates.
 * 
 * @author Stephen Halm
 */
public abstract class ExchangeRateUpdater {
	protected String symbol;
	protected String source;
	
	@Nullable
	public abstract BigDecimal update();
	
	public String getSymbol() {
		return symbol;
	}
	public String getSourceName() {
		return source;
	}
	
	@Nullable
	protected String getURL(String url) {
		Scanner s = null;
		String result = "";
		try {
			s = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A");
			result = s.next();
		} catch (IOException e) {
			return null;
		} finally {
			if (s != null)
				s.close();
		}
		return result;
	}
	
}
