package de.hype.bbsentials.client.common.client;

import com.google.gson.annotations.Expose;
import de.hype.bbsentials.shared.packets.function.PositionCommunityFeedback;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Class will look bad and is used to store all those variables needed but would make the actually important classes less readable.
 */
public class DummyDataStorage {
    @Expose(serialize = false, deserialize = false)
    public static volatile AtomicReference<PositionCommunityFeedback> comGoalDataPacketInstance;
    @Expose(serialize = false, deserialize = false)
    public static volatile AtomicReference<ScheduledFuture<?>> comGoalDataPacketSendFuture;

    public static void addComGoalDataToPacket(PositionCommunityFeedback.ComGoalPosition positioning) {
        if (positioning.position == null && comGoalDataPacketInstance == null) return;
        else if (positioning.position == null && comGoalDataPacketInstance != null) {
            comGoalDataPacketSendFuture.get().cancel(false);
            comGoalDataPacketSendFuture.set(BBsentials.executionService.schedule(() -> {
                if (BBsentials.connection != null && BBsentials.connection.isConnected()) BBsentials.connection.sendPacket(comGoalDataPacketInstance.get());
                comGoalDataPacketInstance = null;
                comGoalDataPacketSendFuture = null;
            }, 50, TimeUnit.MILLISECONDS));
            return;
        }
        if (comGoalDataPacketInstance == null) {
            ArrayList<PositionCommunityFeedback.ComGoalPosition> positionings = new ArrayList<>();
            positionings.add(positioning);
            comGoalDataPacketInstance.set(new PositionCommunityFeedback(positionings));
        }
        else {
            comGoalDataPacketSendFuture.get().cancel(false);
            comGoalDataPacketInstance.get().positions.add(positioning);
            comGoalDataPacketSendFuture.set(BBsentials.executionService.schedule(() -> {
                if (BBsentials.connection != null && BBsentials.connection.isConnected()) BBsentials.connection.sendPacket(comGoalDataPacketInstance.get());
                comGoalDataPacketInstance = null;
                comGoalDataPacketSendFuture = null;
            }, 50, TimeUnit.MILLISECONDS));
        }
    }
}
