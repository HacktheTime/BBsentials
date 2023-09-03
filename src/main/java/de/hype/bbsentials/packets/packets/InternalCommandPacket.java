package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

//Only used for small things which don't really need an own Packet.
public class InternalCommandPacket extends AbstractPacket {

    public InternalCommandPacket(String command, String[] parameters) {
        super(1, 1); //Min and Max supported Version
        this.command = command;
        this.parameters = parameters;
    }

    public final String command;
    public final String[] parameters;

}
