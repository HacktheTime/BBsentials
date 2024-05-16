package de.hype.bbsentials.client.common.client;

import de.hype.bbsentials.shared.constants.EnumUtils;
import de.hype.bbsentials.shared.constants.Islands;
import net.hypixel.data.region.Environment;
import net.hypixel.data.type.GameType;
import net.hypixel.data.type.ServerType;
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket;

public class BBDataStorage {
    public final String serverId;
    public final String lobbyName;
    public final String mode;
    public final Islands island;
    public final String map;
    public final ServerType gameType;
    public String currentProfileID;
    public String profileType;
    public String currentProfileCuteName;
//    public final String proxyName;
//    public final Environment environment;

    public BBDataStorage(ClientboundLocationPacket packet) {
        BBDataStorage data = BBsentials.dataStorage;
        this.serverId = packet.getServerName();
        if (packet.getLobbyName().isPresent()) this.lobbyName = packet.getLobbyName().get();
        else {
            lobbyName = null;
        }
        if (packet.getMap().isPresent()) this.map = packet.getMap().get();
        else {
            map = null;
        }
        if (packet.getServerType().isPresent()) this.gameType = packet.getServerType().get();
        else {
            gameType = null;
        }
        this.currentProfileID = data.currentProfileID;
        this.profileType = data.profileType;
        this.currentProfileCuteName = data.currentProfileCuteName;
//        this.proxyName = packet.getProxyName();
//        this.environment = packet.getEnvironment();
        if (packet.getMode().isPresent()) {
            this.mode = packet.getMode().get();
            if (isInSkyblock()) this.island = EnumUtils.getEnumByValue(Islands.class, mode);
            else this.island = null;
        }
        else {
            mode = null;
            island = null;
        }
    }

    public Islands getIsland() {
        return island;
    }

    public boolean isInSkyblock() {
        return isOnHypixel() && gameType == GameType.SKYBLOCK;
    }

    public boolean isOnHypixel() {
        return true;
//                environment != null;
    }

//    public boolean isOnMain() {
//        return environment == Environment.PRODUCTION;
//    }
}
