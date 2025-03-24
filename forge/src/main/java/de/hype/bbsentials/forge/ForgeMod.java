package de.hype.bingonet.forge;

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.client.common.mclibraries.TextUtils;
import de.hype.bingonet.forge.client.MoulConfig;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@Mod(modid = "bingonet", useMetadata = true)
public class ForgeMod {
    static boolean alreadyInialised = false;
    static Bingo Net sentials;
    public static MoulConfig config = new MoulConfig();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        printLocation();
        EnvironmentCore core = EnvironmentCore.forge(new Utils(), new MCEvents(), new ForgeChat(), new Commands(), new Options(), new DebugThread(), new TextUtils() {
            @Override
            public String getContentFromJson(String json) {
                return IChatComponent.Serializer.jsonToComponent(json).getUnformattedText();
            }

            @Override
            public String getJsonFromContent(String content) {
                return IChatComponent.Serializer.componentToJson(new IChatComponent("sdd"));
            }
        });
        MinecraftForge.EVENT_BUS.register(this);
        sentials = new BingoNet();
        BingoNet.init();
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
    public void onClientConnected(PlayerEvent.PlayerRespawnEvent event) {
        BingoNet.onServerJoin();
    }

}

