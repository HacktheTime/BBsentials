package de.hype.bingonet.forge.CommandImplementations;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.shared.constants.MiningEvents;
import de.hype.bingonet.shared.packets.mining.MiningEventPacket;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import static de.hype.bingonet.client.common.client.BingoNet.connection;


public class Command2xPowder extends CommandBase {

    @Override
    public String getCommandName() {
        return "2xpowder";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/2xpowder";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        try {
            connection.sendPacket(new MiningEventPacket(MiningEvents.DOUBLE_POWDER,"", EnvironmentCore.utils.getCurrentIsland()));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
    }
    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
