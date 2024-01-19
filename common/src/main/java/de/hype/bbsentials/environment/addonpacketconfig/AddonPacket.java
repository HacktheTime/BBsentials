package de.hype.bbsentials.environment.addonpacketconfig;

import java.util.function.Consumer;

public class AddonPacket<T extends AbstractAddonPacket> {

    private final Class<T> clazz;
    private final Consumer<T> consumer;

    public AddonPacket(Class<T> clazz, Consumer<T> consumer) {
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
