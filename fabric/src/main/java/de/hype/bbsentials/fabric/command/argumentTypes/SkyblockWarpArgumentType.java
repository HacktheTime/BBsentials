package de.hype.bbsentials.fabric.command.argumentTypes;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SkyblockWarpArgumentType implements ArgumentType<String> {
    private static final List<String> warps = List.of(
            "home",
            "island",
            "hub",
            "village",
            "elizabeth",
            "castle",
            "da",
            "crypt",
            "crypts",
            "museum",
            "dungeon_hub",
            "dungeons",
            "dhub",
            "barn",
            "desert",
            "trapper",
            "trap",
            "park",
            "jungle",
            "howl",
            "gold",
            "deep",
            "mines",
            "forge",
            "forge",
            "crystals",
            "hollows",
            "dh",
            "nucleus",
            "spider",
            "spiders",
            "top",
            "nest",
            "mound",
            "arachne",
            "end",
            "drag",
            "void",
            "sepulture",
            "crimson",
            "nether",
            "isle",
            "kuudra",
            "wasteland",
            "dragontail",
            "scarleton",
            "smoldering",
            "smoldering_tomb",
            "smold",
            "garden",
            "winter",
            "jerry",
            "workshop",
            "basecamp",
            "camp",
            "glacite",
            "base",
            "tunnels",
            "tunnel",
            "gt");

    private SkyblockWarpArgumentType() {
    }

    public static SkyblockWarpArgumentType warptype() {
        return new SkyblockWarpArgumentType();
    }

    public static String getWarpString(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        String current = builder.getRemainingLowerCase();
        warps.parallelStream().forEach(v->{
            if (v.startsWith(current)) {
                builder.suggest(v);
            }
        });
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return warps;
    }
}
