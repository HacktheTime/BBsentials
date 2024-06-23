package de.hype.bbsentials.fabric.mixins.mixin;

import de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces.IChestLidMixinMixinAccess;
import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("UnreachableCode")
@Mixin(ChestLidAnimator.class)
public class ChestLidMixin implements IChestLidMixinMixinAccess {
    @Shadow
    private boolean open;

    @Unique
    public boolean BBsentials$isOpen() {
        return open;
    }
}
