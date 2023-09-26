package de.hype.bbsentials.packets;

import de.hype.bbsentials.communication.BBsentialConnection;
import de.hype.bbsentials.packets.packets.*;

import java.util.ArrayList;
import java.util.List;

public class PacketManager {
    private static List<Packet<? extends AbstractPacket>> packets = new ArrayList<>();

    public List<Packet<? extends AbstractPacket>> getPackets() {
        return packets;
    }

    // Define a map to store packet classes and their associated actions
    BBsentialConnection connection;

    // Method to initialize packet actions
    public PacketManager(BBsentialConnection connection) {
        this.connection = connection;
        initializePacketActions(connection);
    }

    public static void initializePacketActions(BBsentialConnection connection) {
        packets.add(new Packet<>(BingoChatMessagePacket.class, connection::onBingoChatMessagePacket));
        packets.add(new Packet<>(BroadcastMessagePacket.class, connection::onBroadcastMessagePacket));
        packets.add(new Packet<>(ChChestPacket.class, connection::onChChestPacket));
        packets.add(new Packet<>(DisconnectPacket.class, connection::onDisconnectPacket));
        packets.add(new Packet<>(DisplayTellrawMessagePacket.class, connection::onDisplayTellrawMessagePacket));
//        packets.add(new Packet<>(InternalCommandPacket.class, connection::dummy));
        packets.add(new Packet<>(InvalidCommandFeedbackPacket.class, connection::onInvalidCommandFeedbackPacket));
        packets.add(new Packet<>(MiningEventPacket.class, connection::onMiningEventPacket));
        packets.add(new Packet<>(PartyPacket.class, connection::onPartyPacket));
//        packets.add(new Packet<>(RequestConnectPacket.class, connection::dummy));
        packets.add(new Packet<>(SplashNotifyPacket.class, connection::onSplashNotifyPacket));
        packets.add(new Packet<>(SystemMessagePacket.class, connection::onSystemMessagePacket));
        packets.add(new Packet<>(WelcomeClientPacket.class, connection::onWelcomePacket));
    }

    // Method to handle a received packet


    //   method to get a list of all packets
    public static List<Class<? extends AbstractPacket>> getAllPacketClasses() {
        initializePacketActions(null);
        List<Class<? extends AbstractPacket>> allPackets = new ArrayList<>();
        for (int i = 0; i < allPackets.size(); i++) {
            allPackets.add(packets.get(i).getClazz());
        }
        return allPackets;
    }
}
