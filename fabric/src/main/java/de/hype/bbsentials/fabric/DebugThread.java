package de.hype.bbsentials.fabric;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import de.hype.bbsentials.client.common.chat.Chat;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.client.common.client.DebugThread {
    public static List<Object> store = new ArrayList<>();
    boolean doTest = false;

    @Override
    public void loop() {
        if (doTest) {
            doTest = false;
            test();
        }
    }

    public void onNumpadCode() {
    }


    Collection<CommandNode<FabricClientCommandSource>> getClientCommands() {
        return ClientCommandManager.getActiveDispatcher().getRoot().getChildren();
    }

    RootCommandNode<CommandSource> getServerCommands() {
        return MinecraftClient.getInstance().getNetworkHandler().getCommandDispatcher().getRoot();
    }

    public void replaceCommand(String name) {
        Collection<CommandNode<FabricClientCommandSource>> clientCommandChilds = getClientCommands();
        Collection<CommandNode<CommandSource>> commandChilds = getServerCommands().getChildren();

        Iterator<CommandNode<CommandSource>> iterator = commandChilds.iterator();
        while (iterator.hasNext()) {
            CommandNode<CommandSource> child = iterator.next();
            if (child.getName().equals(name)) {
                iterator.remove(); // Safely remove the element
            }
        }

        ClientCommandManager.getActiveDispatcher().register(
                ClientCommandManager.literal(name)
                        .then(ClientCommandManager.argument("playernames", StringArgumentType.greedyString())
                                .suggests((context, builder) -> {
                                    // Provide tab-completion options for classes
                                    List<String> playerNames = List.of("hi", "hi2", "hi3");
                                    // Replace with your own logic to retrieve class names
                                    return CommandSource.suggestMatching(playerNames, builder);
                                })
                                .executes((context -> {
                                    Chat.sendPrivateMessageToSelfImportantInfo("Test Success: " + StringArgumentType.getString(context, "playernames"));
                                    return 1;
                                }))
                        )
        );
    }

    public void doOnce() {
        doTest = true;
    }

    public void unlockCursor() {
        MinecraftClient.getInstance().mouse.unlockCursor();
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

}
