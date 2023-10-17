package de.hype.bbsentials.fabric;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.io.File;

public class MCUtils implements de.hype.bbsentials.common.mclibraries.MCUtils {
    public boolean isWindowFocused() {
        return MinecraftClient.getInstance().isWindowFocused();
    }

    public File getConfigPath() {
        return FabricLoader.getInstance().getConfigDir().toFile();
    }

    public String getUsername() {
        return MinecraftClient.getInstance().player.getName().getString();
    }

    public String getMCUUID() {
        return MinecraftClient.getInstance().player.getUuid().toString();
    }


    public void playsound(String eventName) {
        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance
                .master(SoundEvent.of(new Identifier(eventName)), 1.0F, 1.0F));
    }

    public int getPotTime() {
        int remainingDuration = 0;
        StatusEffectInstance potTimeRequest = MinecraftClient.getInstance().player.getStatusEffect(StatusEffects.STRENGTH);
        if (potTimeRequest != null) {
            if (potTimeRequest.getAmplifier() >= 7) {
                remainingDuration = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingDuration;
    }

    public String mojangAuth(String serverId) {
        boolean success = false;
        int tries = 10;
        while (tries > 0 && !success) {
            tries--;
            try {
                MinecraftClient.getInstance().getSessionService().joinServer(MinecraftClient.getInstance().getGameProfile().getId(), MinecraftClient.getInstance().getSession().getAccessToken(), serverId);
                success = true;
            } catch (AuthenticationException ignored) {

            }
        }
        return serverId;
    }
}
