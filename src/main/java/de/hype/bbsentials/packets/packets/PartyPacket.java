package de.hype.bbsentials.packets.packets;

import de.hype.bbsentials.packets.AbstractPacket;

public class PartyPacket extends AbstractPacket {

    public PartyPacket(String type, String[] users) {
        super(1, 1); //Min and Max supportet Version
        this.type = type;
        this.users = users;
    }

    public final String type;
    public final String[] users;

}
