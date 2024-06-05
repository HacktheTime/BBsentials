package de.hype.bbsentials.fabric.mixins.mixin;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IBBsentialsCommandSource;
import de.hype.bbsentials.shared.constants.Islands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@Mixin(ClientCommandSource.class)
public abstract class BBsentialsCommandSource implements IBBsentialsCommandSource {
    @Unique
    Islands currentIsland;
    @Final
    @Shadow
    private MinecraftClient client;
    @Inject(method = "<init>",at = @At("RETURN"))
    public void constructor(ClientPlayNetworkHandler networkHandler, MinecraftClient client, CallbackInfo ci) {
        currentIsland=EnvironmentCore.utils.getCurrentIsland();
    }

    public Islands BBsentials$getCurrentIsland(){
        return currentIsland;
    }

    public void sendError(Text message) {
        sendFeedback(Text.literal("").append(message).formatted(Formatting.RED));
    }
    @Override
    public MinecraftClient getClient() {
        return client;
    }

    /**
     * Gets the player that used the command.
     *
     * @return the player
     */
    public ClientPlayerEntity getPlayer() {
        return client.player;
    }

    public void sendFeedback(Text message) {
        client.inGameHud.getChatHud().addMessage(message);
        client.getNarratorManager().narrate(message);
    }

    public ClientWorld getWorld() {
        return client.world;
    }

    @Shadow
    public abstract Collection<String> getPlayerNames();
    @Shadow
    public abstract Collection<String> getTeamNames();

    @Shadow
    public abstract Stream<Identifier> getSoundIds() ;

    @Shadow
    public abstract Stream<Identifier> getRecipeIds() ;

    @Shadow
    public abstract CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) ;

    @Shadow
    public abstract  Set<RegistryKey<World>> getWorldKeys();

    @Shadow
    public abstract DynamicRegistryManager getRegistryManager() ;

    @Shadow
    public abstract  FeatureSet getEnabledFeatures() ;

    @Shadow
    public abstract  CompletableFuture<Suggestions> listIdSuggestions(RegistryKey<? extends Registry<?>> registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context);

    @Shadow
    public abstract boolean hasPermissionLevel(int level);
}
