package de.hype.bbsentials.forge;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.Display;

import java.io.File;
import java.math.BigInteger;
import java.util.Random;

public class MCUtils implements de.hype.bbsentials.common.mclibraries.MCUtils {
    public boolean isWindowFocused() {
        return Display.isActive();
    }

    public File getConfigPath() {
        return new File(Minecraft.getMinecraft().mcDataDir, "config");
    }

    public String getUsername() {
        return Minecraft.getMinecraft().thePlayer.getName();
    }

    public String getMCUUID() {
        return Minecraft.getMinecraft().thePlayer.getUniqueID().toString();
    }


    public void playsound(String eventName) {
        Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation(eventName), 1.0F, 1.0F, 0.0F));
    }

    public int getPotTime() {
        int remainingDuration = 0;
        PotionEffect potTimeRequest = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.damageBoost);
        if (potTimeRequest != null) {
            if (potTimeRequest.getAmplifier() >= 7) {
                remainingDuration = (int) (potTimeRequest.getDuration() / 20.0);
            }
        }
        return remainingDuration;
    }


    public String mojangAuth(String serverId) {
        try {
            Minecraft.getMinecraft().getSessionService().joinServer(Minecraft
                    .getMinecraft()
                    .getSession()
                    .getProfile(), Minecraft.getMinecraft().getSession().getToken(), serverId);
        } catch (AuthenticationException e) {
            return null;
        }
        return serverId;
    }

}
