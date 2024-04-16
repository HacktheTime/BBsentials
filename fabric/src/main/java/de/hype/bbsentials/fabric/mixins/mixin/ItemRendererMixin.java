package de.hype.bbsentials.fabric.mixins.mixin;

import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.item.ItemRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    @Shadow
    private ItemModels models;

    @Shadow
    @Final
    private ItemColors colors;

}
