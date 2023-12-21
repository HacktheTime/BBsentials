package de.hype.bbsentials.shared.constants;

/**
 * Enumeration representing internal reasons constants for a specific functionality.
 * These constants are used to provide detailed information about various scenarios or states.
 * <p>
 * The following are the possible reasons represented by this enumeration:
 * <ul>
 *     <li>{@link #INVALID_PARAMETER}: Invalid parameter was provided.</li>
 *     <li>{@link #MISSING_PARAMETER}: Required parameter is missing.</li>
 *     <li>{@link #INSUFFICIENT_PRIVILEGES}: Insufficient privileges to perform the operation.</li>
 *     <li>{@link #MUTED}: User is muted.</li>
 *     <li>{@link #BANNED}: User is banned.</li>
 *     <li>{@link #API_UNSUPPORTED}: API feature is unsupported.</li>
 *     <li>{@link #INVALID_LOGIN}: Invalid login attempt.</li>
 *     <li>{@link #KICKED}: User was kicked from the server.</li>
 *     <li>{@link #ANOTHER_LOGIN}: Another login detected for the same user.</li>
 *     <li>{@link #SERVER_RESTART}: Server is restarting.</li>
 *     <li>{@link #NOT_REGISTERED}: User is not registered.</li>
 *     <li>{@link #ON_COOLDOWN}: Operation is on cooldown.</li>
 *     <li>{@link #OTHER}: Other unspecified reason.</li>
 * </ul>
 */
public enum InternalReasonConstants {
    INVALID_PARAMETER,
    MISSING_PARAMETER,
    INSUFFICIENT_PRIVILEGES,
    MUTED,
    BANNED,
    API_UNSUPPORTED,
    INVALID_LOGIN,
    KICKED,
    ANOTHER_LOGIN,
    SERVER_RESTART,
    NOT_REGISTERED,
    ON_COOLDOWN,
    OTHER
}
