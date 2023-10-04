package de.hype.bbsentials.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import de.hype.bbsentials.api.Options;
import de.hype.bbsentials.chat.Chat;
import de.hype.bbsentials.client.Commands.CommandsOLD;
import de.hype.bbsentials.communication.BBsentialConnection;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.CommandSource;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.hype.bbsentials.chat.Chat.*;

public class BBsentials implements ClientModInitializer {
    public static Config config;
    public static BBsentialConnection connection;
    public static CommandsOLD coms;
    public static ScheduledExecutorService executionService = Executors.newScheduledThreadPool(1000);
    public static boolean splashLobby;
    private static Thread bbthread;
    private boolean initialised = false;
    public static SplashStatusUpdateListener splashStatusUpdateListener;

    public static Config getConfig() {
        return config;
    }

    public static void connectToBBserver() {
        connectToBBserver(config.connectToBeta);
    }

    /**
     * Checks if still connected to the Server.
     *
     * @return true if it connected; false if old connection is kept.
     */
    public static boolean conditionalReconnectToBBserver() {
        if (!connection.isConnected()) {
            Chat.sendPrivateMessageToSelfInfo("Reconnecting");
            connectToBBserver(config.connectToBeta);
            return true;
        }
        return false;
    }

    public static void connectToBBserver(boolean beta) {
        if (connection != null) {
            connection.sendHiddenMessage("exit");
        }
        if (bbthread != null) {
            if (bbthread.isAlive()) {
                bbthread.interrupt();
            }
        }
        bbthread = new Thread(() -> {
            connection = new BBsentialConnection();
            coms = new CommandsOLD();
            connection.setMessageReceivedCallback(message -> executionService.execute(() -> connection.onMessageReceived(message)));
            if (beta) {
                connection.connect(config.getBBServerURL(), 5011);
            }
            else {
                connection.connect(config.getBBServerURL(), 5000);
            }
            executionService.scheduleAtFixedRate(new DebugThread(), 0, 20, TimeUnit.SECONDS);
        });
        bbthread.start();
    }

    /**
     * Runs the mod initializer on the client environment.
     */

    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> {
            splashLobby = false;
            if (!initialised) {
                config = Config.load();
                if (config.doGammaOverride) Options.setGamma(10);
                Chat chat = new Chat();
                if (Config.isBingoTime() || config.overrideBingoTime()) {
                    connectToBBserver();
                }
                initialised = true;
            }
        });
    }

    {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("bbi")
                    .then(ClientCommandManager.literal("reconnect")
                            .executes((context) -> {
                                connectToBBserver();
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("reconnect-stable-server")
                            .executes((context) -> {
                                connectToBBserver(false);
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("reconnect-test-server")
                            .executes((context) -> {
                                connectToBBserver(true);
                                return 1;
                            }))
                    .then(ClientCommandManager.literal("config")
                            .then(ClientCommandManager.argument("category", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        // Provide tab-completion options for config subfolder
                                        return CommandSource.suggestMatching(new String[]{"save", "reset", "load"}, builder);
                                    }).executes((context) -> {
                                        String category = StringArgumentType.getString(context, "category");
                                        switch (category) {
                                            case "save":
                                                getConfig().save();
                                                sendPrivateMessageToSelfSuccess("Saved config successfully");
                                                break;
                                            case "load":
                                                BBsentials.config = Config.load();
                                                break;
                                            case "reset":
                                                // Reset logic here
                                                break;
                                        }
                                        return 1;
                                    }))
                            .then(ClientCommandManager.literal("set-value")
                                    .then(ClientCommandManager.argument("className", StringArgumentType.string())
                                            .suggests((context, builder) -> {
                                                // Provide tab-completion options for classes
                                                ArrayList<String> classNames = new ArrayList<>();
                                                classNames.add("Config");
                                                // Replace with your own logic to retrieve class names
                                                return CommandSource.suggestMatching(classNames, builder);
                                            })
                                            .then(ClientCommandManager.argument("variableName", StringArgumentType.string())
                                                    .suggests((context, builder) -> {
                                                        // Provide tab-completion options for variable names
                                                        List<String> variableNames;
                                                        variableNames = List.of(getVariableInfo("de.hype.bbsentials.client", "Config"));
                                                        return CommandSource.suggestMatching(variableNames, builder);
                                                    })
                                                    .then(ClientCommandManager.argument("variableValue", StringArgumentType.string())
                                                            .executes((context) -> {
                                                                // Handle "variableName" and "variableValue" logic here
                                                                String variableName = StringArgumentType.getString(context, "variableName");
                                                                String variableValue = StringArgumentType.getString(context, "variableValue");
                                                                try {
                                                                    if (!variableName.toLowerCase().contains("dev") || config.hasBBRoles("dev")) {
                                                                        setVariableValue(getConfig(), variableName, variableValue);
                                                                    }
                                                                    getConfig().save();
                                                                } catch (ClassNotFoundException |
                                                                         NoSuchFieldException |
                                                                         IllegalAccessException |
                                                                         InstantiationException |
                                                                         InvocationTargetException |
                                                                         NoSuchMethodException e) {
                                                                    Chat.sendPrivateMessageToSelfError("Invalid variable or value");
                                                                }
                                                                return 1;
                                                            })))))
                            .then(ClientCommandManager.literal("get-value")
                                    .then(ClientCommandManager.argument("className", StringArgumentType.string())
                                            .suggests((context, builder) -> {
                                                // Provide tab-completion options for classes
                                                ArrayList<String> classNames = new ArrayList<>();
                                                classNames.add("Config");
                                                // Replace with your own logic to retrieve class names
                                                return CommandSource.suggestMatching(classNames, builder);
                                            })
                                            .then(ClientCommandManager.argument("variableName", StringArgumentType.string())
                                                    .suggests((context, builder) -> {
                                                        // Provide tab-completion options for variable names
                                                        List<String> variableNames;
                                                        variableNames = List.of(getVariableInfo("de.hype.bbsentials.client", "Config"));
                                                        return CommandSource.suggestMatching(variableNames, builder);
                                                    })
                                                    .executes((context) -> {
                                                        // Handle "variableName" and "variableValue" logic here
                                                        String variableName = StringArgumentType.getString(context, "variableName");
                                                        try {
                                                            Chat.getVariableValue(getConfig(), variableName);
                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                        return 1;
                                                    }))).executes((context) -> {
                                        // Handle the case when "config" argument is not provided
                                        // ...
                                        return 1;
                                    })))
            );
        }); //bbi

        KeyBinding devKeyBind = new KeyBinding("Open Mod Menu Config", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_ADD, "BBsentials: Developing Tools");
        KeyBindingHelper.registerKeyBinding(devKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (devKeyBind.wasPressed()) {
                MinecraftClient.getInstance().setScreen(BBsentialsConfigScreemFactory.create(MinecraftClient.getInstance().currentScreen));
            }
        });

        KeyBinding promptKeyBind = new KeyBinding("Chat Prompt Yes / Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "BBsentials");
        KeyBindingHelper.registerKeyBinding(promptKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (promptKeyBind.wasPressed()) {
                if (config.getLastChatPromptAnswer() != null) {
                    if (config.isDetailedDevModeEnabled()) {
                        Chat.sendPrivateMessageToSelfDebug(config.getLastChatPromptAnswer());
                    }
                    MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(config.getLastChatPromptAnswer());
                }
                config.setLastChatPromptAnswer(null);
            }
        });
        KeyBinding craftKeyBind = new KeyBinding("Craft", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "BBsentials");
        KeyBindingHelper.registerKeyBinding(craftKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (craftKeyBind.wasPressed()) MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/craft");
        });
        KeyBinding petKeyBind = new KeyBinding("Open Pet Menu", InputUtil.Type.KEYSYM,  -1, "BBsentials");
        KeyBindingHelper.registerKeyBinding(petKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (petKeyBind.wasPressed()) MinecraftClient.getInstance().getNetworkHandler().sendChatMessage("/pets");
        });
        for (int i = 1; i <= 9; i++) {
            KeyBinding ecPageKeyBind = new KeyBinding("Ender Chest Page " + i, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_1 + (i - 1), "BBsentials");
            KeyBindingHelper.registerKeyBinding(ecPageKeyBind);
            int pageNum = i; // Capture the page number for lambda
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (ecPageKeyBind.wasPressed()) {
                    BBsentials.getConfig().sender.addImmediateSendTask("/ec " + pageNum);
                }
            });
        }
    } // KeyBinds
}