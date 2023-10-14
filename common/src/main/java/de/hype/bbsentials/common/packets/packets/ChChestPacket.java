package de.hype.bbsentials.common.packets.packets;

import de.hype.bbsentials.common.constants.enviromentShared.ChChestItem;
import de.hype.bbsentials.common.packets.AbstractPacket;

public class ChChestPacket extends AbstractPacket {

    public ChChestPacket(String announcerUsername, ChChestItem[] items, String locationCoords, String bbcommand, String extraMessage) {
        super(1, 1); //Min and Max supported Version
        this.announcerUsername = announcerUsername;
        this.locationCoords = locationCoords;
        this.bbcommand = bbcommand;
        this.extraMessage = extraMessage;
        this.items = items;
    }

    public final String announcerUsername;
    public final String locationCoords;
    public final String bbcommand;
    public final String extraMessage;
    public final ChChestItem[] items;

}
