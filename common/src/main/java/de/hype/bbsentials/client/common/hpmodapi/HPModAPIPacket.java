package de.hype.bbsentials.client.common.hpmodapi;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import net.hypixel.modapi.packet.impl.VersionedPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundLocationPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundLocationPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPingPacket;
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPlayerInfoPacket;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class HPModAPIPacket {
    public static final PING_REGISTRY PING = new PING_REGISTRY();
    public static final PLAYER_INFO_REGISTRY PLAYER_INFO = new PLAYER_INFO_REGISTRY();
    public static final LOCATION_REGISTRY LOCATION = new LOCATION_REGISTRY();
    public static final PARTYINFO_REGISTRY PARTYINFO = new PARTYINFO_REGISTRY();

    Class<? extends VersionedPacket> serverboundClass;
    Class<? extends VersionedPacket> clientboundClass;

    HPModAPIPacket(Class<? extends VersionedPacket> serverboundClass, Class<? extends VersionedPacket> clientboundClass) {
        this.serverboundClass = serverboundClass;
        this.clientboundClass = clientboundClass;
    }

    public <T extends VersionedPacket> CompletableFuture<T> send(Class<T> t) {
        CompletableFuture<T> future = new CompletableFuture<>();

        // Send the packet
        VersionedPacket packetToSend;
        try {
            packetToSend = serverboundClass.newInstance();
            EnvironmentCore.utils.sendPacket(packetToSend);
        } catch (InstantiationException | IllegalAccessException e) {
            future.completeExceptionally(e);
            return future;
        }

        return future;
    }

    private abstract static class BasicPacket<Server extends VersionedPacket, Client extends VersionedPacket> {
        public Class<Server> clazz;

        public BasicPacket(Class<Server> clazz) {
            this.clazz = clazz;
        }

        public void send() {
            Server packet = getServerInstance();
            if (packet == null) return;
            EnvironmentCore.utils.sendPacket(packet);
        }

        private Server getServerInstance() {
            try {
                return clazz.newInstance();
            } catch (Exception e) {
                return null;
            }
        }

        public void schedule(Consumer<Client> task) {
            Server packet = getServerInstance();
            if (packet == null) task.accept(null);
            CompletableFuture<Client> future = new CompletableFuture<>();
            future.thenAccept(task);
            BBsentials.hpModAPICore.addToWaiting(getType(), future);
            EnvironmentCore.utils.sendPacket(packet);
        }

        public Client complete() {
            Server packet = getServerInstance();
            if (packet == null) return null;
            CompletableFuture<Client> future = new CompletableFuture<>();
            BBsentials.hpModAPICore.addToWaiting(getType(), future);
            EnvironmentCore.utils.sendPacket(packet);

            try {
                return future.get();
            } catch (Exception e) {
                return null;
            }

        }

        public abstract HPModAPIPacketEnum getType();
    }

    public static class PING_REGISTRY extends BasicPacket<ServerboundPingPacket, ClientboundPingPacket> {
        PING_REGISTRY() {
            super(ServerboundPingPacket.class);
        }

        public HPModAPIPacketEnum getType() {
            return HPModAPIPacketEnum.PING;
        }
    }

    public static class PLAYER_INFO_REGISTRY extends BasicPacket<ServerboundPlayerInfoPacket, ClientboundPlayerInfoPacket> {
        PLAYER_INFO_REGISTRY() {
            super(ServerboundPlayerInfoPacket.class);
        }

        public HPModAPIPacketEnum getType() {
            return HPModAPIPacketEnum.PLAYER_INFO;
        }
    }

    public static class LOCATION_REGISTRY extends BasicPacket<ServerboundLocationPacket, ClientboundLocationPacket> {
        LOCATION_REGISTRY() {
            super(ServerboundLocationPacket.class);
        }

        public HPModAPIPacketEnum getType() {
            return HPModAPIPacketEnum.LOCATION;
        }
    }

    public static class PARTYINFO_REGISTRY extends BasicPacket<ServerboundPartyInfoPacket, ClientboundPartyInfoPacket> {
        PARTYINFO_REGISTRY() {
            super(ServerboundPartyInfoPacket.class);
        }

        public HPModAPIPacketEnum getType() {
            return HPModAPIPacketEnum.PARTYINFO;
        }
    }
}
