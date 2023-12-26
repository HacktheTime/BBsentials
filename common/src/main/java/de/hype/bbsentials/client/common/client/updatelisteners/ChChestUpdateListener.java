package de.hype.bbsentials.client.common.client.updatelisteners;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.ChChestData;
import de.hype.bbsentials.shared.objects.ChestLobbyData;
import de.hype.bbsentials.shared.objects.Position;
import de.hype.bbsentials.shared.packets.mining.ChestLobbyUpdatePacket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChChestUpdateListener extends UpdateListener {
    List<Position> chestsOpened = new ArrayList<>();
    public ChestLobbyData lobby;

    public boolean isHoster = false;

    public ChChestUpdateListener(BBsentialConnection connection, ChestLobbyData lobby) {
        super(connection);
        if (lobby == null) return;
        this.lobby = lobby;
        isHoster = (lobby.contactMan.equalsIgnoreCase(BBsentials.config.getUsername()));
    }

    public void updateLobby(ChestLobbyData data) {
        lobby = data;
    }
    @Override
    public void run() {
        BBsentials.onServerLeave.add(new ServerSwitchTask(() -> isInLobby.set(false)));
        int maxPlayerCount = EnvironmentCore.utils.getMaximumPlayerCount();
        isInLobby.set(true);
        //(15mc days * 20 min day * 60 to seconds * 20 to ticks) -> 360000 | 1s 1000ms 1000/20 for ms for 1 tick.
        try {
            lobby.setLobbyMetaData(null, new Date(System.currentTimeMillis() + (360000 - EnvironmentCore.utils.getLobbyTime()) / 50));
        } catch (SQLException ignored) {
            //never thrown lol
        }
        while (isInLobby.get()) {
            if ((EnvironmentCore.utils.getPlayerCount() >= maxPlayerCount)) {
                setStatus(StatusConstants.FULL);
            }
            else if ((EnvironmentCore.utils.getPlayerCount() < maxPlayerCount - 3)) {
                setStatus(StatusConstants.OPEN);
            }
            try {
                // 3s
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public boolean allowOverlayOverall() {
        return BBsentials.config.useChChestHudOverlay;
    }

    public void setStatus(StatusConstants newStatus) {
        try {
            lobby.setStatus(newStatus);
        } catch (SQLException e) {
            //never thrown lol
        }
        connection.sendPacket(new ChestLobbyUpdatePacket(lobby));
    }

    public List<ChChestData> getUnopenedChests() {
        List<ChChestData> unopened = new ArrayList<>();

        for (ChChestData chest : lobby.chests) {
            if (!chestsOpened.contains(chest.coords)) unopened.add(chest);
        }
        return unopened;
    }

    public void addOpenedChest(Position pos) {
        chestsOpened.add(pos);
    }
}
