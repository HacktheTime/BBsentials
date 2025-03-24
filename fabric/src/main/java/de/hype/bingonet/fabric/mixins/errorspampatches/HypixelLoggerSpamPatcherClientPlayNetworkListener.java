package de.hype.bingonet.fabric.mixins.errorspampatches;

import com.mojang.brigadier.ParseResults;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.data.DataTracker;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class HypixelLoggerSpamPatcherClientPlayNetworkListener {

}
