package de.hype.bbsentials.client.common.client.socketAddons;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.ChatPrompt;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.environment.addonpacketconfig.AbstractAddonPacket;
import de.hype.bbsentials.environment.addonpacketconfig.AddonPacketUtils;
import de.hype.bbsentials.shared.objects.ClientWaypointData;
import de.hype.bbsentials.shared.packets.addonpacket.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
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
        BBsentials.sender.addSendTask(packet.message.replace("§.","").replace("\n","").substring(0,Math.min(255,packet.message.length())), packet.timing);
    }

    public void onServerCommandAddonPacket(ServerCommandAddonPacket packet) {
        if (!BBsentials.socketAddonConfig.allowAutomatedSending) return;
        BBsentials.sender.addSendTask("/" + packet.command.replace("§.","").replace("\n","").substring(0,Math.min(254,packet.command.length())), packet.timing);
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
            if (BBsentials.socketAddonConfig.addonDebug && !(packet.getClass().equals(ReceivedPublicChatMessageAddonPacket.class)&&!BBsentials.socketAddonConfig.addonChatDebug)) {
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
}
