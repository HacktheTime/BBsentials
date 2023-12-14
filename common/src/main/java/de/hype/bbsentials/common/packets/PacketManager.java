package de.hype.bbsentials.common.packets;

import de.hype.bbsentials.common.client.SplashManager;
import de.hype.bbsentials.common.communication.BBsentialConnection;
import de.hype.bbsentials.common.packets.packets.*;

import java.util.ArrayList;
import java.util.List;

public class PacketManager {

    private static PacketManager lastPacketManager = null;
    List<Packet<? extends AbstractPacket>> packets = new ArrayList<>();

    public List<Packet<? extends AbstractPacket>> getPackets() {
        return packets;
    }

    // Define a map to store packet classes and their associated actions
    BBsentialConnection connection;

    // Method to initialize packet actions
    public PacketManager(BBsentialConnection connection) {
        this.connection = connection;
        initializePacketActions(connection);
        lastPacketManager = this;
    }

    public void initializePacketActions(BBsentialConnection connection) {
        packets.add(new Packet<>(BingoChatMessagePacket.class, connection::onBingoChatMessagePacket));
        packets.add(new Packet<>(BroadcastMessagePacket.class, connection::onBroadcastMessagePacket));
        packets.add(new Packet<>(ChChestPacket.class, connection::onChChestPacket));
        packets.add(new Packet<>(DisconnectPacket.class, connection::onDisconnectPacket));
        packets.add(new Packet<>(DisplayTellrawMessagePacket.class, connection::onDisplayTellrawMessagePacket));
        packets.add(new Packet<>(InternalCommandPacket.class, connection::onInternalCommandPacket));
        packets.add(new Packet<>(InvalidCommandFeedbackPacket.class, connection::onInvalidCommandFeedbackPacket));
        packets.add(new Packet<>(MiningEventPacket.class, connection::onMiningEventPacket));
        packets.add(new Packet<>(PartyPacket.class, connection::onPartyPacket));
//        packets.add(new Packet<>(RequestConnectPacket.class, connection::dummy));
        packets.add(new Packet<>(SplashNotifyPacket.class, connection::onSplashNotifyPacket));
        packets.add(new Packet<>(SystemMessagePacket.class, connection::onSystemMessagePacket));
        packets.add(new Packet<>(WelcomeClientPacket.class, connection::onWelcomePacket));
        packets.add(new Packet<>(RequestAuthentication.class, connection::onRequestAuthentication));
        packets.add(new Packet<>(SplashUpdatePacket.class, SplashManager::updateSplash));
    }

    // Method to handle a received packet


    //   method to get a list of all packets
    public static List<Class<? extends AbstractPacket>> getAllPacketClasses() {
        if (lastPacketManager == null) {
            lastPacketManager = new PacketManager(null);
        }
        List<Class<? extends AbstractPacket>> allPackets = new ArrayList<>();
        for (int i = 0; i < lastPacketManager.packets.size(); i++) {
            allPackets.add(lastPacketManager.packets.get(i).getClazz());
        }
        return allPackets;
    }
}
