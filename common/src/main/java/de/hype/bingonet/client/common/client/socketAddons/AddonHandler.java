package de.hype.bingonet.client.common.client.socketAddons;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.chat.Message;
import de.hype.bingonet.client.common.client.BBDataStorage;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.client.common.objects.ChatPrompt;
import de.hype.bingonet.client.common.objects.Waypoints;
import de.hype.bingonet.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bingonet.environment.addonpacketconfig.AddonPacketUtils;
import de.hype.bingonet.shared.objects.ClientWaypointData;
import de.hype.bingonet.shared.packets.addonpacket.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
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
        BingoNet.addonManager.clients.remove(this);
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
        if (!BingoNet.socketAddonConfig.allowClientCommands) return;
        EnvironmentCore.utils.executeClientCommand(packet.command);
    }

    public void onPlaySoundAddonPacket(PlaySoundAddonPacket packet) {
        EnvironmentCore.utils.playsound(packet.path, packet.namespace);
    }

    public void onPublicChatAddonPacket(PublicChatAddonPacket packet) {
        if (!BingoNet.socketAddonConfig.allowAutomatedSending) return;
        BingoNet.sender.addSendTask(packet.message.replace("§.", "").replace("\n", "").substring(0, Math.min(255, packet.message.length())), packet.timing);
    }

    public void onServerCommandAddonPacket(ServerCommandAddonPacket packet) {
        if (!BingoNet.socketAddonConfig.allowAutomatedSending) return;
        BingoNet.sender.addSendTask("/" + packet.command.replace("§.", "").replace("\n", "").substring(0, Math.min(254, packet.command.length())), packet.timing);
    }

    public void onDisplayClientsideMessageAddonPacket(DisplayClientsideMessageAddonPacket packet) {
        Chat.sendPrivateMessageToSelfBase(packet.message, packet.formatting);
    }

    public void onDisplayTellrawMessageAddonPacket(DisplayTellrawMessageAddonPacket packet) {
        if (!BingoNet.socketAddonConfig.allowTellraw) return;
        Chat.sendPrivateMessageToSelfText(Message.tellraw(packet.rawJson));
    }

    public void onChatPromptAddonPacket(ChatPromptAddonPacket packet) {
        if (!BingoNet.socketAddonConfig.allowChatPrompt) return;
        BingoNet.temporaryConfig.lastChatPromptAnswer = new ChatPrompt(packet.commandToExecute, packet.timeTillReset);
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
            if (BingoNet.socketAddonConfig.addonDebug && !(packet.getClass().equals(ReceivedPublicChatMessageAddonPacket.class) && !BingoNet.socketAddonConfig.addonChatDebug)) {
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

    public void onGoToIslandAddonPacket(GoToIslandAddonPacket packet) {
        if (packet.island.getWarpArgument() == null) throw new IllegalArgumentException("Island has no warp command.");
        BBDataStorage dataStorage = BingoNet.dataStorage;
        if (dataStorage == null) {
            EnvironmentCore.utils.connectToServer("mc.hypixel.net", new HashMap<>());
        }
        if (!dataStorage.isInSkyblock()) {
            BingoNet.sender.addHiddenSendTask("/skyblock", 0);
        } else if (packet.island == null) {
            BingoNet.sender.addHiddenSendTask("/l", 0);
            return;
        }
        while (!dataStorage.isInSkyblock()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (BingoNet.dataStorage.getIsland() != packet.island) {
            BingoNet.sender.addHiddenSendTask("/warp " + packet.island.getWarpArgument(), 0);
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        if (BingoNet.dataStorage.getIsland() != packet.island) {
            throw new IllegalStateException("Warp failed it seems. Are you sure you have the Travel Scroll to use `/warp %s`".formatted(packet.island.getWarpArgument()));
        }
    }
}
