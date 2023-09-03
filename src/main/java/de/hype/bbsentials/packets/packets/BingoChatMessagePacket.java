package de.hype.bbsentials.packets.packets;


import de.hype.bbsentials.packets.AbstractPacket;

public class BingoChatMessagePacket extends AbstractPacket {

    public BingoChatMessagePacket(String prefix, String username, String message, int bingo_cards) {
        super(1, 1); //Min and Max supported Version
        this.message = message;
        this.username = username;
        this.prefix = prefix;
        this.bingo_cards = bingo_cards;
    }

    public final String message;
    public final String username;
    public final String prefix;
    public final int bingo_cards;

    public boolean baseCheck() {
        boolean cancelPacket = false;

        return !cancelPacket;
    }
}
