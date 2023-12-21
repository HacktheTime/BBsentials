package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.SplashStatusUpdateListener;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.concurrent.TimeUnit;

public class CommandGetLeechers extends CommandBase {

    @Override
    public String getCommandName() {
        return "getLeechers";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/getLeechers";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        SplashStatusUpdateListener.showSplashOverlayOverrideDisplay = true;
        Chat.sendPrivateMessageToSelfDebug("Leechers: " + String.join(", ", EnvironmentCore.mcUtils.getSplashLeechingPlayers()));
        BBsentials.executionService.schedule(() -> SplashStatusUpdateListener.showSplashOverlayOverrideDisplay = false, 2, TimeUnit.MINUTES);
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
