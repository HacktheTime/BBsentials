package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.PunishmentData;

/**
 * Send von Client to Server to punish a User in the Network with a Ban, Mute or Blacklist.
 */
public class PunishUserPacket extends AbstractPacket {
    public PunishmentData data;

    public PunishUserPacket(PunishmentData data) {
        super(1, 1);
        this.data = data;
    }

}
