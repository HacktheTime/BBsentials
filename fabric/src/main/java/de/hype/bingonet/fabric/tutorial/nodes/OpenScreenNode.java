package de.hype.bingonet.fabric.tutorial.nodes;

import de.hype.bingonet.fabric.tutorial.AbstractTutorialNode;
import net.minecraft.client.MinecraftClient;

public class OpenScreenNode extends AbstractTutorialNode {
    public String title;

    public OpenScreenNode(String name) {
        this.title = name;
        canBeSkipped = false;
    }

    @Override
    public void onPreviousCompleted() {
        if (MinecraftClient.getInstance().currentScreen == null) return;
        if (MinecraftClient.getInstance().currentScreen.getTitle().getString().equals(title)) {
            completed=true;
        }
    }

    @Override
    public String getDescriptionString() {
        return "Open %s".formatted(title);
    }
}
