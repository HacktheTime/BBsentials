package de.hype.bbsentials.fabric;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.client.SplashStatusUpdateListener;
import de.hype.bbsentials.common.constants.enviromentShared.ChChestItems;
import de.hype.bbsentials.common.constants.enviromentShared.MiningEvents;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.common.mclibraries.MCCommand;
import de.hype.bbsentials.common.packets.AbstractPacket;
import de.hype.bbsentials.common.packets.packets.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Commands implements MCCommand {
    Event<ClientCommandRegistrationCallback> event = ClientCommandRegistrationCallback.EVENT;

    private static void simpleCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String commandName, String[] parameters) {
        dispatcher.register(
                ClientCommandManager.literal(commandName)
                        .executes((context) -> {
                            sendPacket(new InternalCommandPacket(commandName, parameters));
                            return 1;
                        })
        );
    }

    private static void miningEvent(CommandDispatcher<FabricClientCommandSource> dispatcher, String commandName, MiningEvents event) {
        dispatcher.register(
                ClientCommandManager.literal(commandName)
                        .executes((context) -> {
                            try {
                                sendPacket(new MiningEventPacket(event,
                                        BBsentials.config.getUsername(), Objects.requireNonNull(EnvironmentCore.utils.getCurrentIsland())));
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelfError(e.getMessage());
                            }
                            return 1;
                        })
        );
    }

    public static <T extends AbstractPacket> void sendPacket(T packet) {
        BBsentials.connection.sendPacket(packet);
    }

    public void registerMain() {
        event.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("creport")
                    .then(ClientCommandManager.argument("Player_Name", StringArgumentType.string())
                            .executes((context) -> {
                                String playerName = StringArgumentType.getString(context, "Player_Name");
                                BBsentials.getConfig().sender.addSendTask("/creport " + playerName, 0);
                                BBsentials.getConfig().addReported(playerName);
                                return 1;
                            })));
        });//creport helper → no double report during same launch
        event.register((dispatcher, registryAccess) -> {
            miningEvent(dispatcher, "goblinraid", MiningEvents.GOBLIN_RAID);
        });/*goblinraid*/
        event.register((dispatcher, registryAccess) -> {
            miningEvent(dispatcher, "2xpowder", MiningEvents.DOUBLE_POWDER);
        });/*2xpowder*/
        event.register((dispatcher, registryAccess) -> {
            miningEvent(dispatcher, "bettertogether", MiningEvents.BETTER_TOGETHER);
        });/*b2g*/
        event.register((dispatcher, registryAccess) -> {
            miningEvent(dispatcher, "raffle", MiningEvents.RAFFLE);
        });/*raffle*/
        event.register((dispatcher, registryAccess) -> {
            miningEvent(dispatcher, "gonewiththewind", MiningEvents.GONE_WITH_THE_WIND);
        });/*gonewiththewind*/
        event.register((dispatcher, registryAccess) -> {
            miningEvent(dispatcher, "mithrilgourmand", MiningEvents.MITHRIL_GOURMAND);
        });/*gonewiththewind*/
        event.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("chchest")
                    .then(ClientCommandManager.argument("Item", StringArgumentType.string())
                            .suggests((context, builder) -> {
                                String[] items = new String[]{"PrehistoricEgg", "Pickonimbus2000", "ElectronTransmitter", "FTX3070", "RobotronReflector", "ControlSwitch", "SyntheticHeart", "SuperliteMotor", "BlueGoblinEgg", "YellowGoblinEgg", "FlawlessAmberGemstone", "FlawlessJadeGemstone", "FlawlessSapphireGemstone", "FlawlessRubyGemstone", "FlawlessAmethystGemstone", "JungleHeart", "FlawlessTopazGemstone", "FlawlessJasperGemstone"};
                                String input = builder.getRemaining().toLowerCase();
                                int lastSemicolonIndex = input.lastIndexOf(";");
                                List<String> suggestions = new ArrayList<>();
                                if (lastSemicolonIndex >= 0) {
                                    String inputBeforeSemicolon = input.substring(0, lastSemicolonIndex + 1); // Include the semicolon

                                    for (String item : items) {
                                        suggestions.add(inputBeforeSemicolon + item);
                                    }
                                }
                                return CommandSource.suggestMatching(suggestions, builder);
                            })
                            .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                                    .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                                            .then(ClientCommandManager.argument("Z", IntegerArgumentType.integer())
                                                    .then(ClientCommandManager.argument("ContactWay", StringArgumentType.string())
                                                            .suggests(((context, builder) -> {
                                                                return CommandSource.suggestMatching(new String[]{"\"/msg " + BBsentials.getConfig().getUsername() + " bb:party me\"", "\"/p join " + BBsentials.config.getUsername() + "\""}, builder);
                                                            }))
                                                            .executes((context) -> {
                                                                        String item = StringArgumentType.getString(context, "Item");
                                                                        int x = IntegerArgumentType.getInteger(context, "X");
                                                                        int y = IntegerArgumentType.getInteger(context, "Y");
                                                                        int z = IntegerArgumentType.getInteger(context, "Z");
                                                                        String contactWay = StringArgumentType.getString(context, "ContactWay");

                                                                sendPacket(new ChChestPacket(0, "", ChChestItems.getItem(item.split(";")), x + " " + y + " " + z, contactWay, ""));
                                                                        return 1;
                                                                    }
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
        });/*chchest*/
        event.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bc")
                            .then(ClientCommandManager.argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                        sendPacket(new BingoChatMessagePacket("", "", message, 0));
                                        return 1;
                                    })
                            )
            );
        });/*BincoChatShort*/
        event.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bingochat")
                            .then(ClientCommandManager.argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                        sendPacket(new BingoChatMessagePacket("", "", message, 0));
                                        return 1;
                                    })
                            )
            );
        });/*BingoChatLong*/
    }

    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher, boolean hasBeta, boolean hasMiningEvents, boolean hasChChest) {
        if (hasMod) {
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(
                        ClientCommandManager.literal("bannounce")
                                .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                        .executes((context) -> {
                                            String message = StringArgumentType.getString(context, "message");
                                            sendPacket(new BroadcastMessagePacket("", "", message));
                                            return 1;
                                        })
                                )
                );
            });/*bAnnounce*/
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(ClientCommandManager.literal("bmute")
                        .then(ClientCommandManager.argument("userId/mcusername", StringArgumentType.string())
                                .then(ClientCommandManager.argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("reason", StringArgumentType.greedyString())
                                                .executes((context) -> {
                                                    String identification = StringArgumentType.getString(context, "userId/mcusername");
                                                    String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
                                                    String reason = StringArgumentType.getString(context, "reason");
                                                    int userId = -1;
                                                    String mcusername = "";
                                                    if (identification.replaceAll("[\\d]", "").trim().isEmpty()) {
                                                        userId = Integer.parseInt(identification);
                                                    }
                                                    else {
                                                        mcusername = identification;
                                                    }
                                                    sendPacket(new PunishUserPacket(PunishUserPacket.PUNISHMENT_TYPE_MUTE, userId, mcusername, duration, reason));
                                                    return 1;
                                                })
                                        )
                                )
                        )
                );
            });/*bmute*/
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(ClientCommandManager.literal("bban")
                        .then(ClientCommandManager.argument("userId/mcusername", StringArgumentType.string())
                                .then(ClientCommandManager.argument("[Duration(d/h/m/s) | 0 forever]", StringArgumentType.string())
                                        .then(ClientCommandManager.argument("reason", StringArgumentType.greedyString())
                                                .executes((context) -> {
                                                    String identification = StringArgumentType.getString(context, "userId/mcusername");
                                                    String duration = StringArgumentType.getString(context, "[Duration(d/h/m/s) | 0 forever]");
                                                    String reason = StringArgumentType.getString(context, "reason");
                                                    int userId = -1;
                                                    String mcusername = "";
                                                    if (identification.replaceAll("[\\d]", "").trim().isEmpty()) {
                                                        userId = Integer.parseInt(identification);
                                                    }
                                                    else {
                                                        mcusername = identification;
                                                    }
                                                    sendPacket(new PunishUserPacket(PunishUserPacket.PUNISHMENT_TYPE_BAN, userId, mcusername, duration, reason));
                                                    return 1;
                                                })
                                        )
                                )
                        )
                );
            });/*ban*/
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(ClientCommandManager.literal("bgetinfo")
                        .then(ClientCommandManager.argument("userId/mcusername", StringArgumentType.string())
                                .executes((context) -> {
                                    String identification = StringArgumentType.getString(context, "userId/mcusername");
                                    sendPacket(new InternalCommandPacket(InternalCommandPacket.GET_USER_INFO, new String[]{identification}));
                                    return 1;
                                })
                        )
                );
            });/*getInfo*/
        }
        if (hasSplasher) {
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(
                        ClientCommandManager.literal("splashAnnounce")
                                .then(ClientCommandManager.argument("Hub", IntegerArgumentType.integer(1, 28))
                                        .then(ClientCommandManager.argument("location", StringArgumentType.string())
                                                .suggests((context, builder) -> {
                                                    return CommandSource.suggestMatching(new String[]{"kat", "bea", "guild-house"}, builder);
                                                })
                                                .then(ClientCommandManager.argument("lasswaste", StringArgumentType.string())
                                                        .suggests((context, builder) -> {
                                                            return CommandSource.suggestMatching(new String[]{"true", "false"}, builder);
                                                        })
                                                        .then(ClientCommandManager.argument("extramessage", StringArgumentType.greedyString())
                                                                .executes((context) -> {
                                                                    int hub = IntegerArgumentType.getInteger(context, "Hub");
                                                                    String extramessage = StringArgumentType.getString(context, "extramessage");
                                                                    String location = StringArgumentType.getString(context, "location");
                                                                    boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
                                                                    splashAnnounce(hub, location, extramessage, lessWaste);
                                                                    return 1;
                                                                })
                                                        )
                                                        .executes((context) -> {
                                                            int hub = IntegerArgumentType.getInteger(context, "Hub");
                                                            String location = StringArgumentType.getString(context, "location");
                                                            boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
                                                            splashAnnounce(hub, location, "", lessWaste);
                                                            return 1;
                                                        })
                                                ))
                                        .executes((context) -> {
                                            int hub = IntegerArgumentType.getInteger(context, "Hub");
                                            String location = "bea";
                                            splashAnnounce(hub, location, "", true);
                                            return 1;
                                        })

                                )
                );
            });/*SplashAnnounce*/
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(
                        ClientCommandManager.literal("requestpottimes")
                                .executes((context) -> {
                                    sendPacket(new InternalCommandPacket(InternalCommandPacket.REQUEST_POT_DURATION, new String[0]));
                                    return 1;
                                })
                );
            });/*requestpottimes*/
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(
                        ClientCommandManager.literal("getLeecher")
                                .executes((context) -> {
                                    BBsentials.executionService.execute(() -> {
                                        SplashStatusUpdateListener.showSplashOverlayOverrideDisplay = true;
                                        Chat.sendPrivateMessageToSelfInfo("Leeching Players: " + String.join(", ", EnvironmentCore.mcUtils.getSplashLeechingPlayers()));
                                        BBsentials.executionService.schedule(() -> SplashStatusUpdateListener.showSplashOverlayOverrideDisplay = false,
                                                2, TimeUnit.MINUTES);
                                    });
                                    return 1;
                                })
                );
            });/*getLeecher*/
        }
        if (hasAdmin) {
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(
                        ClientCommandManager.literal("bshutdown")
                                .then(ClientCommandManager.argument("Reason", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> {
                                            return CommandSource.suggestMatching(new String[]{"Emergency Shutdown", "System Shutdown", "Other"}, builder);
                                        })
                                        .executes((context) -> {
                                            String reason = StringArgumentType.getString(context, "Reason");
                                            sendPacket(new InternalCommandPacket(InternalCommandPacket.SHUTDOWN_SERVER, new String[]{reason}));
                                            return 1;
                                        })
                                )
                );
            });/*BBShutdown*/
            event.register((dispatcher, registryAccess) -> {
                dispatcher.register(
                        ClientCommandManager.literal("bsetmotd")
                                .then(ClientCommandManager.argument("Message", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> {
                                            return CommandSource.suggestMatching(new String[]{""}, builder);
                                        })
                                        .executes((context) -> {
                                            String message = StringArgumentType.getString(context, "Message").trim();
                                            sendPacket(new InternalCommandPacket(InternalCommandPacket.SET_MOTD, new String[]{message}));
                                            return 1;
                                        })
                                )
                );
            });/*BBServerMotd*/
        }
    }

    public void splashAnnounce(int hubNumber, String locationInHub, String extramessage, boolean lessWaste) {
        sendPacket(new SplashNotifyPacket(0, hubNumber, BBsentials.config.getUsername(), locationInHub, EnvironmentCore.utils.getCurrentIsland(), extramessage, lessWaste));
    }
}
