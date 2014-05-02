package org.aquaregia.io;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.aquaregia.wallet.BitcoinAmount;

import com.google.bitcoin.core.Address;

public class BitcoinURI {

	Map<String,String> qsv = new HashMap<String,String>();
	String address;
	
	public BitcoinURI(String address) {
		this.address = address;
	}
	
	public BitcoinURI(String address, BitcoinAmount amount) {
		this(address);
		qsv.put("amount", amount.toString(BitcoinAmount.B.COIN));
	}
	
	@Override
	public String toString() {
		String res = "bitcoin:" + address;
		if (!qsv.isEmpty())
			res += "?" + new QueryString(qsv).toString();
		return res;
	}
	
	/**
	 * http://stackoverflow.com/questions/1861620/is-there-a-java-package-to-handle-building-urls
	 */
	public class QueryString {

		private String query = "";

		public QueryString(Map<String, String> map) {
			Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> pairs = it.next();
				try {
					query += URLEncoder.encode(pairs.getKey(), "UTF-8") + "="
							+ URLEncoder.encode(pairs.getValue(), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (it.hasNext()) {
					query += "&";
				}
			}
		}

		public String toString() {
			return query;
		}
	}
}
