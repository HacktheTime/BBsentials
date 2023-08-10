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

import static de.hype.bbsentials.chat.Chat.*;

public class BBsentials implements ClientModInitializer {
    private boolean initialised = false;
    public static Config config;
    public static BBsentialConnection bbserver;
    public static CommandsOLD coms;

    /**
     * Runs the mod initializer on the client environment.
     */
    @Override
    public void onInitializeClient() {
        ClientPlayConnectionEvents.JOIN.register((a, b, c) -> {
                    if (!initialised) {
                        config = Config.load();
                        Options.setGamma(10);
                        Chat chat = new Chat();
                        if (Config.isBingoTime() || config.overrideBingoTime()) {
                            connectToBBserver();
                        }
                        initialised = true;
                    }
                }
        );
        KeyBinding promptKeyBind = new KeyBinding("Chat Prompt Yes / Open Menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "BBsentials");
        KeyBindingHelper.registerKeyBinding(promptKeyBind);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (promptKeyBind.wasPressed()) {
                if (config.getLastChatPromptAnswer() != null) {
                    if (config.isDetailedDevModeEnabled()){
                    Chat.sendPrivateMessageToSelf(config.getLastChatPromptAnswer());}
                    MinecraftClient.getInstance().getNetworkHandler().sendChatMessage(config.getLastChatPromptAnswer());
                }
                config.setLastChatPromptAnswer(null);
            }
        });
    }

    public static Config getConfig() {
        return config;
    }

    public static void connectToBBserver() {
        if (bbserver != null) {
            bbserver.sendHiddenMessage("exit");
        }
        bbserver = new BBsentialConnection();
        bbserver.setMessageReceivedCallback(message -> bbserver.onMessageReceived(message));
        bbserver.connect(config.getBBServerURL(), 5000);
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("bbi")
                    .then(ClientCommandManager.literal("reconnect")
                            .executes((context) -> {
                                connectToBBserver();
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
                                                                    if (!variableName.contains("dev")||config.bbsentialsRoles.contains("dev")){
                                                                    setVariableValue(getConfig(), variableName, variableValue);}
                                                                    getConfig().save();
                                                                } catch (ClassNotFoundException | NoSuchFieldException |
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
    }

    public static void refreshCommands() {
        coms = new CommandsOLD();
    }

}