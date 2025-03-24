package de.hype.bingonet.client.common.client.updatelisteners;

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.communication.BBsentialConnection;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UpdateListener implements Runnable {
    public boolean showOverlay = false;
    AtomicBoolean isInLobby = new AtomicBoolean(false);

    public UpdateListener() {
    }

    public boolean showOverlay() {
        if (!allowOverlayOverall()) return false;
        return isInLobby.get() || showOverlay;
    }

    public abstract void run();

    public abstract boolean allowOverlayOverall();

    public BBsentialConnection getConnection(){
        return BingoNet.connection;
    }
}