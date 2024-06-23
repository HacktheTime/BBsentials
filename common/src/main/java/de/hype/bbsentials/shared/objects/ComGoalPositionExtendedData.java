package de.hype.bbsentials.shared.objects;

import de.hype.bbsentials.shared.packets.function.PositionCommunityFeedback;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ComGoalPositionExtendedData {
    public final Instant fromTime = Instant.now();
    public final String mcuuid;
    public final PositionCommunityFeedback.ComGoalPosition position;

    public ComGoalPositionExtendedData(PositionCommunityFeedback.ComGoalPosition data, String mcuuid) {
        this.mcuuid = mcuuid;
        position = data;
    }

    public Instant getReferenceTime() {
        int punPos = (100 - position.position);
        return fromTime.plus((long) punPos * punPos, ChronoUnit.MINUTES);
    }
}
