package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Unique;

public interface IChestBlockEntityMixinAccess {

    @Unique
    ChestLidAnimator BingoNet$getLidAnimator();

    @Unique
    boolean BingoNet$isOpen();
}
