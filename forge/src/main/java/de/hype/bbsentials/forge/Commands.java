package de.hype.bbsentials.forge;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.mclibraries.MCCommand;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.forge.CommandImplementations.*;
import de.hype.bbsentials.shared.constants.StatusConstants;
import de.hype.bbsentials.shared.objects.SplashData;
import de.hype.bbsentials.shared.packets.function.SplashNotifyPacket;
import net.minecraftforge.client.ClientCommandHandler;

public class Commands implements MCCommand {
    public static <T extends AbstractPacket> void sendPacket(T packet) {
        BBsentials.connection.sendPacket(packet);
    }

    public void registerMain() {
        ClientCommandHandler.instance.registerCommand(new CommandBBI());
        ClientCommandHandler.instance.registerCommand(new CommandGoblinRaid());
        ClientCommandHandler.instance.registerCommand(new Command2xPowder());
        ClientCommandHandler.instance.registerCommand(new CommandBetterTogether());
        ClientCommandHandler.instance.registerCommand(new CommandRaffle());
        ClientCommandHandler.instance.registerCommand(new CommandGoneWithTheWind());
        ClientCommandHandler.instance.registerCommand(new CommandChChest());
        ClientCommandHandler.instance.registerCommand(new CommandBC());
        ClientCommandHandler.instance.registerCommand(new CommandBingoChat());
        ClientCommandHandler.instance.registerCommand(new CommandOpenConfig());
    }

    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher, boolean hasBeta, boolean hasMiningEvents, boolean hasChChest) {
        if (hasMod) {
            ClientCommandHandler.instance.registerCommand(new CommandBAnnounce());
            ClientCommandHandler.instance.registerCommand(new CommandBMute());
            ClientCommandHandler.instance.registerCommand(new CommandBBan());
        }
        if (hasSplasher) {
            ClientCommandHandler.instance.registerCommand(new CommandSplashAnnounce());
            ClientCommandHandler.instance.registerCommand(new CommandGetLeechers());
        }
    }

    public void splashAnnounce(int hubNumber, String locationInHub, String extramessage, boolean lessWaste) {
        try {
            sendPacket(new SplashNotifyPacket(new SplashData(BBsentials.config.getUsername(), hubNumber, locationInHub, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste) {
                @Override
                public void statusUpdate(StatusConstants newStatus) {
                    //ignored
                }
            }));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
    }
}
