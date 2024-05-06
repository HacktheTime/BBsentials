package de.hype.bbsentials.fabric.mixins.mixinaccessinterfaces;

import org.spongepowered.asm.mixin.Unique;

public interface IChestLidMixinMixinAccess {
    @Unique
    boolean BBsentials$isOpen();
}
