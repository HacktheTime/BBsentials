package de.hype.bbsentials.fabric.mixins.mixin;

// Credits for this code goes to Nea89o Firmanent. 2024 under MIT License

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.CommandDispatcher;
import de.hype.bbsentials.fabric.command.BBCommandDispatcher;
import de.hype.bbsentials.fabric.command.ClientCommandRegistrationCallback;
import de.hype.bbsentials.fabric.command.CommandOverrideCallback;
import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IBBsentialsCommandSource;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.impl.command.client.ClientCommandInternals;
import net.minecraft.client.network.ClientCommandSource;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.s2c.play.LightData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class IncomingPacketListenerPatches {


    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;
    @Shadow
    @Final
    private ClientCommandSource commandSource;

    @Shadow
    protected abstract void readLightData(int x, int z, LightData data);

    @ModifyExpressionValue(method = "onCommandTree", at = @At(value = "NEW", target = "(Lcom/mojang/brigadier/tree/RootCommandNode;)Lcom/mojang/brigadier/CommandDispatcher;", remap = false))
    public CommandDispatcher<CommandSource> onOnCommandTree(CommandDispatcher<CommandSource> dispatcher) {
        List<String> allCommands = dispatcher.getRoot().getChildren().stream().map(c -> c.getName()).toList();
        BBCommandDispatcher bbdispatcher = new BBCommandDispatcher();
        ClientCommandRegistrationCallback.EVENT.invoker().register(bbdispatcher);
        bbdispatcher.addCommands(dispatcher, commandSource);
        CommandOverrideCallback.EVENT.invoker().register(new CommandOverrideCallback.ReplaceHelper((CommandDispatcher<FabricClientCommandSource>) (Object) dispatcher, allCommands));
        return dispatcher;
    }

//    @Inject(method = "onParticle", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/NetworkThreadUtils;forceMainThread(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/listener/PacketListener;Lnet/minecraft/util/thread/ThreadExecutor;)V", shift = At.Shift.AFTER), cancellable = true)
//    public void onParticleSpawn(ParticleS2CPacket packet, CallbackInfo ci) {
//
//    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (BBCommandDispatcher.executeCommand(command)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "sendChatCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfo info) {
        if (BBCommandDispatcher.executeCommand(command)) {
            info.cancel();
        }
    }
}
