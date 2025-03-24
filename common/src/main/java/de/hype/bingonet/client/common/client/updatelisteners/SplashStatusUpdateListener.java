package de.hype.bingonet.client.common.client.updatelisteners;

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.client.objects.ServerSwitchTask;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.shared.constants.StatusConstants;
import de.hype.bingonet.shared.objects.SplashData;
import de.hype.bingonet.shared.packets.function.SplashUpdatePacket;

import java.util.concurrent.TimeUnit;

public class SplashStatusUpdateListener extends UpdateListener {
    public boolean splashed = false;
    public boolean full = false;
    SplashData data;

    public SplashStatusUpdateListener(SplashData data) {
        this.data = data;
    }

    public void run() {
        ServerSwitchTask.onServerLeaveTask(() -> isInLobby.set(false));
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
        return BingoNet.splashConfig.useSplasherOverlay;
    }

    public void setStatus(StatusConstants newStatus) {
        if (!data.status.equals(newStatus)) getConnection().sendPacket(new SplashUpdatePacket(data.splashId, newStatus));
        if (newStatus.equals(StatusConstants.SPLASHING)) {
            splashed = true;
            BingoNet.executionService.schedule(() -> {
                setStatus(StatusConstants.DONEBAD);
                isInLobby.set(false);
            }, 1, TimeUnit.MINUTES);
        }
        data.status = newStatus;
    }
}
