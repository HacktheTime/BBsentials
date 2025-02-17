package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.packets.function.PositionCommunityFeedback;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ComGoalPositionExtendedData {
    public final Instant fromTime;
    public final String mcuuid;
    public final PositionCommunityFeedback.ComGoalPosition position;

    public ComGoalPositionExtendedData(PositionCommunityFeedback.ComGoalPosition data, String mcuuid, Instant time) {
        this.mcuuid = mcuuid;
        position = data;
        fromTime = time;
    }

    public Instant getReferenceTime() {
        int punPos = (100 - position.position);
        return fromTime.plus((long) punPos * punPos, ChronoUnit.MINUTES);
    }

    public boolean dataEquals(ComGoalPositionExtendedData that) {
        return mcuuid.equals(that.mcuuid) && position.dataEquals(that.position);
    }
}
