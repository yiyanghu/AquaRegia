package org.aquaregia.wallet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.bitcoin.core.BlockChain;
import com.google.bitcoin.core.CheckpointManager;
import com.google.bitcoin.core.DownloadListener;
import com.google.bitcoin.core.ECKey;
import com.google.bitcoin.core.NetworkParameters;
import com.google.bitcoin.core.PeerAddress;
import com.google.bitcoin.core.PeerEventListener;
import com.google.bitcoin.core.Wallet;
import com.google.bitcoin.kits.WalletAppKit;
import com.google.bitcoin.net.discovery.DnsDiscovery;
import com.google.bitcoin.store.BlockStoreException;
import com.google.bitcoin.store.SPVBlockStore;
import com.google.bitcoin.store.WalletProtobufSerializer;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.Service.State;

/**
 * Potentially modified WalletAppKit
 * @author Stephen Halm
 */
public class WalletInitializer extends WalletAppKit {
    public WalletInitializer(NetworkParameters params, File directory, String filePrefix) {
    	super(params, directory, filePrefix);
    }
    
    @Override
    protected void startUp() throws Exception {
        // Runs in a separate thread.
        if (!directory.exists()) {
            if (!directory.mkdir()) {
                throw new IOException("Could not create named directory.");
            }
        }
        FileInputStream walletStream = null;
        try {
            File chainFile = new File(directory, filePrefix + ".spvchain");
            boolean chainFileExists = chainFile.exists();
            vWalletFile = new File(directory, filePrefix + ".wallet");
            boolean shouldReplayWallet = vWalletFile.exists() && !chainFileExists;

            vStore = new SPVBlockStore(params, chainFile);
            if (!chainFileExists && checkpoints != null) {
                // Ugly hack! We have to create the wallet once here to learn the earliest key time, and then throw it
                // away. The reason is that wallet extensions might need access to peergroups/chains/etc so we have to
                // create the wallet later, but we need to know the time early here before we create the BlockChain
                // object.
                long time = Long.MAX_VALUE;
                if (vWalletFile.exists()) {
                    Wallet wallet = new Wallet(params);
                    FileInputStream stream = new FileInputStream(vWalletFile);
                    new WalletProtobufSerializer().readWallet(WalletProtobufSerializer.parseToProto(stream), wallet);
                    time = wallet.getEarliestKeyCreationTime();
                }
                CheckpointManager.checkpoint(params, checkpoints, vStore, time);
            }
            vChain = new BlockChain(params, vStore);
            vPeerGroup = createPeerGroup();
            if (this.userAgent != null)
                vPeerGroup.setUserAgent(userAgent, version);
            if (vWalletFile.exists()) {
                walletStream = new FileInputStream(vWalletFile);
                vWallet = new Wallet(params);
                addWalletExtensions(); // All extensions must be present before we deserialize
                new WalletProtobufSerializer().readWallet(WalletProtobufSerializer.parseToProto(walletStream), vWallet);
                if (shouldReplayWallet)
                    vWallet.clearTransactions(0);
            } else {
                vWallet = new Wallet(params);
                vWallet.addKey(new ECKey());
                addWalletExtensions();
            }
            if (useAutoSave) vWallet.autosaveToFile(vWalletFile, 1, TimeUnit.SECONDS, null);
            // Set up peer addresses or discovery first, so if wallet extensions try to broadcast a transaction
            // before we're actually connected the broadcast waits for an appropriate number of connections.
            if (peerAddresses != null) {
                for (PeerAddress addr : peerAddresses) vPeerGroup.addAddress(addr);
                peerAddresses = null;
            } else {
                vPeerGroup.addPeerDiscovery(new DnsDiscovery(params));
            }
            vChain.addWallet(vWallet);
            vPeerGroup.addWallet(vWallet);
            onSetupCompleted();

            if (blockingStartup) {
                vPeerGroup.startAsync();
                vPeerGroup.awaitRunning();
                // Make sure we shut down cleanly. 
                installShutdownHook();

                // TODO: Be able to use the provided download listener when doing a blocking startup.
                final DownloadListener listener = new DownloadListener();
                vPeerGroup.startBlockChainDownload(listener);
                listener.await();
            } else {
                vPeerGroup.startAsync();
                vPeerGroup.addListener(new Service.Listener() {
                    @Override
                    public void running() {
                        final PeerEventListener l = downloadListener == null ? new DownloadListener() : downloadListener;
                        vPeerGroup.startBlockChainDownload(l);
                    }

                    @Override
                    public void failed(State from, Throwable failure) {
                        throw new RuntimeException(failure);
                    }
                }, MoreExecutors.sameThreadExecutor());
            }
        } catch (BlockStoreException e) {
            throw new IOException(e);
        } finally {
            if (walletStream != null) walletStream.close();
        }
    }
    
    private void installShutdownHook() {
        if (autoStop) Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override public void run() {
                try {
                    WalletInitializer.this.stopAsync();
                    WalletInitializer.this.awaitTerminated();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
