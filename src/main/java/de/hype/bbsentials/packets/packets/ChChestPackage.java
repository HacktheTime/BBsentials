package de.hype.bbsentials.packets.packets;

 
import de.hype.bbsentials.constants.enviromentShared.ChChestItem;
import de.hype.bbsentials.packets.AbstractPacket;

public class ChChestPackage extends AbstractPacket {

    public ChChestPackage(String announcerUsername, ChChestItem[] items, String locationCoords, String bbcommand, String extraMessage) {
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
