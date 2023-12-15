package de.hype.bbsentials.common.client;

import de.hype.bbsentials.common.communication.BBsentialConnection;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.common.packets.packets.SplashNotifyPacket;
import de.hype.bbsentials.common.packets.packets.SplashUpdatePacket;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class SplashStatusUpdateListener implements Runnable {
    public String newStatus = SplashUpdatePacket.STATUS_WAITING;
    public boolean splashed = false;
    public boolean full = false;
    AtomicBoolean splashLobby = new AtomicBoolean(false);
    public static boolean showSplashOverlayOverrideDisplay = false;
    BBsentialConnection connection;
    SplashNotifyPacket packet;
    private String status = SplashUpdatePacket.STATUS_WAITING;

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
                newStatus = SplashUpdatePacket.STATUS_FULL;
                full = true;
            }
            if (!status.equals(newStatus)) {
                status = newStatus;
                connection.sendPacket(new SplashUpdatePacket(packet.splashId, status));
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }
        }
        if (splashed) {
            newStatus = SplashUpdatePacket.STATUS_DONE;
        }
        else {
            newStatus = SplashUpdatePacket.STATUS_CANCELED;
        }
        if (!status.equals(newStatus)) {
            status = newStatus;
            connection.sendPacket(new SplashUpdatePacket(packet.splashId, status));
        }
    }

    public void setStatus(String newStatus) {
        this.newStatus = newStatus;
        if (newStatus.equals(SplashUpdatePacket.STATUS_SPLASHING)) {
            splashed = true;
            BBsentials.executionService.schedule(() -> {
                setStatus(SplashUpdatePacket.STATUS_DONE);
                splashLobby.set(false);
            }, 1, TimeUnit.MINUTES);
        }
    }
}
