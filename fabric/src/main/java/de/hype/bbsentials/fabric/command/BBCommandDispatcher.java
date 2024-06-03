package de.hype.bbsentials.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IBBsentialsCommandSource;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BBCommandDispatcher extends CommandDispatcher<IBBsentialsCommandSource> {
    private static List<LiteralCommandNode<IBBsentialsCommandSource>> replaceChilds = new ArrayList<>();


    private static <T extends CommandSource & IBBsentialsCommandSource> void copyChildren(
            CommandNode<CommandSource> origin,
            CommandNode<CommandSource> target,
            CommandSource source,
            Map<CommandNode<CommandSource>, CommandNode<CommandSource>> originalToCopy
    ) {
        for (CommandNode<CommandSource> child : origin.getChildren()) {

            ArgumentBuilder<CommandSource, ?> builder = child.createBuilder();
            // Reset the unnecessary non-completion stuff from the builder
            builder.requires(s -> true); // This is checked with the if check above.

            if (builder.getCommand() != null) {
                builder.executes(context -> 0);
            }

            // Set up redirects
            if (builder.getRedirect() != null) {
                builder.redirect(originalToCopy.get(builder.getRedirect()));
            }
            CommandNode<CommandSource> result = builder.build();
            originalToCopy.put(child, result);
            target.addChild(result);

            if (!child.getChildren().isEmpty()) {
                copyChildren(child, result, source, originalToCopy);
            }
        }
    }

    public LiteralCommandNode<IBBsentialsCommandSource> replaceRegister(LiteralArgumentBuilder<IBBsentialsCommandSource> command) {
        LiteralCommandNode<IBBsentialsCommandSource> toReturn = command.build();
        replaceChilds.add(toReturn);
        return toReturn;
    }

    public void addCommands(CommandDispatcher<CommandSource> target, CommandSource source) {
        Map<CommandNode<CommandSource>, CommandNode<CommandSource>> originalToCopy = new HashMap<>();
        originalToCopy.put((CommandNode<CommandSource>) (Object) getRoot(), target.getRoot());
        copyChildren((CommandNode<CommandSource>) (Object) getRoot(), target.getRoot(), source, originalToCopy);
    }
}
