package de.hype.bingonet.client.common.client;

import com.google.gson.annotations.Expose;
import de.hype.bingonet.shared.packets.function.PositionCommunityFeedback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * This Class will look bad and is used to store all those variables needed but would make the actually important classes less readable.
 */
public class DummyDataStorage {
    @Expose(serialize = false, deserialize = false)
    public static volatile AtomicReference<PositionCommunityFeedback> comGoalDataPacketInstance = new AtomicReference<>();
    @Expose(serialize = false, deserialize = false)
    public static volatile AtomicReference<ScheduledFuture<?>> comGoalDataPacketSendFuture = new AtomicReference<>();

    public static synchronized void addComGoalDataToPacket(PositionCommunityFeedback.ComGoalPosition positioning) {
        if (positioning.position == null && comGoalDataPacketInstance == null) return;
        if (comGoalDataPacketSendFuture.get() != null) comGoalDataPacketSendFuture.get().cancel(false);
        comGoalDataPacketSendFuture.set(BingoNet.executionService.schedule(() -> {
            if (BingoNet.connection != null && BingoNet.connection.isConnected())
                BingoNet.connection.sendPacket(comGoalDataPacketInstance.get());
            comGoalDataPacketInstance.set(null);
            comGoalDataPacketSendFuture.set(null);
        }, 1, TimeUnit.SECONDS));
        if (comGoalDataPacketInstance.get() == null)
            comGoalDataPacketInstance.set(new PositionCommunityFeedback(new HashSet<>()));
        comGoalDataPacketInstance.get().positions.add(positioning);
    }
}
