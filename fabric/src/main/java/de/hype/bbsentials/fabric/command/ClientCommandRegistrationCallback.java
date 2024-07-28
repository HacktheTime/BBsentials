package de.hype.bbsentials.fabric.command;/*
 * Code copied from the Fabric Project and adjusted where needed
 */

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

public interface ClientCommandRegistrationCallback {
    Event<ClientCommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(ClientCommandRegistrationCallback.class, (callbacks) -> (dispatcher) -> {
        for (ClientCommandRegistrationCallback callback : callbacks) {
            callback.register(dispatcher);
        }
    });

    void register(BBCommandDispatcher dispatcher);
}
