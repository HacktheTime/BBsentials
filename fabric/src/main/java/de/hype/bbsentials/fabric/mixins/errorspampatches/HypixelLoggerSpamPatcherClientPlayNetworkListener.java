package de.hype.bbsentials.fabric.mixins.errorspampatches;

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

    @Shadow
    protected abstract ParseResults<CommandSource> parse(String command);

    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V"), remap = false)
    public void BBsentials$passengerEntityUnknownPatch(Logger instance, String s) {
        //Do exactly nothing
    }

    @Redirect(method = "onEntityTrackerUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;writeUpdatedEntries(Ljava/util/List;)V"), remap = false)
    public void BBsentials$EntityUpdateCrashPatcher(DataTracker instance, List<DataTracker.SerializedEntry<?>> entries) {
        try {
            instance.writeUpdatedEntries(entries);
        } catch (Exception e) {
            System.out.println("Normally this would probably caused a Crash or force disconnect. BBsentials caught that error");
            e.printStackTrace();
        }
    }
}
