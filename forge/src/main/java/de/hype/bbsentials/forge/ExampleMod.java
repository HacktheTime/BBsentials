package de.hype.bbsentials.forge;

import de.hype.bbsentials.forge.client.BBsentials;
import de.hype.bbsentials.forge.client.CommandBBI;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "bbsentials", useMetadata = true)
public class ExampleMod {
    static boolean alreadyInialised = false;
    static BBsentials sentials = new BBsentials();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void clientSetup() {
        if (!alreadyInialised) {
            ClientCommandHandler.instance.registerCommand(new CommandBBI());
            alreadyInialised = true;
            sentials.init();
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        clientSetup();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}

