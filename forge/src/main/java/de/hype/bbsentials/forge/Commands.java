package de.hype.bbsentials.forge;

import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.common.mclibraries.MCCommand;
import de.hype.bbsentials.common.packets.AbstractPacket;
import de.hype.bbsentials.common.packets.packets.SplashNotifyPacket;
import de.hype.bbsentials.forge.CommandImplementations.*;
import de.hype.bbsentials.forge.client.CommandBBI;
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
    }

    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher, boolean hasBeta, boolean hasMiningEvents, boolean hasChChest) {
//        if (hasMod) {
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("bannounce").then(ClientCommandManager.argument("message", StringArgumentType.greedyString()).executes((context) -> {
//                    String message = StringArgumentType.getString(context, "message");
//                    sendPacket(new BroadcastMessagePacket("", "", message));
//                    return 1;
//                })));
//            });/*bAnnounce*/
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("bmute").then(ClientCommandManager.argument("userId/mcusername", StringArgumentType.string()).then(ClientCommandManager.argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string()).then(ClientCommandManager.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
//                    String identification = StringArgumentType.getString(context, "userId/mcusername");
//                    String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
//                    String reason = StringArgumentType.getString(context, "reason");
//                    int userId = -1;
//                    String mcusername = "";
//                    if (identification.replaceAll("[\\d]", "").trim().isEmpty()) {
//                        userId = Integer.parseInt(identification);
//                    }
//                    else {
//                        mcusername = identification;
//                    }
//                    sendPacket(new PunishUserPacket(PunishUserPacket.PUNISHMENT_TYPE_MUTE, userId, mcusername, duration, reason));
//                    return 1;
//                })))));
//            });/*bmute*/
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("bban").then(ClientCommandManager.argument("userId/mcusername", StringArgumentType.string()).then(ClientCommandManager.argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string()).then(ClientCommandManager.argument("reason", StringArgumentType.greedyString()).executes((context) -> {
//                    String identification = StringArgumentType.getString(context, "userId/mcusername");
//                    String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
//                    String reason = StringArgumentType.getString(context, "reason");
//                    int userId = -1;
//                    String mcusername = "";
//                    if (identification.replaceAll("[\\d]", "").trim().isEmpty()) {
//                        userId = Integer.parseInt(identification);
//                    }
//                    else {
//                        mcusername = identification;
//                    }
//                    sendPacket(new PunishUserPacket(PunishUserPacket.PUNISHMENT_TYPE_BAN, userId, mcusername, duration, reason));
//                    return 1;
//                })))));
//            });/*ban*/
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("bgetinfo").then(ClientCommandManager.argument("userId/mcusername", StringArgumentType.string()).executes((context) -> {
//                    String identification = StringArgumentType.getString(context, "userId/mcusername");
//                    sendPacket(new InternalCommandPacket(InternalCommandPacket.GET_USER_INFO, new String[]{identification}));
//                    return 1;
//                })));
//            });/*getInfo*/
//        }
//        if (hasSplasher) {
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("splashAnnounce").then(ClientCommandManager.argument("Hub", IntegerArgumentType.integer(1, 28)).then(ClientCommandManager.argument("location", StringArgumentType.string()).suggests((context, builder) -> {
//                            return CommandSource.suggestMatching(new String[]{"kat", "bea", "guild-house"}, builder);
//                        }).then(ClientCommandManager.argument("lasswaste", StringArgumentType.string()).suggests((context, builder) -> {
//                            return CommandSource.suggestMatching(new String[]{"true", "false"}, builder);
//                        }).then(ClientCommandManager.argument("extramessage", StringArgumentType.greedyString()).executes((context) -> {
//                            int hub = IntegerArgumentType.getInteger(context, "Hub");
//                            String extramessage = StringArgumentType.getString(context, "extramessage");
//                            String location = StringArgumentType.getString(context, "location");
//                            boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
//                            splashAnnounce(hub, location, extramessage, lessWaste);
//                            return 1;
//                        })).executes((context) -> {
//                            int hub = IntegerArgumentType.getInteger(context, "Hub");
//                            String location = StringArgumentType.getString(context, "location");
//                            boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
//                            splashAnnounce(hub, location, "", lessWaste);
//                            return 1;
//                        }))).executes((context) -> {
//                            int hub = IntegerArgumentType.getInteger(context, "Hub");
//                            String location = "bea";
//                            splashAnnounce(hub, location, "", true);
//                            return 1;
//                        })
//
//                ));
//            });/*SplashAnnounce*/
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("requestpottimes").executes((context) -> {
//                    sendPacket(new InternalCommandPacket(InternalCommandPacket.REQUEST_POT_DURATION, new String[0]));
//                    return 1;
//                }));
//            });/*requestpottimes*/
//        }
//        if (hasAdmin) {
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("bshutdown").then(ClientCommandManager.argument("Reason", StringArgumentType.greedyString()).suggests((context, builder) -> {
//                    return CommandSource.suggestMatching(new String[]{"Emergency Shutdown", "System Shutdown", "Other"}, builder);
//                }).executes((context) -> {
//                    String reason = StringArgumentType.getString(context, "Reason");
//                    sendPacket(new InternalCommandPacket(InternalCommandPacket.SHUTDOWN_SERVER, new String[]{reason}));
//                    return 1;
//                })));
//            });/*BBShutdown*/
//            event.register((dispatcher, registryAccess) -> {
//                dispatcher.register(ClientCommandManager.literal("bsetmotd").then(ClientCommandManager.argument("Message", StringArgumentType.greedyString()).suggests((context, builder) -> {
//                    return CommandSource.suggestMatching(new String[]{""}, builder);
//                }).executes((context) -> {
//                    String message = StringArgumentType.getString(context, "Message").trim();
//                    sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_MOTD, new String[]{message}));
//                    return 1;
//                })));
//            });/*BBServerMotd*/
//        }
        if (hasMod) {
            ClientCommandHandler.instance.registerCommand(new CommandBAnnounce());
            ClientCommandHandler.instance.registerCommand(new CommandBMute());
            ClientCommandHandler.instance.registerCommand(new CommandBBan());
        }
        if (hasSplasher) {
            ClientCommandHandler.instance.registerCommand(new CommandSplashAnnounce());
        }

    }

    public void splashAnnounce(int hubNumber, String locationInHub, String extramessage, boolean lessWaste) {
        sendPacket(new SplashNotifyPacket(0, hubNumber, BBsentials.config.getUsername(), locationInHub, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste));
    }
}
