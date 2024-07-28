package de.hype.bbsentials.fabric.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IBBsentialsCommandSource;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IRootCommandNodeMixinAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mojang.text2speech.Narrator.LOGGER;

public class BBCommandDispatcher extends CommandDispatcher<IBBsentialsCommandSource> {
    private static BBCommandDispatcher INSTANCE;
    private final List<LiteralArgumentBuilder<IBBsentialsCommandSource>> replaceChilds = new ArrayList<LiteralArgumentBuilder<IBBsentialsCommandSource>>();


    public BBCommandDispatcher() {
        INSTANCE = this;
    }

    private static <T extends CommandSource & IBBsentialsCommandSource> void copyChildren(
            CommandNode<CommandSource> origin,
            CommandNode<CommandSource> target,
            CommandSource source,
            Map<CommandNode<CommandSource>, CommandNode<CommandSource>> originalToCopy
    ) {
        for (CommandNode<CommandSource> child : origin.getChildren()) {

            ArgumentBuilder<CommandSource, ?> builder = child.createBuilder();
            // Reset the unnecessary non-completion stuff from the builder

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

    public static boolean executeCommand(String command) {
        MinecraftClient client = MinecraftClient.getInstance();

        // The interface is implemented on ClientCommandSource with a mixin.
        // noinspection ConstantConditions
        IBBsentialsCommandSource commandSource = (IBBsentialsCommandSource) client.getNetworkHandler().getCommandSource();

        client.getProfiler().push(command);

        try {
            INSTANCE.execute(command, commandSource);
            return true;
        } catch (CommandSyntaxException e) {
            boolean ignored = isIgnoredException(e.getType());

            if (ignored) {
                LOGGER.debug("Syntax exception for client-sided command '{}'", command, e);
                return false;
            }

            LOGGER.warn("Syntax exception for client-sided command '{}'", command, e);
            commandSource.sendError(getErrorMessage(e));
            return true;
        } catch (Exception e) {
            LOGGER.warn("Error while executing client-sided command '{}'", command, e);
            commandSource.sendError(Text.of(e.getMessage()));
            return true;
        } finally {
            client.getProfiler().pop();
        }
    }

    private static Text getErrorMessage(CommandSyntaxException e) {
        Text message = Texts.toText(e.getRawMessage());
        String context = e.getContext();

        return context != null ? Text.translatable("command.context.parse_error", message, e.getCursor(), context) : message;
    }

    private static boolean isIgnoredException(CommandExceptionType type) {
        BuiltInExceptionProvider builtins = CommandSyntaxException.BUILT_IN_EXCEPTIONS;

        // Only ignore unknown commands and node parse exceptions.
        // The argument-related dispatcher exceptions are not ignored because
        // they will only happen if the user enters a correct command.
        return type == builtins.dispatcherUnknownCommand() || type == builtins.dispatcherParseException();
    }

    public LiteralCommandNode<IBBsentialsCommandSource> replaceRegister(LiteralArgumentBuilder<IBBsentialsCommandSource> command) {
        LiteralArgumentBuilder<IBBsentialsCommandSource> toReturn = command;
        replaceChilds.add(toReturn);
        return toReturn.build();
    }

    public void addCommands(CommandDispatcher<CommandSource> target, CommandSource source) {
//        IRootCommandNodeMixinAccess<CommandSource> parsedTarget = (IRootCommandNodeMixinAccess) (Object) target.getRoot();
//        parsedTarget.BBsentials$replaceNodes(replaceChilds);
        for (LiteralArgumentBuilder<IBBsentialsCommandSource> replaceChild : replaceChilds) {
            if (target.getRoot().getChildren().removeIf(child -> child.getName().equals(replaceChild.getLiteral()))) {
                target.register((LiteralArgumentBuilder<CommandSource>) (Object) replaceChild);
                BBCommandDispatcher.INSTANCE.register(replaceChild);
            }
        }
        Map<CommandNode<CommandSource>, CommandNode<CommandSource>> originalToCopy = new HashMap<>();
        originalToCopy.put((CommandNode<CommandSource>) (Object) getRoot(), target.getRoot());
        copyChildren((CommandNode<CommandSource>) (Object) getRoot(), target.getRoot(), source, originalToCopy);
    }
}
