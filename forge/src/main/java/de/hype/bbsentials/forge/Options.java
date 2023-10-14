package de.hype.bbsentials.forge;

import net.minecraft.client.Minecraft;

public class Options implements de.hype.bbsentials.common.mclibraries.Options {
    public void setFov(int value) {
        Minecraft.getMinecraft().gameSettings.fovSetting=value;
    }
    public void setGamma(double value) {
        Minecraft.getMinecraft().gameSettings.gammaSetting= (float) value;
    }
}

