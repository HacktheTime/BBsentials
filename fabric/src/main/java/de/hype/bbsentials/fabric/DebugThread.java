package de.hype.bbsentials.fabric;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.client.common.client.DebugThread {
    public static List<Object> store = new ArrayList<>();
    boolean doTest = false;

    @Override
    public void loop() {
        if (doTest) {
            doTest = false;
            test();
        }
    }

    public void onNumpadCode() {

    }

    public void doOnce() {
        doTest = true;
    }

    public void unlockCursor(){
        MinecraftClient.getInstance().mouse.unlockCursor();
    }
    @Override
    public List<String> test() {
        return List.of("");
    }

    public void setScreen(Screen screen) {
        if (screen == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(screen));
    }

}
