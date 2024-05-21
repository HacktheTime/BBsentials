package de.hype.bbsentials.fabric.mixins.mixin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(YggdrasilServicesKeyInfo.class)
public class SignaturePropertyErrorIgnoreMixin {
    @Inject(method = "validateProperty", at = @At("HEAD"), cancellable = true, remap = false)
    public void validateProperty(Property property, CallbackInfoReturnable<Boolean> cir) {
        if (property.signature() == null) return;
        if (property.signature().isEmpty()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
