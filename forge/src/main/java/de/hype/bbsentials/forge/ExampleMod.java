package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import io.github.moulberry.moulconfig.gui.MoulConfigEditor;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
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
        printLocation();

        EnvironmentCore core = new EnvironmentCore(new BBUtils(), new ForgeChat(), new MCUtils(), new Commands(), new Options(), new DebugThread());
        MinecraftForge.EVENT_BUS.register(this);
    }
    public void printLocation() {
//        try {
//            // Get the URL of the JAR file containing the class
//            URL jarUrl = this.getClass().getProtectionDomain().getCodeSource().getLocation();
//
//            // Convert the URL to a URI
//            File jarFile = new File(jarUrl.toURI());
//
//            // Get the absolute path of the JAR file
//            String jarPath = jarFile.getAbsolutePath();
//
////            throw new RuntimeException(jarPath);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }
    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        BBsentials.onServerSwap();
    }
}

