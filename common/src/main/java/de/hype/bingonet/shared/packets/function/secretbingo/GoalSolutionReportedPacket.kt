package de.hype.bingonet.shared.packets.function.secretbingo;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;

public class GoalSolutionReportedPacket extends AbstractPacket {
    //TODO add description report from users.
    public final String goalName;
    public final String goalId;
    public final String solution;
    public final boolean verified;

    public GoalSolutionReportedPacket(String goalName, String goalId, String solution, boolean verified) {
        super(1, 1);
        this.goalName = goalName;
        this.goalId = goalId;
        this.solution = solution;
        this.verified = verified;
    }
}
