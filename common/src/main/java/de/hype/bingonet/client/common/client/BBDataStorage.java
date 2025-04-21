package de.hype.bingonet.client.common.client;

import de.hype.bingonet.shared.constants.Islands;
import net.hypixel.data.region.Environment;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundHelloPacket;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

import java.time.Instant;

public class BBDataStorage {
    public final String serverId;
    public final String lobbyName;
    public final String mode;
    public Islands island;
    public final String map;
    public final ServerType gameType;
    public String currentProfileID = null;
    public String profileType = null;
    public String currentProfileCuteName = null;
    public Environment environment;
    public Instant joinTime;
//    public final String proxyName;
//    public final Environment environment;

    public BBDataStorage(ClientboundLocationPacket packet) {
        BBDataStorage data = BingoNet.dataStorage;
        this.serverId = packet.getServerName();
        if (packet.getLobbyName().isPresent()) this.lobbyName = packet.getLobbyName().get();
        else {
            lobbyName = null;
        }
        if (packet.getMap().isPresent()) {
            this.map = packet.getMap().get();
            this.island = Islands.getIslandByMap(map);
        } else {
            map = null;
            island = null;
        }
        if (packet.getServerType().isPresent()) this.gameType = packet.getServerType().get();
        else {
            gameType = null;
        }
        if (data != null) {
            this.currentProfileID = data.currentProfileID;
            this.profileType = data.profileType;
            this.currentProfileCuteName = data.currentProfileCuteName;
        }

//        this.proxyName = packet.getProxyName();
//        this.environment = packet.getEnvironment();
        if (packet.getMode().isPresent()) {
            this.mode = packet.getMode().get();
        } else {
            mode = null;
        }
        joinTime = Instant.now();
    }

    public BBDataStorage(ClientboundHelloPacket packet) {
        packet.getEnvironment();
        map = null;
        gameType = null;
        island = null;
        mode = null;
        lobbyName = null;
        serverId = null;
        joinTime = Instant.now();
    }

    public Islands getIsland() {
        return island;
    }


    public boolean isInSkyblock() {
        return gameType == GameType.SKYBLOCK;
    }

    public boolean isOnHypixel() {
        return environment != null;
    }

    public boolean isOnMain() {
        return environment == Environment.PRODUCTION;
    }

    public boolean isInLimbo() {
        return serverId != null && serverId.equals("limbo");
    }

    /**
     * @return The time when you joined the lobby (like sb hub) your currently in.
     */
    public Instant getServerJoinTime() {
        return joinTime;
    }
}
