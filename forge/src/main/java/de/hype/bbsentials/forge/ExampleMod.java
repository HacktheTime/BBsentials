package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
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
        EnvironmentCore core = new EnvironmentCore(new BBUtils(), new ForgeChat(), new MCUtils(), new Commands(), new Options(), new DebugThread());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        BBsentials.onServerSwap();
    }
}

