package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Unique;

public interface IChestBlockEntityMixinAccess {

    @Unique
    ChestLidAnimator BBsentials$getLidAnimator();

    @Unique
    boolean BBsentials$isOpen();
}
