package de.hype.bbsentials.fabric.command.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.hype.bbsentials.client.common.client.BBsentials;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkyblockItemIdArgumentType implements ArgumentType<String> {


    private SkyblockItemIdArgumentType() {
    }

    public static SkyblockItemIdArgumentType itemidtype() {
        return new SkyblockItemIdArgumentType();
    }

    public static String getItemId(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        String current = builder.getRemainingLowerCase();
        if (current.length() <= 2) return builder.buildFuture(); //So less resources are required;
        BBsentials.itemIds.keySet().parallelStream().forEach(v -> {
            if (v.startsWith(current)) builder.suggest(v);
        });
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("CARROT_GENERATOR_6", "FURBALL", "EMERALD_GENERATOR_4", "SPEEDSTER_ROD");
    }
}

