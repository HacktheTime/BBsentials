package de.hype.bingonet.fabric.mixins.mixinaccessinterfaces;

import org.spongepowered.asm.mixin.Unique;

public interface IChestLidMixinMixinAccess {
    @Unique
    boolean BingoNet$isOpen();
}
