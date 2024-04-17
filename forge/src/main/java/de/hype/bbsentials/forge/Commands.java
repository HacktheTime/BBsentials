package de.hype.bbsentials.forge;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.client.common.mclibraries.MCCommand;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.forge.CommandImplementations.*;
import de.hype.bbsentials.shared.objects.SplashData;
import de.hype.bbsentials.shared.objects.SplashLocation;
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
//            ClientCommandHandler.instance.registerCommand(new CommandBMute());
//            ClientCommandHandler.instance.registerCommand(new CommandBBan());
        }
        if (hasSplasher) {
            ClientCommandHandler.instance.registerCommand(new CommandSplashAnnounce());
            ClientCommandHandler.instance.registerCommand(new CommandGetLeechers());
        }
    }

    public void splashAnnounce(String serverid, Integer hubNumber, SplashLocation locationInHub, String extramessage, boolean lessWaste) {
        if (serverid == null) serverid = EnvironmentCore.utils.getServerId();
        if (serverid == null) {
            Chat.sendPrivateMessageToSelfError("Could not get the Server ID from Tablist.");
            return;
        }
        if (hubNumber == null) hubNumber = BBsentials.temporaryConfig.getHubNumberFromCache(serverid);
        if (hubNumber == null) {
            Chat.sendPrivateMessageToSelfError("Cache is either outdated or missing the current hub. Open the Hub Selector and try again.");
            return;
        }
        try {
            sendPacket(new SplashNotifyPacket(new SplashData(BBsentials.generalConfig.getUsername(), hubNumber, locationInHub, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste, serverid)));
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
    }
}
