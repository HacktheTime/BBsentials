package de.hype.bingonet.shared.packets.network;


import de.hype.bingonet.environment.packetconfig.AbstractPacket;

/**
 * Message in the Bingo Net Environment. Shared Discord and Mod in-game
 */
public class BingoChatMessagePacket extends AbstractPacket {
    /**
     * @param prefix      the prefix the sender has.
     * @param username    the username the sender has. (In game name)
     * @param message     message
     * @param bingo_cards the count of cards the user has.
     */
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
}
