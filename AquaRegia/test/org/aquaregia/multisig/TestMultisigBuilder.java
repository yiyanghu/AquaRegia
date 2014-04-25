package org.aquaregia.multisig;

import static org.junit.Assert.*;

import org.aquaregia.wallet.deterministic.Deterministic;
import org.junit.Test;

import com.google.bitcoin.core.Address;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.Utils;
import com.google.bitcoin.params.MainNetParams;

public class TestMultisigBuilder {

	@Test
	public void testAddressEquivalency() {
		NetworkParameters params = MainNetParams.get();
		ECKey clientPriv = new ECKey();
		ECKey serverPriv = new ECKey();
		
		ECKey clientPub = new ECKey(null, clientPriv.getPubKey());
		ECKey serverPub = new ECKey(null, serverPriv.getPubKey());
		
		MultisigBuilder clientMs = new MultisigBuilder(2,2);
		clientMs.addOwnedKey(clientPriv);
		clientMs.addOtherKey(serverPub);
		clientMs.complete();
		
		MultisigBuilder serverMs = new MultisigBuilder(2,2);
		serverMs.addOwnedKey(serverPriv);
		serverMs.addOtherKey(clientPub);
		serverMs.complete();
		
		//System.out.println(Utils.bytesToHexString(clientMs.getRedeemScript()));
		
		Address ac = clientMs.getAddress(params);
		Address as = serverMs.getAddress(params);
		
		assertEquals(ac, as);
	}
	
	@Test
	public void correctAddressAndRedeemScript() {
		// Values obtained from Electrum test
		String pk1 = "045ce62c5a778c083f623e720e99e00e56235578d22c4b7cebec5c42d4cf86d1b8aa5af0f1f5846e899c98605cbad41a4161d200ce929db5e867977912c2415340";
		String pk2 = "04883519de6cc5382063f5d563c737f76c192046e8e5c8ffec758df0a52e9de4b7ce37394d03a8ab7f1e66809c52ef3d1abe910109471d4584a4353e41c73ae478";
		String pk3 = "04d6d8237913748f4051cd05d435142e5a85d46253b7a432c1560a91b1db3873a6a1bdf9b5fad443aa35654634d0a3cf826c6819514645b6fb60904a63b1415de9";
		
		ECKey key1 = new ECKey(null, Deterministic.hexStringToByteArray(pk1));
		ECKey key2 = new ECKey(null, Deterministic.hexStringToByteArray(pk2));
		ECKey key3 = new ECKey(null, Deterministic.hexStringToByteArray(pk3));
		
		MultisigBuilder testMs = new MultisigBuilder(2,3);
		testMs.addOtherKey(key1);
		testMs.addOtherKey(key2);
		testMs.addOtherKey(key3);
		testMs.complete();
		
		String redeemscript = Utils.bytesToHexString(testMs.getRedeemScript());
		String expScript = "5241045ce62c5a778c083f623e720e99e00e56235578d22c4b7cebec5c42d4cf86d1b8aa5af0f1f5846e899c98605cbad41a4161d200ce929db5e867977912c24153404104883519de6cc5382063f5d563c737f76c192046e8e5c8ffec758df0a52e9de4b7ce37394d03a8ab7f1e66809c52ef3d1abe910109471d4584a4353e41c73ae4784104d6d8237913748f4051cd05d435142e5a85d46253b7a432c1560a91b1db3873a6a1bdf9b5fad443aa35654634d0a3cf826c6819514645b6fb60904a63b1415de953ae";
		assertEquals(expScript, redeemscript);
		
		NetworkParameters params = MainNetParams.get();
		Address addr = testMs.getAddress(params);
		assertEquals("397D9A7stah3Bd8cjNzWJnhVAJdS9mwDZY", addr.toString());
	}

}
