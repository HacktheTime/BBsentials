package de.hype.bbsentials.fabric.mixins.errorspampatches;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.SignatureState;
import de.hype.bbsentials.client.common.client.BBsentials;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerSkinProvider.class)
public class PlayerTexturePropertyErrorSpamPlayerSkinProviderPatch {
//    @ModifyExpressionValue(method = "met", at = @At(value = "INVOKE", target = "signatureStatecallsuggestionhere")))
//    public SignatureState BBsentials$antiInvalidSignatureSpam(SignatureState original) {
//        if (BBsentials.generalConfig.noWarnSpam)return SignatureState.SIGNED;
//        else return original;
//    }
}
