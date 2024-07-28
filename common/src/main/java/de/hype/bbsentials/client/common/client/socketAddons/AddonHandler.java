package de.hype.bbsentials.client.common.client.socketAddons;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.ChatPrompt;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bbsentials.environment.addonpacketconfig.AddonPacketUtils;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.constants.TravelEnums;
import de.hype.bbsentials.shared.objects.ClientWaypointData;
import de.hype.bbsentials.shared.packets.addonpacket.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AddonHandler implements Runnable {
    public Socket client;
    private BufferedReader reader;
    private PrintWriter writer;

    public AddonHandler(Socket client) {
        this.client = client;
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = new PrintWriter(client.getOutputStream(), true);
        } catch (Exception ignored) {
            try {
                close();
            } catch (Exception ignoredtoo) {

            }
        }
    }

    public void sendMessage(String message) {
        writer.write(message);
    }

    public void onReceive(String message) {
        AddonPacketUtils.handleIfPacket(this, message);
    }

    @Override
    public void run() {
        while (client.isConnected()) {
            try {
                onReceive(reader.readLine());
            } catch (Exception ignored) {

            }
        }
        BBsentials.addonManager.clients.remove(this);
    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {

        }
        reader = null;
        writer = null;
    }

    public void onClientCommandAddonPacket(ClientCommandAddonPacket packet) {
        if (!BBsentials.socketAddonConfig.allowClientCommands) return;
        EnvironmentCore.utils.executeClientCommand(packet.command);
    }

    public void onPlaySoundAddonPacket(PlaySoundAddonPacket packet) {
        EnvironmentCore.utils.playsound(packet.path, packet.namespace);
    }

    public void onPublicChatAddonPacket(PublicChatAddonPacket packet) {
        if (!BBsentials.socketAddonConfig.allowAutomatedSending) return;
        BBsentials.sender.addSendTask(packet.message.replace("§.", "").replace("\n", "").substring(0, Math.min(255, packet.message.length())), packet.timing);
    }

    public void onServerCommandAddonPacket(ServerCommandAddonPacket packet) {
        if (!BBsentials.socketAddonConfig.allowAutomatedSending) return;
        BBsentials.sender.addSendTask("/" + packet.command.replace("§.", "").replace("\n", "").substring(0, Math.min(254, packet.command.length())), packet.timing);
    }

    public void onDisplayClientsideMessageAddonPacket(DisplayClientsideMessageAddonPacket packet) {
        Chat.sendPrivateMessageToSelfBase(packet.message, packet.formatting);
    }

    public void onDisplayTellrawMessageAddonPacket(DisplayTellrawMessageAddonPacket packet) {
        if (!BBsentials.socketAddonConfig.allowTellraw) return;
        Chat.sendPrivateMessageToSelfText(Message.tellraw(packet.rawJson));
    }

    public void onChatPromptAddonPacket(ChatPromptAddonPacket packet) {
        if (!BBsentials.socketAddonConfig.allowChatPrompt) return;
        BBsentials.temporaryConfig.lastChatPromptAnswer = new ChatPrompt(packet.commandToExecute, packet.timeTillReset);
    }

    public void onWaypointAddonPacket(WaypointAddonPacket packet) {
        if (packet.operation.equals(WaypointAddonPacket.Operation.ADD)) {
            new Waypoints(packet.waypoint);
        }
        else if (packet.operation.equals(WaypointAddonPacket.Operation.REMOVE)) {
            try {
                Waypoints.waypoints.get(packet.waypointId).removeFromPool();
            } catch (Exception ignored) {

            }
        }
        else if (packet.operation.equals(WaypointAddonPacket.Operation.EDIT)) {
            try {
                Waypoints oldWaypoint = Waypoints.waypoints.get(packet.waypointId);
                oldWaypoint.replaceWithNewWaypoint(packet.waypoint, packet.waypointId);
            } catch (Exception ignored) {

            }
        }
    }

    public void onGetWaypointsAddonPacket(GetWaypointsAddonPacket packet) {
        sendPacket(new GetWaypointsAddonPacket(Waypoints.waypoints.values().stream().map((waypoint -> ((ClientWaypointData) waypoint))).collect(Collectors.toList())));
    }

    public <E extends AbstractAddonPacket> void sendPacket(E packet) {
        String packetName = packet.getClass().getSimpleName();
        String rawjson = AddonPacketUtils.parsePacketToJson(packet);
        if (client.isConnected() && writer != null) {
            if (BBsentials.socketAddonConfig.addonDebug && !(packet.getClass().equals(ReceivedPublicChatMessageAddonPacket.class) && !BBsentials.socketAddonConfig.addonChatDebug)) {
                Chat.sendPrivateMessageToSelfDebug("BBDev-AsP: " + packetName + ": " + rawjson);
            }
            writer.println(packetName + "." + rawjson);
        }
        else {
            Chat.sendPrivateMessageToSelfError("BB: Couldn't send a " + packetName + "! did you get disconnected?");
        }
    }

    public void onStatusUpdateAddonPacket(StatusUpdateAddonPacket packet) {
    }

    public void onPlayTimeUpdated(PlayTimeUpdatedPacket packet) {
        Islands.putPlaytimeUpdate(packet.serverID, packet.updateTime);
        if (BBsentials.futureServerLeave != null) BBsentials.futureServerLeave.cancel(false);
        long waitTime = Duration.between(Instant.now(), packet.updateTime.plusSeconds(60)).getSeconds() - 8;
        if (BBsentials.dataStorage.serverId.equals(packet.serverID)) {
            Chat.sendPrivateMessageToSelfInfo("Scheduled Leave in %s".formatted(waitTime));
            BBsentials.futureServerLeave = BBsentials.executionService.schedule(BBsentials::doLeaveTask, waitTime, TimeUnit.SECONDS);
        }
        if (!Objects.equals(BBsentials.goToGoal.getIsland(), packet.islandType)) return;
        BBsentials.onDoJoinTask();
    }

    public void onSetGoToIsland(SetGoToIsland packet) {
        if (BBsentials.generalConfig.isAlt()) {
            if (!BBsentials.dataStorage.isInSkyblock()) {
                BBsentials.sender.addImmediateSendTask("/skyblock");
                try {
                    Thread.sleep(3_000);
                } catch (InterruptedException ignored) {
                }
            }
            BBsentials.goToGoal = packet.warp;
            if (packet.warp == TravelEnums.dungeon) {
                return;
            }
            else if (!packet.warp.getIsland().canBeWarpedIn()) {
                if (BBsentials.partyConfig.isPartyLeader)
                    BBsentials.sender.addSendTask("/p transfer " + BBsentials.generalConfig.getMainName());
            }
            else if (BBsentials.dataStorage.getIsland() != packet.warp.getIsland())
                BBsentials.sender.addSendTask(packet.warp.getIsland().getWarpCommand());
        }
    }

    public void onPausePacket(PausePacket packet) {
        BBsentials.pauseWarping = packet.setPaused;
    }

    public void onReceivedPublicChatMessageAddonPacket(ReceivedPublicChatMessageAddonPacket packet) {
    }

    public void onShareUpdateTime(ShareUpdateTime packet) {
        if (Objects.equals(packet.serverID, BBsentials.dataStorage.serverId)) {
            long updateTime = Duration.between(Instant.now(), packet.updateTime).getSeconds();
            if (updateTime <= 3) {
                BBsentials.sender.addImmediateSendTask("/l");
                Chat.sendPrivateMessageToSelfFatal("WARNING: ERROR DETECTED! YOU JOINED A SERVER THAT WILL UPDATE SOON EMERGENCY LEFT");
            }
            else if (updateTime <= 7) {
                BBsentials.doLeaveTask();
                BBsentials.sender.addSendTask("/l", 2);
                Chat.sendPrivateMessageToSelfFatal("WARNING: ERROR DETECTED! YOU JOINED A SERVER THAT WILL UPDATE SOON EMERGENCY LEFT");
            }
            else {
                BBsentials.futureServerLeave.cancel(false);
                BBsentials.futureServerLeave = BBsentials.executionService.schedule(BBsentials::doLeaveTask, updateTime - 7, TimeUnit.SECONDS);
            }
        }
    }

    public void onRequestUpdateTime(RequestUpdateTime packet) {
        if (Objects.equals(packet.serverID, BBsentials.dataStorage.serverId))
            sendPacket(new ShareUpdateTime(packet.serverID, BBsentials.temporaryConfig.lastPlaytimeUpdate.plusSeconds(60)));
    }

    public void onRequestLobbyData(RequestLobbyData requestLobbyData) {
        sendPacket(new SendLobbyData(BBsentials.dataStorage, BBsentials.temporaryConfig.lastPlaytimeUpdate, BBsentials.funConfig.lowPlaytimeHelperJoinDate));
    }

    public void onSendLobbyData(SendLobbyData sendLobbyData) {
        if (BBsentials.altDataStorage != null && BBsentials.altDataStorage.getIsland() != null && BBsentials.dataStorage.getIsland().isPersonalIsland())
            BBsentials.altDataStorage.getIsland().setLastLeave(sendLobbyData.serverJoinTime);
        if (sendLobbyData.lastPlaytimeUpdate != null)
            Islands.putPlaytimeUpdate(sendLobbyData.dataStorage.serverId, sendLobbyData.lastPlaytimeUpdate);
        BBsentials.altDataStorage = sendLobbyData.dataStorage;
        BBsentials.altLastplaytimeUpdate = sendLobbyData.lastPlaytimeUpdate;
    }
}
