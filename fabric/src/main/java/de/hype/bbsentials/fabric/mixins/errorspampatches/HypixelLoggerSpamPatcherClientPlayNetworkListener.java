package de.hype.bbsentials.fabric.mixins.errorspampatches;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class HypixelLoggerSpamPatcherClientPlayNetworkListener {
    @Inject(method = "onEntityPassengersSet",at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"), cancellable = true, remap = false)
    public void BBsentials$passengerEntityUnknownPatch(EntityPassengersSetS2CPacket packet, CallbackInfo ci){
        ci.cancel();
    }
}
