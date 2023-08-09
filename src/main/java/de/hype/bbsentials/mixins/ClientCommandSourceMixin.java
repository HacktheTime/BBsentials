package de.hype.bbsentials.mixins;

import com.google.common.collect.Lists;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Environment(EnvType.CLIENT)
@Mixin(ClientCommandSource.class)
public abstract class ClientCommandSourceMixin {
    @Shadow
    private final ClientPlayNetworkHandler networkHandler;
    private final MinecraftClient client;
    @Final
    private List<PlayerListEntry> playerList;

    /**
     * @return Collection of player names.
     * @author HacktheTime
     * @reason Remove hypixels dummy players from the list.
     * Overwrites the getPlayerNames() method with the new implementation.
     * This method returns a collection of player names from the playerList.
     * This method is now also used by server-side commands.
     */
    @Overwrite
    public Collection<String> getPlayerNames() {
        List<String> list = Lists.newArrayList();
        Iterator var2 = this.networkHandler.getPlayerList().iterator();

        while (var2.hasNext()) {
            PlayerListEntry playerListEntry = (PlayerListEntry) var2.next();
            String playerName = playerListEntry.getProfile().getName();
            if (!playerName.startsWith("!")) {
                list.add(playerName);
            }
        }

        return list;
    }

    public ClientCommandSourceMixin(ClientPlayNetworkHandler networkHandler) {
        this.networkHandler = networkHandler;
        this.client = MinecraftClient.getInstance();
    }
}
