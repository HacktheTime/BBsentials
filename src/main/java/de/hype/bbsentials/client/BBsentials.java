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
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static de.hype.bbsentials.chat.Chat.*;

public class BBsentials implements ClientModInitializer {
    private boolean initialised = false;
    public static Config config;
    public static BBsentialConnection bbserver;
    public static CommandsOLD coms;
    public static ScheduledExecutorService executionService = Executors.newScheduledThreadPool(1000);
    private static Thread bbthread;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        System.out.println("ide: " + Boolean.getBoolean("runningFromIDE"));
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> {
            if (!initialised) {
                config = Config.load();
                Options.setGamma(10);
                Chat chat = new Chat();
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
                                                        sendPrivateMessageToSelf(Formatting.GREEN + "Saved config successfully");
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
                                                                            Chat.sendPrivateMessageToSelf("§cInvalid variable or value");
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
                }); //bbi}
                if (Config.isBingoTime() || config.overrideBingoTime()) {
                    connectToBBserver();
                }
                initialised = true;
            }
        });
    }

    {
        KeyBinding promptKeyBind = new KeyBinding("Chat Prompt Yes / Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "BBsentials");
        KeyBindingHelper.registerKeyBinding(promptKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (promptKeyBind.wasPressed()) {
                if (config.getLastChatPromptAnswer() != null) {
                    if (config.isDetailedDevModeEnabled()) {
                        Chat.sendPrivateMessageToSelf(config.getLastChatPromptAnswer());
                    }
                    MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(config.getLastChatPromptAnswer());
                }
                config.setLastChatPromptAnswer(null);
            }
        });
        KeyBinding craftKeyBind = new KeyBinding("Craft", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "BBsentials");
        KeyBindingHelper.registerKeyBinding(craftKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (craftKeyBind.wasPressed()) Chat.sendCommand("/craft");
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

    public static Config getConfig() {
        return config;
    }

    public static void connectToBBserver() {
        connectToBBserver(config.connectToBeta);
    }

    public static void connectToBBserver(boolean beta) {
        if (bbserver != null) {
            bbserver.sendHiddenMessage("exit");
        }
        if (bbthread != null) {
            if (bbthread.isAlive()) {
                bbthread.interrupt();
            }
        }
        bbthread = new Thread(() -> {
            bbserver = new BBsentialConnection();
            coms = new CommandsOLD();
            bbserver.setMessageReceivedCallback(message -> bbserver.onMessageReceived(message));
            if (beta) {
                bbserver.connect(config.getBBServerURL(), 5011);
            }
            else {
                bbserver.connect(config.getBBServerURL(), 5000);
            }
            executionService.scheduleAtFixedRate(new DebugThread(), 0, 20, TimeUnit.SECONDS);
        });
        bbthread.start();
    }

    public static void refreshCommands() {
        Chat.sendPrivateMessageToSelf("Setting up commands");
        coms = new CommandsOLD();
    }

}