package de.hype.bbsentials.client.common.client.updatelisteners;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.objects.ServerSwitchTask;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.SplashData;
import de.hype.bbsentials.shared.packets.function.SplashUpdatePacket;

import java.util.concurrent.TimeUnit;

public class SplashStatusUpdateListener extends UpdateListener {
    public boolean splashed = false;
    public boolean full = false;
    SplashData data;

    public SplashStatusUpdateListener(BBsentialConnection connection, SplashData data) {
        super(connection);
        this.data = data;
    }

    public void run() {
        BBsentials.onServerLeave.add(new ServerSwitchTask(() -> isInLobby.set(false)));
        int maxPlayerCount = EnvironmentCore.utils.getMaximumPlayerCount() - 5;
        isInLobby.set(true);
        while (isInLobby.get()) {
            if (!full && (EnvironmentCore.utils.getPlayerCount() >= maxPlayerCount)) {
                setStatus(StatusConstants.FULL);
                full = true;
            }
            try {
                Thread.sleep(250);
            } catch (InterruptedException ignored) {
            }
        }
        if (splashed) {
            setStatus(StatusConstants.DONEBAD);
        }
        else {
            setStatus(StatusConstants.DONEBAD);
        }
    }

    @Override
    public boolean allowOverlayOverall() {
        return BBsentials.config.useSplashLeecherOverlayHud;
    }

    public void setStatus(StatusConstants newStatus) {
        if (!data.status.equals(newStatus)) connection.sendPacket(new SplashUpdatePacket(data.splashId, newStatus));
        if (newStatus.equals(StatusConstants.SPLASHING)) {
            splashed = true;
            BBsentials.executionService.schedule(() -> {
                setStatus(StatusConstants.DONEBAD);
                isInLobby.set(false);
            }, 1, TimeUnit.MINUTES);
        }
        data.status = newStatus;
    }
}
