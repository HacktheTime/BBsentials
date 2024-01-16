package de.hype.bbsentials.shared.constants;

/**
 * Allowed party operations.
 * <p>
 * The available enums are:
 * <ul>
 *     {@link #INVITE}: Represents the type for sending party invitations.</li>
 *     {@link #ACCEPT}: Represents the type for accepting party invitations.</li>
 *     {@link #DISBAND}: Represents the type for disbanding a party.</li>
 *     {@link #KICK}: Represents the type for kicking a user from the party.</li>
 * </ul>
 */
public enum PartyConstants {
    INVITE,
    JOIN,
    TRANSFER,
    ACCEPT,
    DISBAND,
    KICK
}
