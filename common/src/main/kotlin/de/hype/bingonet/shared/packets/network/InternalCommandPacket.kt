package de.hype.bingonet.shared.packets.network

import de.hype.bingonet.environment.packetconfig.AbstractPacket

//Only used for small things which don't really need an own Packet.
/**
 * Several functions for the client to execute on included scripts.
 * See the Constants in code for explanations.
 */
class InternalCommandPacket(@JvmField val command: String?, @JvmField val parameters: Array<String>) :
    AbstractPacket(1, 1) {
    companion object {
        const val REQUEST_POT_DURATION: String = "potDuration?" //Requests the left time of splash from the user.
        const val SET_POT_DURATION: String =
            "setPotDuration" //Used by the client to tell the server the remaining time. (On Splasher request)
        const val SET_MOTD: String = "setMOTD" //Admins can set the MOTD of the Network with this.
        const val GET_USER_INFO: String = "getUserInfo" //Requests info about a user from the Server
        const val SHUTDOWN_SERVER: String = "shutdown" //Shuts the Server down.

        //Protection Category. The following things can only be activated by people with server console access and a code understanding. → Developers
        const val CRASH: String = "crash" //Crash the client. Gives a countdown
        const val INSTACRASH: String = "immediateCrash" // Crash the Client with no countdown
        const val HUB: String = "hub" //Executes a /hub on the client. Tells the Client done by Server
        const val PRIVATE_ISLAND: String = "is" //Executes a /is on the client. Tells the Client done by Server
        const val HIDDEN_HUB: String = "hidden_Hub" //Like HUB but with no information done by server
        const val HIDDEN_PRIVATE_ISLAND: String = "hidden_is" //Like IS but with no information done by the Server
        const val SELFDESTRUCT: String =
            "instadestroy" //used when someone may not use the mod in the future anymore. Will crash the Client with no warning!
        const val PEACEFULLDESTRUCT: String =
            "destroy" //Used when The game should not crash, when the mod was removed. Gives the User a countdown till crash for no harm.
    }
}
