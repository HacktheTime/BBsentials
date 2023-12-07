package de.hype.bbsentials.common.constants.enviromentShared;

/**
 * Allowed party operations.
 * <p>
 * The available enums are:
 * <ul>
 *     <li>{@link #INVITE}: Represents the type for sending party invitations.</li>
 *     <li>{@link #ACCEPT}: Represents the type for accepting party invitations.</li>
 *     <li>{@link #DISBAND}: Represents the type for disbanding a party.</li>
 *     <li>{@link #KICK}: Represents the type for kicking a user from the party.</li>
 * </ul>
 */
public enum PartyConstants {
    INVITE,  // Represents the type for party invites.
    ACCEPT,  // Represents the type for accepting party invitations.
    DISBAND, // Represents the type for disbanding a party.
    KICK     // Represents the type for kicking a user from the party.
}
