package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.constants.PartyConstants;

/**
 * sends the client the request to execute the party command.
 *
 * @see PartyPacket
 */
public class PartyPacket extends AbstractPacket {
    /**
     * @param type  Party Command Type {@link PartyConstants}
     * @param users users is just a reference for what behind the type.
     */
    public PartyPacket(PartyConstants type, String[] users) {
        super(1, 1); //Min and Max supportet Version
        this.type = type;
        this.users = users;
    }

    public final PartyConstants type;
    public final String[] users;

}
