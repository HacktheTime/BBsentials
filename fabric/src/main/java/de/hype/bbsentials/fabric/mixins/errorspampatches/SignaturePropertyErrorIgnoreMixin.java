package de.hype.bbsentials.fabric.mixins.errorspampatches;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilServicesKeyInfo;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(YggdrasilServicesKeyInfo.class)
public class SignaturePropertyErrorIgnoreMixin {
    @Redirect(method = "validateProperty", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"),remap = false)
    public void BBsentials$validateProperty(Logger instance, String s, Object o, Object o1) {
        //We dont care about logs
    }

}
