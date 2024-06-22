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
    private final static List<String> skyblockItemIds = BBsentials.neuRepoManager.getItemIds().stream().filter(id->!id.contains(";")).toList();

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
        if (!current.isEmpty()) {
            skyblockItemIds.parallelStream().forEach(v -> {
                if (v.toLowerCase().contains(current)) builder.suggest(v);
            });
        }else {
            for (String materialId : skyblockItemIds) {
                builder.suggest(materialId);
            }
        }
        try {
            return builder.buildFuture();
        } catch (Exception e) {
            return Suggestions.empty();
        }
    }

    @Override
    public Collection<String> getExamples() {
        return List.of("CARROT_GENERATOR_6", "FURBALL", "EMERALD_GENERATOR_4", "SPEEDSTER_ROD");
    }
}

