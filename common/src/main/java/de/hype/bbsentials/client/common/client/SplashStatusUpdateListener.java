package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.packets.function.SplashNotifyPacket;
import de.hype.bbsentials.shared.packets.function.SplashUpdatePacket;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashStatusUpdateListener implements Runnable {
    public StatusConstants status = StatusConstants.WAITING;
    public boolean splashed = false;
    public boolean full = false;
    AtomicBoolean splashLobby = new AtomicBoolean(false);
    public static boolean showSplashOverlayOverrideDisplay = false;
    BBsentialConnection connection;
    SplashNotifyPacket packet;

    public SplashStatusUpdateListener(BBsentialConnection connection, SplashNotifyPacket packet) {
        this.connection = connection;
        this.packet = packet;
    }

    public boolean showSplashOverlay() {
        if (!BBsentials.config.useSplashLeecherOverlayHud) return false;
        return splashLobby.get() || showSplashOverlayOverrideDisplay;
    }

    @Override
    public void run() {
        BBsentials.onServerLeave.add(() -> splashLobby.set(false));
        int maxPlayerCount = EnvironmentCore.utils.getMaximumPlayerCount() - 5;
        splashLobby.set(true);
        while (splashLobby.get()) {
            if (!full && (EnvironmentCore.utils.getPlayerCount() >= maxPlayerCount)) {
                status = StatusConstants.FULL;
                full = true;
            }
            if (!status.equals(status)) {
                status = status;
                connection.sendPacket(new SplashUpdatePacket(packet.splash.splashId, status));
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }
        }
        if (splashed) {
            status = StatusConstants.DONEBAD;
        }
        else {
            status = StatusConstants.DONEBAD;
        }
        if (!status.equals(status)) {
            status = status;
            connection.sendPacket(new SplashUpdatePacket(packet.splash.splashId, status));
        }
    }

    public void setStatus(StatusConstants newStatus) {
        this.status = newStatus;
        if (newStatus.equals(StatusConstants.SPLASHING)) {
            splashed = true;
            BBsentials.executionService.schedule(() -> {
                setStatus(StatusConstants.DONEBAD);
                splashLobby.set(false);
            }, 1, TimeUnit.MINUTES);
        }
    }
}
