package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.ChestLobbyData;
import de.hype.bbsentials.shared.packets.function.SplashUpdatePacket;
import de.hype.bbsentials.shared.packets.mining.ChestLobbyUpdatePacket;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChChestUpdateListener implements Runnable {
    AtomicBoolean chestLobby = new AtomicBoolean(false);
    Map<Integer, Boolean> chestsOpened = new HashMap<>();
    public static boolean showChChestOverlay = false;
    BBsentialConnection connection;
    ChestLobbyData lobby;

    public ChChestUpdateListener(BBsentialConnection connection, ChestLobbyData lobby) {
        this.connection = connection;
        this.lobby = lobby;
    }

    public boolean showOverlay() {
        if (!BBsentials.config.useSplashLeecherOverlayHud) return false;
        return chestLobby.get() || showChChestOverlay;
    }

    @Override
    public void run() {
        BBsentials.onServerLeave.add(() -> chestLobby.set(false));
        int maxPlayerCount = EnvironmentCore.utils.getMaximumPlayerCount();
        chestLobby.set(true);
        //(15mc days * 20 min day * 60 to seconds * 20 to ticks) -> 360000 | 1s 1000ms 1000/20 for ms for 1 tick.
        try {
            lobby.setLobbyMetaData(null, new Date(System.currentTimeMillis() + (360000 - EnvironmentCore.utils.getLobbyTime()) / 50));
        } catch (SQLException ignored) {
            //never thrown lol
        }
        while (chestLobby.get()) {
            if ((EnvironmentCore.utils.getPlayerCount() >= maxPlayerCount)) {
                setStatus(StatusConstants.FULL);
            }
            else if ((EnvironmentCore.utils.getPlayerCount() < maxPlayerCount)) {
                setStatus(StatusConstants.OPEN);
            }
            try {
                // 3s
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void setStatus(StatusConstants newStatus) {
        try {
            lobby.setStatus(newStatus);
        } catch (SQLException e) {
            //never thrown lol
        }
        connection.sendPacket(new ChestLobbyUpdatePacket(lobby.lobbyId, lobby, null));
    }
}
