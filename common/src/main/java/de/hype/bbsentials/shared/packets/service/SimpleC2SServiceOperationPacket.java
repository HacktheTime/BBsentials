package de.hype.bbsentials.shared.packets.service;

/**
 * Used when the Service is full and the Service can be started
 */
public class SimpleC2SServiceOperationPacket {
    public final int serviceId;
    public final ClientServiceOperation operation;

    public SimpleC2SServiceOperationPacket(ClientServiceOperation operation, int serviceId) {
        this.serviceId = serviceId;
        this.operation = operation;
    }

    public enum ClientServiceOperation {
        START(true),
        CLOSE_SERVICE(true),
        READY_ANSWER(false),
        JOIN(false),
        LEAVE(false),
        ;
        final boolean requiresHoster;

        ClientServiceOperation(boolean requiresHoster) {
            this.requiresHoster = requiresHoster;
        }

        public boolean requiresHoster() {
            return requiresHoster;
        }
    }
}

