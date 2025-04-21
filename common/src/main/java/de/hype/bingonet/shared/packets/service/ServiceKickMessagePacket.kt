package de.hype.bingonet.shared.packets.service;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

public class ServiceKickMessagePacket extends AbstractPacket {
    public final String message;
    public final int serviceId;
    public final boolean suggestRejoin;

    public ServiceKickMessagePacket(int serviceId, boolean suggestRejoin, String message) {
        super(1, 1);
        this.message = message;
        this.serviceId = serviceId;
        this.suggestRejoin = suggestRejoin;
    }
}
