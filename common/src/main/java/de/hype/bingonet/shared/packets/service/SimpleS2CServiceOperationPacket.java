package de.hype.bingonet.shared.packets.service;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.objects.BBServiceData;

public class SimpleS2CServiceOperationPacket extends AbstractPacket {
    public final BBServiceData service;
    public final ClientServiceOperation operation;

    public SimpleS2CServiceOperationPacket(ClientServiceOperation operation, BBServiceData service) {
        super(1, 1);
        this.service = service;
        this.operation = operation;
    }

    public enum ClientServiceOperation {
        IS_READY_QUESTION,
        INFORM_SERVICE_FULL_READY
    }
}
