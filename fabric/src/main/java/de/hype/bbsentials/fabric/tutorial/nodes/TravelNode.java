package de.hype.bbsentials.fabric.tutorial.nodes;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.fabric.tutorial.AbstractTutorialNode;
import de.hype.bbsentials.shared.constants.Islands;

import java.time.Duration;
import java.util.Date;

public class TravelNode extends AbstractTutorialNode {
    public Islands island;
    public String warpArgument;

    public TravelNode(Islands island) {
        this.island = island;
        warpArgument = island.getWarpCommand();
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
            BBsentials.sender.addSendTask("/warp " + warpArgument);
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
