package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

import java.util.List;

public class PositionCommunityFeedback extends AbstractPacket {
    public List<ComGoalPosition> positions;

    public PositionCommunityFeedback(List<ComGoalPosition> positions) {
        super(1, 1);
        this.positions = positions;
    }

    public static class ComGoalPosition {
        public String goalName;
        public Integer contribution;
        public Double topPercentage;
        public Integer position;

        public ComGoalPosition(String goalName, Integer contribution, Double topPercentage, Integer position) {
            this.goalName = goalName;
            this.contribution = contribution;
            this.topPercentage = topPercentage;
            this.position = position;
        }
    }
}
