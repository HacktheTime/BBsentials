package de.hype.bbsentials.forge.CommandImplementations;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.constants.MiningEvents;
import de.hype.bbsentials.shared.packets.mining.MiningEventPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bbsentials.client.common.client.BBsentials.connection;


public class CommandGoneWithTheWind extends CommandBase {

    @Override
    public String getCommandName() {
        return "gonewiththewind";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/gonewiththewind";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            connection.sendPacket(new MiningEventPacket(MiningEvents.GONE_WITH_THE_WIND,"", EnvironmentCore.utils.getCurrentIsland()));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
