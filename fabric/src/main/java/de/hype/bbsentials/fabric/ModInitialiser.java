package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.client.BBsentials;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class ModInitialiser implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> {
            BBsentials.onServerSwap();
        });
    }
}
