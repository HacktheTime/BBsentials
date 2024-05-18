package de.hype.bbsentials.fabric.mixins.mixin;

// Credits for this code goes to Nea89o Firmanent. 2024 under MIT License

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import de.hype.bbsentials.fabric.ModInitialiser;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.LightData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class IncomingPacketListenerPatches {


    @Shadow
    protected abstract void readLightData(int x, int z, LightData data);

    @ModifyExpressionValue(method = "onCommandTree", at = @At(value = "NEW", target = "(Lcom/mojang/brigadier/tree/RootCommandNode;)Lcom/mojang/brigadier/CommandDispatcher;", remap = false))
    public CommandDispatcher<CommandSource> onOnCommandTree(CommandDispatcher<CommandSource> dispatcher) {
        CommandDispatcher<FabricClientCommandSource> clientDispatcher = ModInitialiser.dispatcher;
        if (clientDispatcher == null) return dispatcher;
        for (CommandNode<FabricClientCommandSource> child : clientDispatcher.getRoot().getChildren()) {
            dispatcher.getRoot().getChildren().removeIf(it -> it.getName().equalsIgnoreCase(child.getName()));
        }
        return dispatcher;
    }

//    @Inject(method = "onParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
//    public void onParticleSpawn(ParticleS2CPacket packet, CallbackInfo ci) {
//
//    }
}
