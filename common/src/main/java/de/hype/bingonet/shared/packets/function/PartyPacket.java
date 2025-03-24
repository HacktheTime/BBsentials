package de.hype.bingonet.shared.packets.function;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.constants.PartyConstants;

import java.util.List;

/**
 * sends the client the request to execute the party command.
 *
 * @see PartyPacket
 */
public class PartyPacket extends AbstractPacket {
    public final PartyConstants type;
    public final List<String> users;
    public final boolean serverBypass;
    /**
     * @param type         Party Command Type {@link PartyConstants}
     * @param users        users is just a reference for what behind the type.
     * @param serverBypass true when server did verification for example when hosting bingo party.
     */
    public PartyPacket(PartyConstants type, List<String> users, boolean serverBypass) {
        super(1, 1); //Min and Max supportet Version
        this.type = type;
        this.users = users;
        this.serverBypass = serverBypass;
    }

}
