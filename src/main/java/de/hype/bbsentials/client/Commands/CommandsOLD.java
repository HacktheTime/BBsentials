package de.hype.bbsentials.client.Commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.BBUtils;
import de.hype.bbsentials.client.BBsentials;
import de.hype.bbsentials.constants.enviromentShared.ChChestItems;
import de.hype.bbsentials.constants.enviromentShared.MiningEvents;
import de.hype.bbsentials.packets.AbstractPacket;
import de.hype.bbsentials.packets.packets.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.command.CommandSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.hype.bbsentials.client.BBsentials.*;

public class CommandsOLD {
    public CommandsOLD() {
        Event<ClientCommandRegistrationCallback> event = ClientCommandRegistrationCallback.EVENT;
//        event.register((dispatcher, registryAccess) -> {
//            dispatcher.register(ClientCommandManager.literal("warp").then(ClientCommandManager.argument("destination", StringArgumentType.string()).suggests((context, builder) -> {
//                // Provide tab-completion options for menu subfolder
//                return CommandSource.suggestMatching(new String[]{"desert", "hub", "dhub", "nether", "isle", "wizard", "portal", "mines", "forge", "ch", "crystals", "nucleus", "end", "drag", "void", "castle", "howl", "park", "jungle", "nest", "arachne", "spider", "deep", "barn", "home", "kuurda", "wasteland", "dragontail", "scarleton", "smold", "garden", "da", "crypt", "museum", "trapper", "dungeon_hub"}, builder);
//            }).executes((context) -> {
//                // Handle "variableName" and "variableValue" logic here
//                String destination = StringArgumentType.getString(context, "destination");
//                getConfig().sender.addSendTask("/warp " + destination, 0);
//                return 1;
//            })));
//        }); //warp test
        event.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("creport")
                    .then(ClientCommandManager.argument("Player_Name", StringArgumentType.string())
                            .executes((context) -> {
                                String playerName = StringArgumentType.getString(context, "Player_Name");
                                getConfig().sender.addSendTask("/creport " + playerName, 0);
                                getConfig().addReported(playerName);
                                return 1;
                            })));
        });//creport helper → no double report during same launch
//        event.register((dispatcher, registryAccess) -> {
//            dispatcher.register(ClientCommandManager.literal("hp").then(ClientCommandManager.literal("accept").then(ClientCommandManager.argument("player", StringArgumentType.string()).executes((context) -> {
//                String player = StringArgumentType.getString(context, "player");
//                getConfig().sender.addImmediateSendTask("/party accept " + player);
//                return 1;
//            }))).then(ClientCommandManager.literal("chat").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party chat");
//                return 1;
//            })).then(ClientCommandManager.literal("demote").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
//                // Provide tab-completion options for menu subfolder
//                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
//            }).executes((context) -> {
//                String player = StringArgumentType.getString(context, "player");
//                getConfig().sender.addImmediateSendTask("/party demote " + player);
//                return 1;
//            }))).then(ClientCommandManager.literal("disband").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party disband");
//                return 1;
//            })).then(ClientCommandManager.literal("kick").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
//                // Provide tab-completion options for menu subfolder
//                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
//            }).executes((context) -> {
//                String player = StringArgumentType.getString(context, "player");
//                getConfig().sender.addImmediateSendTask("/party kick " + player);
//                return 1;
//            }))).then(ClientCommandManager.literal("kickoffline").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party kickoffline");
//                return 1;
//            })).then(ClientCommandManager.literal("leave").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party leave");
//                return 1;
//            })).then(ClientCommandManager.literal("list").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party list");
//                return 1;
//            })).then(ClientCommandManager.literal("mute").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party mute");
//                return 1;
//            })).then(ClientCommandManager.literal("poll").then(ClientCommandManager.argument("question/answer/answer/answer", StringArgumentType.greedyString()).executes((context) -> {
//                String questionAndAnswers = StringArgumentType.getString(context, "question answer answer (answer)");
//                getConfig().sender.addImmediateSendTask("/party poll " + questionAndAnswers);
//                return 1;
//            }))).then(ClientCommandManager.literal("private").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party private");
//                return 1;
//            })).then(ClientCommandManager.literal("promote").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
//                // Provide tab-completion options for menu subfolder
//                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
//            }).executes((context) -> {
//                String player = StringArgumentType.getString(context, "player");
//                getConfig().sender.addImmediateSendTask("/party promote " + player);
//                return 1;
//            }))).then(ClientCommandManager.literal("setting").then(ClientCommandManager.literal("allinvite")).executes((context) -> {
//                        String setting = StringArgumentType.getString(context, "setting");
//                        getConfig().sender.addImmediateSendTask("/party setting " + setting);
//                        return 1;
//                    })
//
//            ).then(ClientCommandManager.literal("transfer").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
//                // Provide tab-completion options for menu subfolder
//                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
//            }).executes((context) -> {
//                String player = StringArgumentType.getString(context, "player");
//                getConfig().sender.addImmediateSendTask("/party transfer " + player);
//                return 1;
//            }))).then(ClientCommandManager.literal("warp").executes((context) -> {
//                getConfig().sender.addImmediateSendTask("/party warp");
//                return 1;
//            })).executes(context -> {
//                getConfig().sender.addImmediateSendTask("/p");
//                return 1;
//            }));
//        }); //party test
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
                                                                return CommandSource.suggestMatching(new String[]{"/msg " + getConfig().getUsername() + " bb:party me", "/p join " + config.getUsername()}, builder);
                                                            }))
                                                            .executes((context) -> {
                                                                        String item = StringArgumentType.getString(context, "Item");
                                                                        int x = IntegerArgumentType.getInteger(context, "X");
                                                                        int y = IntegerArgumentType.getInteger(context, "Y");
                                                                        int z = IntegerArgumentType.getInteger(context, "Z");
                                                                        String contactWay = StringArgumentType.getString(context, "ContactWay");

                                                                        bbserver.sendPacket(new ChChestPacket("", ChChestItems.getItem(item.split(";")), x + " " + y + " " + z, contactWay, ""));
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
                    ClientCommandManager.literal("bbserver")
                            .then(ClientCommandManager.argument("Message", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message");
                                        if (message.equals("bb:reconnect")) {
                                            BBsentials.connectToBBserver();
                                        }
                                        else {
                                            BBsentials.bbserver.sendMessage(message);
                                        }
                                        return 1;
                                    })
                            )
            );
        });/*BBserver*/
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
        if (getConfig().bbsentialsRoles != null) {
            if (getConfig().hasBBRoles("mod")) {
                event.register((dispatcher, registryAccess) -> {
                    dispatcher.register(
                            ClientCommandManager.literal("bannounce")
                                    .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                            .executes((context) -> {
                                                String message = StringArgumentType.getString(context, "message");
                                                sendCommand("?announce " + message);
                                                return 1;
                                            })
                                    )
                    );
                });/*bAnnounce*/
                event.register((dispatcher, registryAccess) -> {
                    dispatcher.register(
                            ClientCommandManager.literal("bmute")
                                    .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                            .executes((context) -> {
                                                String message = StringArgumentType.getString(context, "message");
                                                sendCommand("?mute " + message);
                                                return 1;
                                            })
                                    )
                    );
                });/*bmute*/
                event.register((dispatcher, registryAccess) -> {
                    dispatcher.register(
                            ClientCommandManager.literal("bban")
                                    .then(ClientCommandManager.argument("message", StringArgumentType.greedyString())
                                            .executes((context) -> {
                                                String message = StringArgumentType.getString(context, "message");
                                                sendCommand("?bban " + message);
                                                return 1;
                                            })
                                    )
                    );
                });/*bmute*/
            }
            if (getConfig().hasBBRoles("splasher")) {
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
                                                                String location = "bea";
                                                                boolean lessWaste = Boolean.parseBoolean(StringArgumentType.getString(context, "lasswaste"));
                                                                splashAnnounce(hub, location, "", lessWaste);
                                                                return 1;
                                                            })
                                                    ))
                                            .executes((context) -> {
                                                int hub = IntegerArgumentType.getInteger(context, "Hub");
                                                String location = StringArgumentType.getString(context, "location");
                                                splashAnnounce(hub, location, "", true);
                                                return 1;
                                            })

                                    )
                    );
                });/*SplashAnnounce*/
            }
            else {
            }
        }
    }

    private static void simpleCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String commandName, String[] parameters) {
        dispatcher.register(
                ClientCommandManager.literal(commandName)
                        .executes((context) -> {
                            BBsentials.bbserver.sendPacket(new InternalCommandPacket(commandName, parameters));
                            return 1;
                        })
        );
    }

    private static void miningEvent(CommandDispatcher<FabricClientCommandSource> dispatcher, String commandName, MiningEvents event) {
        dispatcher.register(
                ClientCommandManager.literal(commandName)
                        .executes((context) -> {
                            try {
                                BBsentials.bbserver.sendPacket(new MiningEventPacket(event,//TODO get the island
                                        config.getUsername(), Objects.requireNonNull(BBUtils.getCurrentIsland())));
                            } catch (Exception e) {
                                Chat.sendPrivateMessageToSelf("§c" + e.getMessage());
                            }
                            return 1;
                        })
        );
    }

    public void splashAnnounce(int hubNumber, String locationInHub, String extramessage, boolean lessWaste) {
        sendPacket(new SplashNotifyPacket(0, hubNumber, config.getUsername(), locationInHub, BBUtils.getCurrentIsland(), extramessage, lessWaste));
    }


    public void sendCommand(String message) {
        BBsentials.bbserver.sendCommand(message);
    }

    public <E extends AbstractPacket> void sendPacket(E packet) {
        BBsentials.bbserver.sendPacket(packet);
    }
}