package de.hype.bingonet.environment.addonpacketconfig;

import de.hype.bingonet.client.common.client.socketAddons.AddonHandler;
import de.hype.bingonet.shared.packets.addonpacket.*;

import java.util.ArrayList;
import java.util.List;

public class AddonPacketManager {

    private static AddonPacketManager lastAddonPacketManager = null;
    List<AddonPacket<? extends AbstractAddonPacket>> addonPackets = new ArrayList<>();

    public List<AddonPacket<? extends AbstractAddonPacket>> getPackets() {
        return addonPackets;
    }

    // Define a map to store packet classes and their associated actions
    AddonHandler connection;

    // Method to initialize packet actions
    public AddonPacketManager(AddonHandler connection) {
        this.connection = connection;
        initializePacketActions(connection);
        lastAddonPacketManager = this;
    }

    public void initializePacketActions(AddonHandler connection) {
        addonPackets.add(new AddonPacket<>(DisplayTellrawMessageAddonPacket.class, connection::onDisplayTellrawMessageAddonPacket));
        addonPackets.add(new AddonPacket<>(DisplayClientsideMessageAddonPacket.class, connection::onDisplayClientsideMessageAddonPacket));
        addonPackets.add(new AddonPacket<>(PlaySoundAddonPacket.class, connection::onPlaySoundAddonPacket));
        addonPackets.add(new AddonPacket<>(PublicChatAddonPacket.class, connection::onPublicChatAddonPacket));
        addonPackets.add(new AddonPacket<>(ServerCommandAddonPacket.class, connection::onServerCommandAddonPacket));
        addonPackets.add(new AddonPacket<>(ChatPromptAddonPacket.class, connection::onChatPromptAddonPacket));
        addonPackets.add(new AddonPacket<>(WaypointAddonPacket.class, connection::onWaypointAddonPacket));
        addonPackets.add(new AddonPacket<>(GetWaypointsAddonPacket.class, connection::onGetWaypointsAddonPacket));
        addonPackets.add(new AddonPacket<>(ClientCommandAddonPacket.class, connection::onClientCommandAddonPacket));
//        addonPackets.add(new AddonPacket<>(ReceivedPublicChatMessageAddonPacket.class, connection::onReceivedPublicChatMessageAddonPacket));
        addonPackets.add(new AddonPacket<>(StatusUpdateAddonPacket.class, connection::onStatusUpdateAddonPacket));


    }

    // Method to handle a received packet


    //   method to get a list of all packets
    public static List<Class<? extends AbstractAddonPacket>> getAllPacketClasses() {
        if (lastAddonPacketManager == null) {
            lastAddonPacketManager = new AddonPacketManager(null);
        }
        List<Class<? extends AbstractAddonPacket>> allPackets = new ArrayList<>();
        for (int i = 0; i < lastAddonPacketManager.addonPackets.size(); i++) {
            allPackets.add(lastAddonPacketManager.addonPackets.get(i).getClazz());
        }
        return allPackets;
    }
}
