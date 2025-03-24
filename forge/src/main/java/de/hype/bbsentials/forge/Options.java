package de.hype.bingonet.forge;

import net.minecraft.client.Minecraft;

public class Options implements de.hype.bingonet.client.common.mclibraries.Options {
    public void setFov(int value) {
        Minecraft.getMinecraft().gameSettings.fovSetting=value;
    }
    public void setGamma(double value) {
        Minecraft.getMinecraft().gameSettings.gammaSetting= (float) value;
    }
}

