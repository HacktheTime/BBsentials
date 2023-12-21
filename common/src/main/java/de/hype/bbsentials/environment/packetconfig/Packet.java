package de.hype.bbsentials.environment.packetconfig;

import java.util.function.Consumer;

public class Packet<T extends AbstractPacket> {

    private final Class<T> clazz;
    private final Consumer<T> consumer;

    public Packet(Class<T> clazz, Consumer<T> consumer) {
        this.clazz = clazz;
        this.consumer = consumer;
    }

    public String getName() {
        return clazz.getSimpleName();
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public Consumer<T> getConsumer() {
        return consumer;
    }
}
