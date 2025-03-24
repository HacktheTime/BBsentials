package de.hype.bingonet.fabric.mixins.mixin;

import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.IChestBlockEntityMixinAccess;
import de.hype.bingonet.fabric.mixins.mixinaccessinterfaces.IChestLidMixinMixinAccess;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ChestLidAnimator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@SuppressWarnings("UnreachableCode")
@Mixin(ChestBlockEntity.class)
public class ChestMixin implements IChestBlockEntityMixinAccess {
    @Final
    @Shadow
    private ChestLidAnimator lidAnimator;


    @Unique
    public ChestLidAnimator BingoNet$getLidAnimator() {
        return lidAnimator;
    }

    @Unique
    public boolean BingoNet$isOpen() {
        return (((IChestLidMixinMixinAccess) (Object) lidAnimator)).BingoNet$isOpen();
    }
}
