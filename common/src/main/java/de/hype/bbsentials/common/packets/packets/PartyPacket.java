package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.constants.enviromentShared.PartyConstants;
import de.hype.bbsentials.common.packets.AbstractPacket;

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
