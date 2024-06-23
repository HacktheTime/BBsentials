package de.hype.bbsentials.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import java.util.Collection;
import java.util.List;


public interface CommandOverrideCallback {
    Event<CommandOverrideCallback> EVENT = EventFactory.createArrayBacked(CommandOverrideCallback.class, (callbacks) -> (helper) -> {
        for (CommandOverrideCallback callback : callbacks) {
            callback.register(helper);
        }
    });

    void register(ReplaceHelper helper);

    public static class ReplaceHelper {
        CommandDispatcher<FabricClientCommandSource> dispatcher;
        List<String> allCommands;
        Collection<CommandNode<FabricClientCommandSource>> childen;

        public ReplaceHelper(CommandDispatcher<FabricClientCommandSource> dispatcher, List<String> allCommands) {
            this.dispatcher = dispatcher;
            this.allCommands = allCommands;
            childen = dispatcher.getRoot().getChildren();
        }

        public void replace(LiteralArgumentBuilder<FabricClientCommandSource> node){
            if (childen.removeIf(n->n.getName().equals(node.getLiteral()))) {
                dispatcher.register(node);
            }
        }
    }
}