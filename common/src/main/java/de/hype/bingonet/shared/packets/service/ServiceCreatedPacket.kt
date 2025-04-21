package de.hype.bingonet.shared.packets.service;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.objects.BBServiceData;

public class ServiceCreatedPacket extends AbstractPacket {
    public final BBServiceData data;

    public ServiceCreatedPacket(BBServiceData data) {
        super(1, 1);
        this.data = data;
    }
}
