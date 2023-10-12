package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.chat.Message;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;

import java.io.File;

public class MCUtils {
    public static boolean isWindowFocused() {
        return MinecraftClient.getInstance().isWindowFocused();
    }

    public static File getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public static String getUsername() {
        return MinecraftClient.getInstance().player.getName().getString();
    }

    public static String getMCUUID() {
        return MinecraftClient.getInstance().player.getUuid().toString();
    }

    public static void sendChatMessage(String message) {
        MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(message);
    }
    public static void playsound(SoundEvent event) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance
                .master(event, 1.0F, 1.0F));
    }
    public static void sendClientSideMessage(Message message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            client.player.sendMessage(Text.Serializer.fromJson(message.getJson()));
        }
    }

    public static int getPotTime() {
        int remainingDuration = 0;
        StatusEffectInstance potTimeRequest = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.STRENGTH);
        if (potTimeRequest != null) {
            if (potTimeRequest.getAmplifier() >= 7) {
                remainingDuration = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingDuration;
    }
}
