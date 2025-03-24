package de.hype.bingonet.fabric.command;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import de.hype.bingonet.fabric.mixins.mixin.BingoNetCommandSource;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.IBingoNetCommandSource;

public class ClientCommandManager {
    private ClientCommandManager() {
    }

    /**
     * Creates a literal argument builder.
     *
     * @param name the literal name
     * @return the created argument builder
     */
    public static LiteralArgumentBuilder<IBingoNetCommandSource> literal(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    /**
     * Creates a required argument builder.
     *
     * @param name the name of the argument
     * @param type the type of the argument
     * @param <T>  the type of the parsed argument value
     * @return the created argument builder
     */
    public static <T> RequiredArgumentBuilder<IBingoNetCommandSource, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
