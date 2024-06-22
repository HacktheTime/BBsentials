package de.hype.bbsentials.client.common.hpmodapi;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBDataStorage;
import de.hype.bbsentials.client.common.client.BBsentials;
import net.hypixel.modapi.handler.ClientboundPacketHandler;
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
import static de.hype.bbsentials.client.common.client.BBsentials.generalConfig;

public class HypixelModAPICore implements ClientboundPacketHandler {
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
        if (developerConfig.devMode) Chat.sendPrivateMessageToSelfDebug("HP-Mod-API-Rec" + packet);
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
    }

    public void onPartyInfoPacket(ClientboundPartyInfoPacket packet) {
        handlePacketDebug(packet);
        completeGoal(packet, HPModAPIPacket.PARTYINFO.getType());
        if (packet.isInParty()) {
            if (packet.getLeader().get().toString().equals(generalConfig.getMCUUID())) {
                BBsentials.partyConfig.isPartyLeader = true;
            }
        }
    }



    public void onPlayerInfoPacket(ClientboundPlayerInfoPacket packet) {
        handlePacketDebug(packet);
        completeGoal(packet, HPModAPIPacket.PLAYER_INFO.getType());
    }

    @Override
    public void handle(ClientboundHypixelPacket p) {
        if (p instanceof ClientboundHelloPacket) onHelloEvent((ClientboundHelloPacket) p);
        if (p instanceof ClientboundLocationPacket) onLocationEvent((ClientboundLocationPacket) p);
        if (p instanceof ClientboundPingPacket) onPingPacket((ClientboundPingPacket) p);
        if (p instanceof ClientboundPlayerInfoPacket) onPlayerInfoPacket((ClientboundPlayerInfoPacket) p);
        if (p instanceof ClientboundPartyInfoPacket) onPartyInfoPacket((ClientboundPartyInfoPacket) p);
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
