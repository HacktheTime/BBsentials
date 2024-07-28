package de.hype.bbsentials.fabric.mixins.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin<S> implements CommandSource {
    @Final
    @Shadow
    private ClientPlayNetworkHandler networkHandler;
    @Final
    private List<PlayerListEntry> playerList;

    public ClientCommandSourceMixin(ClientPlayNetworkHandler networkHandler) {
    }

    /**
     * @return Collection of player names.
     * @author HacktheTime
     * @reason Remove hypixels dummy players from the list.
     * Overwrites the getPlayerNames() method with the new implementation.
     * This method returns a collection of player names from the playerList.
     * This method is now also used by server-side commands.
     */
    @ModifyReturnValue(method = "getPlayerNames", at = @At("RETURN"))
    public Collection<String> BBsentials$getPlayerNames(Collection<String> original) {
        original.removeIf((entry) -> entry.startsWith("!"));
        return original;
    }
}
