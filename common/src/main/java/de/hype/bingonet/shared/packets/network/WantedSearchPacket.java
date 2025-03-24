package de.hype.bingonet.shared.packets.network;


import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.constants.Islands;

import java.util.List;

/**
 * Used to find collect Data across Servers. Can be used to find Users or Lobbies by ID
 */
public class WantedSearchPacket extends AbstractPacket {
    public boolean targetFound = true;
    public String username;
    public Islands island;
    public Boolean mega;
    public Integer minimumPlayerCount;
    public Integer maximumPlayerCount;
    public String serverId;

    public WantedSearchPacket(String mcUsername, String serverId) {
        super(1, 1);
        this.username = mcUsername;
        this.serverId = serverId;
    }

    public WantedSearchPacket(String mcUsername, String serverId, Islands island, Boolean mega, Integer minimumPlayerCount, Integer maximumPlayerCount) {
        super(1, 1);
        this.username = mcUsername;
        this.serverId = serverId;
        this.mega = mega;
        this.island = island;
        this.minimumPlayerCount = minimumPlayerCount;
        this.maximumPlayerCount = maximumPlayerCount;
    }

    public static WantedSearchPacket findMcUser(String username) {
        WantedSearchPacket packet = new WantedSearchPacket(username, null);
        packet.targetFound = false;
        return packet;
    }

    public static WantedSearchPacket findServer(String serverId) {
        WantedSearchPacket packet = new WantedSearchPacket(null, serverId);
        packet.targetFound = false;
        return packet;
    }

    public static WantedSearchPacket findPrivateMega(Islands island) {
        WantedSearchPacket packet = new WantedSearchPacket(null, null, island, true, null, 15);
        packet.targetFound = false;
        return packet;
    }

    public static WantedSearchPacket findPrivateMegaHubServer() {
        WantedSearchPacket packet = new WantedSearchPacket(null, "private-hub-mega");
        packet.targetFound = false;
        return packet;
    }

    public static class WantedSearchPacketReply extends AbstractPacket {
        public String finder;
        public List<String> usernames;
        public Boolean megaServer;
        public Integer currentPlayerCount;
        public String serverId;

        public WantedSearchPacketReply(String finder, List<String> usernames, Boolean megaServer, String serverId) {
            super(1, 1);
            this.finder = finder;
            this.usernames = usernames;
            this.megaServer = megaServer;
            this.currentPlayerCount = usernames.size();
            this.serverId = serverId;
        }
    }
}
