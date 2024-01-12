package de.hype.bbsentials.client.common.client.updatelisteners;

import de.hype.bbsentials.client.common.communication.BBsentialConnection;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class UpdateListener implements Runnable {
    public boolean showOverlay = false;
    AtomicBoolean isInLobby = new AtomicBoolean(false);
    BBsentialConnection connection;

    public UpdateListener(BBsentialConnection connection) {
        this.connection = connection;
    }

    public boolean showOverlay() {
        if (!allowOverlayOverall()) return false;
        return isInLobby.get() || showOverlay;
    }

    public abstract void run();

    public abstract boolean allowOverlayOverall();
}