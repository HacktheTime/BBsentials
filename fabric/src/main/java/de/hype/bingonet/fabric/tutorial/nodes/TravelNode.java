package de.hype.bingonet.fabric.tutorial.nodes;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.fabric.tutorial.AbstractTutorialNode;
import de.hype.bingonet.shared.constants.Islands;

import java.time.Duration;
import java.util.Date;

public class TravelNode extends AbstractTutorialNode {
    public Islands island;
    public String warpArgument;

    public TravelNode(Islands island) {
        this.island = island;
        warpArgument = island.getWarpArgument();
    }


    public TravelNode(String warpArgument) {
        this.warpArgument = warpArgument;
    }

    @Override
    public void onPreviousCompleted() {
        if (island == EnvironmentCore.utils.getCurrentIsland()) {
            completed = true;
            return;
        }
        while ((Duration.between(Chat.lastNPCMessage.toInstant(), new Date().toInstant()).getSeconds() < 10)) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {

            }
        }
        if (warpArgument != null) {
            BingoNet.sender.addSendTask("/warp " + warpArgument);
            completed = true;
        }
        else Chat.sendPrivateMessageToSelfError("Please visit the " + island.getDisplayName() + "!");
    }

    @Override
    public String getDescriptionString() {
        if (island == null) return "/warp %s".formatted(warpArgument);
        return "Visit the §6%s".formatted(island.getDisplayName());
    }
}
