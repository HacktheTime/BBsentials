package de.hype.bbsentials.client.common.hpmodapi;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBDataStorage;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.config.PartyManager;
import de.hype.bbsentials.environment.packetconfig.PacketManager;
import net.hypixel.modapi.HypixelModAPI;
import net.hypixel.modapi.packet.ClientboundHypixelPacket;
import net.hypixel.modapi.packet.impl.VersionedPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPingPacket;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPlayerInfoPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static de.hype.bbsentials.client.common.client.BBsentials.developerConfig;

public class HypixelModAPICore {
    private Map<HPModAPIPacketEnum, List<HPModPacketAssociation>> waiting = new HashMap<>();

    public <T extends VersionedPacket> void addToWaiting(HPModAPIPacketEnum type, CompletableFuture<T> future) {
        List<HPModPacketAssociation> t = waiting.getOrDefault(type, new ArrayList<>());
        t.add(new HPModPacketAssociation<T>(future));
        waiting.put(type, t);
    }

    public <T extends VersionedPacket> void completeGoal(T packet, HPModAPIPacketEnum type) {
        waiting.computeIfPresent(type, (key, list) -> {
            for (HPModPacketAssociation hpModPacketAssociation : list) {
                hpModPacketAssociation.complete(packet);
            }
            list.clear();
            return list;
        });

    }

    private void handlePacketDebug(ClientboundHypixelPacket packet) {
        if (developerConfig.devMode) Chat.sendPrivateMessageToSelfDebug("HP-Mod-API-Rec: " + packet);
    }

    public void onPingPacket(ClientboundPingPacket packet) {
        handlePacketDebug(packet);
        completeGoal(packet, HPModAPIPacket.PING.getType());
    }

    public void onLocationEvent(ClientboundLocationPacket packet) {
        handlePacketDebug(packet);
        BBsentials.dataStorage = new BBDataStorage(packet);
    }

    public void onHelloEvent(ClientboundHelloPacket packet) {
        handlePacketDebug(packet);
        BBsentials.dataStorage = new BBDataStorage(packet);
    }

    public void onPartyInfoPacket(ClientboundPartyInfoPacket packet) {
        handlePacketDebug(packet);
        completeGoal(packet, HPModAPIPacket.PARTYINFO.getType());
        PartyManager.leechData(packet);
    }


    public void onPlayerInfoPacket(ClientboundPlayerInfoPacket packet) {
        handlePacketDebug(packet);
        completeGoal(packet, HPModAPIPacket.PLAYER_INFO.getType());
    }

    public void register() {
        HypixelModAPI.getInstance().subscribeToEventPacket(ClientboundLocationPacket.class);
        HypixelModAPI.getInstance().registerHandler(ClientboundHelloPacket.class, this::onHelloEvent);
        HypixelModAPI.getInstance().registerHandler(ClientboundPingPacket.class, this::onPingPacket);
        HypixelModAPI.getInstance().registerHandler(ClientboundPlayerInfoPacket.class, this::onPlayerInfoPacket);
        HypixelModAPI.getInstance().registerHandler(ClientboundPartyInfoPacket.class, this::onPartyInfoPacket);
        HypixelModAPI.getInstance().registerHandler(ClientboundLocationPacket.class, this::onLocationEvent);
    }

    public static class HPModPacketAssociation<T extends VersionedPacket> {

        private final CompletableFuture<T> future;

        public HPModPacketAssociation(CompletableFuture<T> future) {
            this.future = future;
        }

        public void complete(T packet) {
            future.complete(packet);
        }
    }
}
