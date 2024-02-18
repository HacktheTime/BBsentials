package de.hype.bbsentials.client.common.client.updatelisteners;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.shared.constants.ChChestItem;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.ChChestData;
import de.hype.bbsentials.shared.objects.ChestLobbyData;
import de.hype.bbsentials.shared.objects.Position;
import de.hype.bbsentials.shared.objects.RenderInformation;
import de.hype.bbsentials.shared.packets.mining.ChestLobbyUpdatePacket;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ChChestUpdateListener extends UpdateListener {
    public ChestLobbyData lobby;
    public boolean isHoster = false;
    List<Position> chestsOpened = new ArrayList<>();
    Map<Position, Waypoints> waypoints = new HashMap<>();

    public ChChestUpdateListener(ChestLobbyData lobby) {
        if (lobby == null) return;
        this.lobby = lobby;
        isHoster = (lobby.contactMan.equalsIgnoreCase(BBsentials.generalConfig.getUsername()));
    }

    public void updateLobby(ChestLobbyData data) {
        lobby = data;
        setWaypoints();
    }

    public void setWaypoints() {
        if (!BBsentials.chChestConfig.addWaypointForChests || lobby == null) return;
        for (ChChestData chest : lobby.chests) {
            Waypoints waypoint = waypoints.get(chest.coords);
            boolean shouldDisplay = !chestsOpened.contains(chest.coords);
            if (waypoint != null) {
                waypoint.visible = shouldDisplay;
                continue;
            }
            List<String> chestItems = Arrays.stream(chest.items).filter(item -> !item.isCustom()).map(ChChestItem::getDisplayName).collect(Collectors.toList());
            List<RenderInformation> renderInformationList = new ArrayList<>();
            chestItems.forEach((item) -> renderInformationList.add(new RenderInformation("bbsentials", "textures/gui/sprites/waypoints/" + item + ".png")));
            if (Waypoints.waypoints.values().stream().noneMatch(waypointFiltered -> waypointFiltered.position.equals(chest.coords))) {
                Waypoints newpoint = new Waypoints(chest.coords, "{\"text\":\"" + String.join(", ", chestItems.subList(0, Math.min(chestItems.size(), 3))) + "\"}", 1000, shouldDisplay, true, renderInformationList, BBsentials.chChestConfig.defaultWaypointColor, BBsentials.chChestConfig.doChestWaypointsTracers);
                waypoints.put(newpoint.position, newpoint);
            }
        }
    }

    @Override
    public void run() {
        ServerSwitchTask.onServerLeaveTask(() -> isInLobby.set(false));
        int maxPlayerCount = EnvironmentCore.utils.getMaximumPlayerCount();
        isInLobby.set(true);
        setWaypoints();
        //(15mc days * 20 min day * 60 to seconds * 20 to ticks) -> 408000 | 1s 1000ms 1000/20 for ms for 1 tick.
        try {
            lobby.setLobbyMetaData(null, ((408000 - EnvironmentCore.utils.getLobbyTime()) * 50));
        } catch (Exception ignored) {
            //never thrown lol
        }
        while (isInLobby.get()) {
            if ((EnvironmentCore.utils.getPlayerCount() >= maxPlayerCount)) {
                setStatus(StatusConstants.FULL);
            }
            else if ((EnvironmentCore.utils.getPlayerCount() < maxPlayerCount - 3) && lobby.getStatus().equals(StatusConstants.FULL.getDisplayName())) {
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
        return BBsentials.hudConfig.useChChestHudOverlay;
    }

    public void setStatus(StatusConstants newStatus) {
        setStatusNoUpdate(newStatus);
        sendUpdatePacket();
    }

    public void setStatusNoUpdate(StatusConstants newStatus) {
        if (lobby.getStatus().equals(newStatus.getDisplayName())) return;
        try {
            lobby.setStatus(newStatus);
        } catch (SQLException e) {
            //never thrown lol
        }
    }

    public void sendUpdatePacket() {
        updateMetaData();
        getConnection().sendPacket(new ChestLobbyUpdatePacket(lobby));
    }

    public List<ChChestData> getUnopenedChests() {
        List<ChChestData> unopened = new ArrayList<>();
        if (lobby == null) return new ArrayList<>();
        for (ChChestData chest : lobby.chests) {
            if (!chestsOpened.contains(chest.coords)) unopened.add(chest);
        }
        return unopened;
    }

    public void addOpenedChest(Position pos) {
        BBsentials.executionService.execute(() -> {
            chestsOpened.add(pos);
            setWaypoints();
        });
    }

    public void updateMetaData() {
        try {
            long timeTillCloseMili = ((360_000 - EnvironmentCore.utils.getLobbyTime()) * 50);
            long timeMiliClosingDate = new Date().getTime() + timeTillCloseMili;
            if (timeTillCloseMili <= 0) setStatusNoUpdate(StatusConstants.CLOSED);
            List<String> players = EnvironmentCore.utils.getPlayers();
            players = players.stream().map(username -> username.replaceAll("\\[\\S+]", "").trim()).filter(userName -> !userName.equals(lobby.contactMan)).collect(Collectors.toList());
            lobby.setLobbyMetaData(players, timeMiliClosingDate);
        } catch (SQLException e) {
            Chat.sendPrivateMessageToSelfError("Uhm how did this happen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
