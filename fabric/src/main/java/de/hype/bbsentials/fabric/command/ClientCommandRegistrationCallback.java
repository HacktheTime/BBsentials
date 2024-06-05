package de.hype.bbsentials.fabric.command;/*
 * Code copied from the Fabric Project and adjusted where needed
 */

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.command.CommandRegistryAccess;

public interface ClientCommandRegistrationCallback {
    Event<ClientCommandRegistrationCallback> EVENT = EventFactory.createArrayBacked(ClientCommandRegistrationCallback.class, (callbacks) -> (dispatcher) -> {
        for (ClientCommandRegistrationCallback callback : callbacks) {
            callback.register(dispatcher);
        }
    });

    void register(BBCommandDispatcher dispatcher);
}
