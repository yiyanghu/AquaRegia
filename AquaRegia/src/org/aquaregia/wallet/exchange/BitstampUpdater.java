package org.aquaregia.wallet.exchange;

import java.math.BigDecimal;
import java.util.Map;

import com.json.parsers.*;

public class BitstampUpdater extends ExchangeRateUpdater {

	public final static String URL = "https://www.bitstamp.net/api/ticker/";
	public final static String KEY = "last";
	
	public BitstampUpdater() {
		symbol = "$";
		source = "Bitstamp";
	}
	
	@Override
	public BigDecimal update() {
		String siteRes = getURL(URL);
		if (siteRes == null)
			return null;
		JSONParser jp = new JSONParser();
		@SuppressWarnings("unchecked")
		Map<Object, Object> json = jp.parseJson(siteRes);
		try {
			return new BigDecimal((String) json.get(KEY));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
