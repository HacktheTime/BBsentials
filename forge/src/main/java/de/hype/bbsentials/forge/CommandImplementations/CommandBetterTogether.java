package de.hype.bingonet.forge.CommandImplementations;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.shared.constants.MiningEvents;
import de.hype.bingonet.shared.packets.mining.MiningEventPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bingonet.client.common.client.BingoNet.connection;


public class CommandBetterTogether extends CommandBase {

    @Override
    public String getCommandName() {
        return "bettertogether";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bettertogether";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            connection.sendPacket(new MiningEventPacket(MiningEvents.BETTER_TOGETHER,"", EnvironmentCore.utils.getCurrentIsland()));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
