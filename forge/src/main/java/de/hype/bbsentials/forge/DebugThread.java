package de.hype.bingonet.forge;

import net.minecraft.client.Minecraft;

import java.util.Collections;
import java.util.List;

public class DebugThread extends de.hype.bingonet.client.common.client.DebugThread {
    public void unlockCursor() {
        Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
    }

    @Override
    public void loop() {
        test();
    }

    @Override
    public List<String> test() {
        return Collections.singletonList("");
    }

    @Override
    public void onServerJoin() {

    }

    @Override
    public void onServerLeave() {

    }
}
