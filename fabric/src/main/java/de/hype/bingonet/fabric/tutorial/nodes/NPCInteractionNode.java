package de.hype.bingonet.fabric.tutorial.nodes;

import de.hype.bingonet.fabric.tutorial.AbstractTutorialNode;
import net.minecraft.util.math.BlockPos;

public class NPCInteractionNode extends AbstractTutorialNode {
    public BlockPos position;
    public String name;
    public boolean use;

    public NPCInteractionNode(String name, BlockPos position, boolean use) {
        this.name = name;
        this.position = position;
        this.use=use;
    }

    @Override
    public void onPreviousCompleted() {

    }

    @Override
    public String getDescriptionString() {
        return "Talk to %s (%s)".formatted(name, position.toString());
    }
}
