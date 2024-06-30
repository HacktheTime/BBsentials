package de.hype.bbsentials.fabric.tutorial;

import net.minecraft.util.Identifier;

public abstract class AbstractTutorialNode {
    public boolean completed = false;
    public boolean persistent = false;
    public boolean canBeSkipped = true;
    public String description;

    public abstract void onPreviousCompleted();

    public String getDescriptionString() {
        return "";
    }
}
