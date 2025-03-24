package de.hype.bingonet.fabric.mixins.errorspampatches;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.SignatureState;
import de.hype.bingonet.client.common.client.BingoNet;
import net.minecraft.client.texture.PlayerSkinProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerSkinProvider.class)
public class PlayerTexturePropertyErrorSpamPlayerSkinProviderPatch {
//    @ModifyExpressionValue(method = "met", at = @At(value = "INVOKE", target = "signatureStatecallsuggestionhere")))
//    public SignatureState BingoNet$antiInvalidSignatureSpam(SignatureState original) {
//        if (BingoNet.generalConfig.noWarnSpam)return SignatureState.SIGNED;
//        else return original;
//    }
}
