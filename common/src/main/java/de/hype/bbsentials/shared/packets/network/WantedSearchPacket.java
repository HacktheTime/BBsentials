package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

/**
 * Used to find collect Data across Servers. Can be used to find Users or Lobbies by ID
 */
public class WantedSearchPacket extends AbstractPacket {
    public boolean targetFound = true;
    public String finder;
    public String username;
    public String serverId;

    public WantedSearchPacket(String mcUsername, String serverId) {
        super(1, 1);
        this.username = mcUsername;
        this.serverId = serverId;
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

    public static WantedSearchPacket findMegaServer() {
        WantedSearchPacket packet = new WantedSearchPacket(null, "mega");
        packet.targetFound = false;
        return packet;
    }
}
