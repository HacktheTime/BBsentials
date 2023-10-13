package de.hype.bbsentials.forge.common.packets.packets;

import de.hype.bbsentials.forge.common.packets.AbstractPacket;

//Only used for small things which don't really need an own Packet.
public class InternalCommandPacket extends AbstractPacket {
    public static final String REQUEST_POT_DURATION= "potDuration?";
    public static final String SET_POT_DURATION= "setPotDuration";
    public static final String SET_MOTD= "setMOTD";
    public static final String GET_USER_INFO= "getUserInfo";
    public static final String SHUTDOWN_SERVER= "shutdown";

    //Protection category. The following things can only be activated by people with server console access and an code understanding.
    public static final String CRASH= "crash";
    public static final String INSTACRASH= "immediateCrash";
    public static final String HUB= "hub";
    public static final String PRIVATE_ISLAND= "is";
    public static final String HIDDEN_HUB= "hidden_Hub";
    public static final String HIDDEN_PRIVATE_ISLAND= "hidden_is";
    public static final String SELFDESTRUCT= "destroy"; //used when someone may not sue the mod in the future anymore
    public static final String PEACEFULLDESTRUCT= "silentDestroy"; //Used when The game should not crash, when the mod was removed

    public InternalCommandPacket(String command, String[] parameters) {
        super(1, 1); //Min and Max supported Version
        this.command = command;
        this.parameters = parameters;
    }

    public final String command;
    public final String[] parameters;

}
