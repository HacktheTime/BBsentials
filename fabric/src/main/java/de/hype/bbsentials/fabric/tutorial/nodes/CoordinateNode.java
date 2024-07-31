package de.hype.bbsentials.fabric.tutorial.nodes;

import de.hype.bbsentials.fabric.ModInitialiser;
import de.hype.bbsentials.fabric.tutorial.AbstractTutorialNode;
import de.hype.bbsentials.shared.constants.Islands;
import de.hype.bbsentials.shared.objects.Position;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.List;

public class CoordinateNode extends AbstractTutorialNode {
    Position position;
    Islands island;

    public CoordinateNode(Position position, Islands island) {
        this.position = position;
        this.island = island;
    }

    public CoordinateNode(BlockPos position, Islands island) {
        this.position = new Position(position.getX(), position.getY(), position.getZ());
        this.island = island;
    }

    public BlockPos getPositionBlockPos() {
        return new BlockPos(new Vec3i(position.x, position.y, position.z));
    }

    public Position getPosition() {
        return position;
    }

    public Islands getIsland() {
        return island;
    }

    @Override
    public void onPreviousCompleted() {

    }

    @Override
    public String getDescriptionString() {
        List<CoordinateNode> nodes = ModInitialiser.tutorialManager.current.getCoordinateNodesToRender();
        if (nodes.isEmpty()) return "Go to %s".formatted(position.toString());
        else return "Go to %s (%s)".formatted(nodes.getLast().position.toString(), position.toString());
    }
}
