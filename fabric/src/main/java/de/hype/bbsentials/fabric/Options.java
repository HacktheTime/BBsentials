package de.hype.bbsentials.fabric;

import de.hype.bbsentials.common.api.ISimpleOption;
import net.minecraft.client.MinecraftClient;

public class Options{
    public static void setFov(int value) {
        ((ISimpleOption) (Object) MinecraftClient.getInstance().options.getFov()).set(value);
    }
    public static void setGamma(double value) {
        ((ISimpleOption) (Object) MinecraftClient.getInstance().options.getGamma()).set(value);
    }
}

