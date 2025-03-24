package de.hype.bingonet.fabric.command.argumentTypes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.client.CustomGson;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SackMaterialArgumentType implements ArgumentType<String> {

    public static List<String> materialIds = getAllSackContents();

    public static SackMaterialArgumentType materialidtype() {
        return new SackMaterialArgumentType();
    }

    public static String getItemId(final CommandContext<?> context, final String name) {
        return context.getArgument(name, String.class);
    }

    public static List<String> getAllSackContents() {
        List<String> combinedContents = new ArrayList<>();

        try {
            // Download JSON from URL
            String jsonString = BingoNet.downloadJson("https://raw.githubusercontent.com/NotEnoughUpdates/NotEnoughUpdates-REPO/master/constants/sacks.json");

            // Parse JSON
            JsonObject jsonObject = CustomGson.create().fromJson(jsonString, JsonObject.class);
            JsonObject sacks = jsonObject.getAsJsonObject("sacks");

            // Iterate through all sacks
            for (String sackName : sacks.keySet()) {
                JsonObject sack = sacks.getAsJsonObject(sackName);
                JsonArray contentsArray = sack.getAsJsonArray("contents");

                // Add each item to combinedContents list
                for (int i = 0; i < contentsArray.size(); i++) {
                    combinedContents.add(contentsArray.get(i).getAsString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return combinedContents;
    }

    @Override
    public String parse(final StringReader reader) throws CommandSyntaxException {
        return reader.readString();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(final CommandContext<S> context, final SuggestionsBuilder builder) {
        String current = builder.getRemainingLowerCase();
        if (!current.isEmpty()) {
            new ArrayList<>(materialIds).forEach(v -> {
                if (v.toLowerCase().contains(current)) builder.suggest(v);
            });
        }else {
            for (String materialId : new ArrayList<>(materialIds)) {
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
        return List.of("WHEAT", "ENCHANTED_HAY_BALE");
    }
}
