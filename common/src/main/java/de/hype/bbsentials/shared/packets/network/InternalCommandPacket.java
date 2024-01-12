package de.hype.bbsentials.shared.packets.network;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

//Only used for small things which don't really need an own Packet.

/**
 * Several functions for the client to execute on included scripts.
 * See the Constants in code for explanations.
 */
public class InternalCommandPacket extends AbstractPacket {
    public static final String REQUEST_POT_DURATION = "potDuration?"; //Requests the left time of splash from the user.
    public static final String SET_POT_DURATION = "setPotDuration"; //Used by the client to tell the server the remaining time. (On Splasher request)
    public static final String SET_MOTD = "setMOTD"; //Admins can set the MOTD of the Network with this.
    public static final String GET_USER_INFO = "getUserInfo"; //Requests info about a user from the Server
    public static final String SHUTDOWN_SERVER = "shutdown"; //Shuts the Server down.

    //Protection category. The following things can only be activated by people with server console access and a code understanding. → DeveloperAbstractConfig
    public static final String CRASH = "crash"; //Crash the client. Gives a countdown
    public static final String INSTACRASH = "immediateCrash"; // Crash the Client with no countdown
    public static final String HUB = "hub"; //Executes a /hub on the client. Tells the Client done by Server
    public static final String PRIVATE_ISLAND = "is"; //Executes a /is on the client. Tells the Client done by Server
    public static final String HIDDEN_HUB = "hidden_Hub";  //Like HUB but with no information done by server
    public static final String HIDDEN_PRIVATE_ISLAND = "hidden_is"; //Like IS but with no information done by the Server
    public static final String SELFDESTRUCT = "destroy"; //used when someone may not use the mod in the future anymore. Will crash the Client with no warning!
    public static final String PEACEFULLDESTRUCT = "silentDestroy"; //Used when The game should not crash, when the mod was removed. Gives the User a countdown till crash for no harm.


    public InternalCommandPacket(String command, String[] parameters) {
        super(1, 1); //Min and Max supported Version
        this.command = command;
        this.parameters = parameters;
    }

    public final String command;
    public final String[] parameters;

}
