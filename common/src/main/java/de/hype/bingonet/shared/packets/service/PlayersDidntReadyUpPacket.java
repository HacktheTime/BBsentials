package de.hype.bingonet.shared.packets.service;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.server.objects.BBUser;

import java.util.Set;

/**
 * send when the timeout for the ready up has been reached and not everyone readied up.
 */
public class PlayersDidntReadyUpPacket extends AbstractPacket {
    int serverId;
    Set<BBUser> participiants;

    public PlayersDidntReadyUpPacket(int serverId, Set<BBUser> participiants) {
        super(1, 1);
        this.participiants = participiants;
        this.serverId = serverId;
    }
}
