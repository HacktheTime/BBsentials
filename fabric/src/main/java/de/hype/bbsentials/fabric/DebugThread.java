package de.hype.bbsentials.fabric;

import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.hpmodapi.HPModAPIPacket;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.objects.Waypoints;
import de.hype.bbsentials.shared.objects.Position;
import de.hype.bbsentials.shared.objects.RenderInformation;
import de.hype.bbsentials.shared.objects.WaypointData;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DebugThread extends de.hype.bbsentials.client.common.client.DebugThread {
    public static volatile AtomicInteger timerSinceLastTick = new AtomicInteger(0);
    boolean doTest = false;
    MinecraftClient minecraft;

    public void unlockCursor() {
        MinecraftClient.getInstance().mouse.unlockCursor();
    }

    @Override
    public void loop() {
        if (doTest) {
            doTest = false;
            test();
        }
    }

    public void onNumpadCode() {
//        init();
        ClientboundPartyInfoPacket packet = HPModAPIPacket.PARTYINFO.complete();
        return;
    }

    Collection<CommandNode<FabricClientCommandSource>> getClientCommands() {
        return ClientCommandManager.getActiveDispatcher().getRoot().getChildren();
    }


    public <T> void triggerRenderBreakpoint(List<T> data) {
    }

//    public void replaceCommand(String name) {
//        Collection<CommandNode<FabricClientCommandSource>> clientCommandChilds = getClientCommands();
//        Collection<CommandNode<CommandSource>> commandChilds = getServerCommands().getChildren();
//
//        Iterator<CommandNode<CommandSource>> iterator = commandChilds.iterator();
//        while (iterator.hasNext()) {
//            CommandNode<CommandSource> child = iterator.next();
//            if (child.getName().equals(name)) {
//                iterator.remove(); // Safely remove the element
//            }
//        }
//
//        ClientCommandManager.getActiveDispatcher().register(
//                ClientCommandManager.literal(name)
//                        .then(ClientCommandManager.argument("playernames", StringArgumentType.greedyString())
//                                .suggests((context, builder) -> {
//                                    // Provide tab-completion options for classes
//                                    List<String> playerNames = List.of("hi", "hi2", "hi3");
//                                    // Replace with your own logic to retrieve class names
//                                    return CommandSource.suggestMatching(playerNames, builder);
//                                })
//                                .executes((context -> {
//                                    Chat.sendPrivateMessageToSelfImportantInfo("Test Success: " + StringArgumentType.getString(context, "playernames"));
//                                    return 1;
//                                }))
//                        )
//        );
//    }

    RootCommandNode<CommandSource> getServerCommands() {
        return MinecraftClient.getInstance().getNetworkHandler().getCommandDispatcher().getRoot();
    }

    public void doOnce() {
        doTest = true;
    }

    @Override
    public List<String> test() {
        return List.of("");
    }

    public void setScreen(Screen screen) {
        if (screen == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(screen));
    }

    @Override
    public void init() {
        super.init();
        ClientTickEvents.START_CLIENT_TICK.register(i -> timerSinceLastTick.set(0));
        ClientTickEvents.END_CLIENT_TICK.register(i -> timerSinceLastTick.set(0));
        BBsentials.executionService.scheduleAtFixedRate(() -> {
            timerSinceLastTick.getAndAdd(1);
            if (timerSinceLastTick.get() > 20 && MinecraftClient.getInstance().currentScreen == null) {
                timerSinceLastTick.set(0);
//                    Chat.sendPrivateMessageToSelfInfo("Cursor unlocked");
                unlockCursor();
            }

        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onServerJoin() {
        Chat.sendPrivateMessageToSelfError("Hub Join exec");
    }

    @Override
    public void onServerLeave() {
        Chat.sendPrivateMessageToSelfInfo(EnvironmentCore.utils.getServerId());
        Chat.sendPrivateMessageToSelfError("Hub Leave exec");
    }

    //Keep in mind that this is not allowed and should only be used for debugging purposes ONLY.
    public void setWaypointsFromEntities(List<Entity> entities) {
        if (!EnvironmentCore.utils.getServerConnectedAddress().startsWith("alpha.hypixel.net")) return;
        Waypoints.waypoints.clear();
        for (Entity entity : entities) {
            BlockPos pos = entity.getBlockPos();
            new Waypoints(new WaypointData(new Position(pos.getX(), pos.getY(), pos.getZ()), Message.of("ddd").getJson(), 1000, true, false, List.of(new RenderInformation(null, null)), true));
        }
    }

    @Override
    public boolean isDevEnv() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }
}
