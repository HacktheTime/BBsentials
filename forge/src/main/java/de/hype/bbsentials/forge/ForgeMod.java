package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.forge.client.MoulConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "bbsentials", useMetadata = true)
public class ForgeMod {
    static boolean alreadyInialised = false;
    static BBsentials sentials = new BBsentials();
    public static MoulConfig config = new MoulConfig();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        printLocation();
        EnvironmentCore core = new EnvironmentCore(new BBUtils(), new ForgeChat(), new MCUtils(), new Commands(), new Options(), new DebugThread());
        BBsentials.init();
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
        BBsentials.onServerJoin();
    }
}

