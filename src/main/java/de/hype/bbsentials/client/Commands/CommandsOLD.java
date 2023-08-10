package de.hype.bbsentials.client.Commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.hype.bbsentials.client.BBsentials;
import de.hype.bbsentials.communication.BBsentialConnection;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.command.CommandSource;

import static de.hype.bbsentials.client.BBsentials.*;

public class CommandsOLD {
    public CommandsOLD() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("warp").then(ClientCommandManager.argument("destination", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(new String[]{"desert", "hub", "dhub", "nether", "isle", "wizard", "portal", "mines", "forge", "ch", "crystals", "nucleus", "end", "drag", "void", "castle", "howl", "park", "jungle", "nest", "arachne", "spider", "deep", "barn", "home", "kuurda", "wasteland", "dragontail", "scarleton", "smold", "garden", "da", "crypt", "museum", "trapper", "dungeon_hub"}, builder);
            }).executes((context) -> {
                // Handle "variableName" and "variableValue" logic here
                String destination = StringArgumentType.getString(context, "destination");
                getConfig().sender.addSendTask("/warp " + destination, 0);
                return 1;
            })));
        }); //warp test
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("creport")
                    .then(ClientCommandManager.argument("Player_Name", StringArgumentType.string())
                            .executes((context) -> {
                                String playerName = StringArgumentType.getString(context, "Player_Name");
                                getConfig().sender.addSendTask("/creport " + playerName, 0);
                                getConfig().addReported(playerName);
                                return 1;
                            })));
        });//creport helper → no double report during same launch
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("hp").then(ClientCommandManager.literal("accept").then(ClientCommandManager.argument("player", StringArgumentType.string()).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party accept " + player);
                return 1;
            }))).then(ClientCommandManager.literal("chat").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party chat");
                return 1;
            })).then(ClientCommandManager.literal("demote").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party demote " + player);
                return 1;
            }))).then(ClientCommandManager.literal("disband").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party disband");
                return 1;
            })).then(ClientCommandManager.literal("kick").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party kick " + player);
                return 1;
            }))).then(ClientCommandManager.literal("kickoffline").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party kickoffline");
                return 1;
            })).then(ClientCommandManager.literal("leave").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party leave");
                return 1;
            })).then(ClientCommandManager.literal("list").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party list");
                return 1;
            })).then(ClientCommandManager.literal("mute").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party mute");
                return 1;
            })).then(ClientCommandManager.literal("poll").then(ClientCommandManager.argument("question/answer/answer/answer", StringArgumentType.greedyString()).executes((context) -> {
                String questionAndAnswers = StringArgumentType.getString(context, "question answer answer (answer)");
                getConfig().sender.addImmediateSendTask("/party poll " + questionAndAnswers);
                return 1;
            }))).then(ClientCommandManager.literal("private").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party private");
                return 1;
            })).then(ClientCommandManager.literal("promote").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party promote " + player);
                return 1;
            }))).then(ClientCommandManager.literal("setting").then(ClientCommandManager.literal("allinvite")).executes((context) -> {
                        String setting = StringArgumentType.getString(context, "setting");
                        getConfig().sender.addImmediateSendTask("/party setting " + setting);
                        return 1;
                    })

            ).then(ClientCommandManager.literal("transfer").then(ClientCommandManager.argument("player", StringArgumentType.string()).suggests((context, builder) -> {
                // Provide tab-completion options for menu subfolder
                return CommandSource.suggestMatching(getConfig().getPlayersInParty(), builder);
            }).executes((context) -> {
                String player = StringArgumentType.getString(context, "player");
                getConfig().sender.addImmediateSendTask("/party transfer " + player);
                return 1;
            }))).then(ClientCommandManager.literal("warp").executes((context) -> {
                getConfig().sender.addImmediateSendTask("/party warp");
                return 1;
            })).executes(context -> {
                getConfig().sender.addImmediateSendTask("/p");
                return 1;
            }));
        }); //party test
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("goblinraid")
                            .executes((context) -> {
                                bbserver.sendMessage("?dwevent goblinraid");
                                return 1;
                            })
            );
        });/*goblinraid*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("2xpowder")
                            .executes((context) -> {
                                bbserver.sendMessage("?dwevent 2xpowder");
                                return 1;
                            })
            );
        });/*2xpowder*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bettertogether")
                            .executes((context) -> {
                                bbserver.sendMessage("?dwevent bettertogether");
                                return 1;
                            })
            );
        });/*b2g*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("raffle")
                            .executes((context) -> {
                                bbserver.sendMessage("?dwevent raffle");
                                return 1;
                            })
            );
        });/*raffle*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("gonewiththewind")
                            .executes((context) -> {
                                bbserver.sendMessage("?dwevent gonewiththewind");
                                return 1;
                            })
            );
        });/*gonewiththewind*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("chchest")
                    .then(ClientCommandManager.argument("Item", StringArgumentType.string())
                            .suggests((context, builder) -> {
                                return CommandSource.suggestMatching(new String[]{"PrehistoricEgg", "Pickonimbus2000", "ElectronTransmitter", "FTX3070", "RobotronReflector", "ControlSwitch", "SyntheticHeart", "SuperliteMotor", "BlueGoblinEgg", "YellowGoblinEgg", "FlawlessAmberGemstone", "FlawlessJadeGemstone", "FlawlessSapphireGemstone", "FlawlessRubyGemstone", "FlawlessAmethystGemstone", "JungleHeart", "FlawlessTopazGemstone", "FlawlessJasperGemstone"}, builder);
                            })
                            .then(ClientCommandManager.argument("X", IntegerArgumentType.integer())
                                    .then(ClientCommandManager.argument("Y", IntegerArgumentType.integer())
                                            .then(ClientCommandManager.argument("Z", IntegerArgumentType.integer())
                                                    .then(ClientCommandManager.argument("ContactWay", StringArgumentType.greedyString())
                                                            .suggests(((context, builder) -> {
                                                                return CommandSource.suggestMatching(new String[]{"/msg " + getConfig().getUsername() + " bb:party me", "/p join " + config.getUsername()}, builder);
                                                            }))
                                                            .executes((context) -> {
                                                                        String destination = StringArgumentType.getString(context, "Item");
                                                                        int x = IntegerArgumentType.getInteger(context, "X");
                                                                        int y = IntegerArgumentType.getInteger(context, "Y");
                                                                        int z = IntegerArgumentType.getInteger(context, "Z");
                                                                        String contactWay = StringArgumentType.getString(context, "ContactWay");

                                                                        String combinedString = "?chchest " + destination + " " + x + " " + y + " " + z + " " + contactWay;
                                                                        bbserver.sendMessage(combinedString);
                                                                        return 1;
                                                                    }
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
        }); /*chchest*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bbserver")
                            .then(ClientCommandManager.argument("Message", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message");
                                        if (message.equals("bb:reconnect")) {
                                            BBsentials.connectToBBserver();
                                        } else {
                                            BBsentials.bbserver.sendMessage(message);
                                        }
                                        return 1;
                                    })
                            )
            );
        });/*BBserver*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bc")
                            .then(ClientCommandManager.argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                        sendCommand("?bingochat " + message);
                                        return 1;
                                    })
                            )
            );
        });/*BincoChatShort*/
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("bingochat")
                            .then(ClientCommandManager.argument("Message to Bingo Chat", StringArgumentType.greedyString())
                                    .executes((context) -> {
                                        String message = StringArgumentType.getString(context, "Message to Bingo Chat");
                                        sendCommand("?bingochat " + message);
                                        return 1;
                                    })
                            )
            );
        });/*BingoChatLong*/
        if (getConfig().bbsentialsRoles != null) {
            if (getConfig().bbsentialsRoles.contains("mod")) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
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
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
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
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
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
            if (getConfig().bbsentialsRoles.contains("splasher")) {
                ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
                    dispatcher.register(
                            ClientCommandManager.literal("splashAnnounce")
                                    .then(ClientCommandManager.argument("Hub", IntegerArgumentType.integer(1, 28))
                                            .then(ClientCommandManager.argument("location", StringArgumentType.string())
                                                    .suggests((context, builder) -> {
                                                        return CommandSource.suggestMatching(new String[]{"kat","bea","guild-house"}, builder);
                                                    })
                                                    .then(ClientCommandManager.argument("extramessage", StringArgumentType.greedyString())
                                                            .executes((context) -> {
                                                                int hub_Number = IntegerArgumentType.getInteger(context, "Hub");
                                                                String extramessage = StringArgumentType.getString(context, "extramessage");
                                                                String location = StringArgumentType.getString(context, "location");
                                                                sendCommand("?splash " + hub_Number + " " + location+" "+extramessage);
                                                                return 1;
                                                            })
                                                    )
                                                    .executes((context) -> {
                                                        int hub_Number = IntegerArgumentType.getInteger(context, "Hub");
                                                        String message = "";
                                                        String location = "bea";
                                                        sendCommand("?splash " + hub_Number + " "+location+" " + message);
                                                        return 1;
                                                    })
                                            )
                                            .executes((context) -> {
                                                int hub_Number = IntegerArgumentType.getInteger(context, "Hub");
                                                String message = "";
                                                String location = StringArgumentType.getString(context, "location");
                                                sendCommand("?splash " + hub_Number + " "+location+" " + message);
                                                return 1;
                                            })
                                    )
                    );
                });/*SplashAnnounce*/
            } else {
            }
        }
    }
    public void sendCommand(String message){
        BBsentials.bbserver.sendCommand(message);
    }
}