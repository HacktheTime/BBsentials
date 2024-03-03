package de.hype.bbsentials.client.common.discordintegration;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DiscordIntegration extends ListenerAdapter {
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private final List<String> silentMessageBuffer = new ArrayList<>();
    int afkTimer = 0;
    String status = "";
    private User botOwner;
    private JDA jda;
    private PrivateChannel dms;
    private long lastCommandTime = 0;
    private boolean afk = false;

    public DiscordIntegration() {
        if (!BBsentials.discordConfig.discordIntegration) return;
        if (!BBsentials.discordConfig.useBridgeBot) return;
        jda = start();
        BBsentials.executionService.scheduleAtFixedRate(() -> updateStatus(), 0, 10, TimeUnit.MINUTES);
        // Schedule the message update task
        executorService.scheduleAtFixedRate(this::updateDMs, 0, 30, TimeUnit.SECONDS);
    }

    public static void reply(GenericCommandInteractionEvent event, String message) {
        event.getHook().sendMessage(message).queue();
    }

    public static void sendScreenshotMessage(SlashCommandInteractionEvent event) {
        event.getHook().editOriginalAttachments(FileUpload.fromData(EnvironmentCore.utils.makeScreenshot(), "screenshot.png")).queue();
    }

    public static void deleteAllDms(PrivateChannel dms) {
        // Get your bot's User object (to identify messages sent by your bot)
        User selfUser = dms.getJDA().getSelfUser();

        // List to store messages to delete
        List<Message> messagesToDelete = new ArrayList<>();

        // Retrieve the messages in the private channel and add bot's messages to the list
        for (Message message : dms.getIterableHistory()) {
            if (message.getAuthor().equals(selfUser)) {
                messagesToDelete.add(message);
            }
        }

        // Delete messages in bulk
        if (!messagesToDelete.isEmpty()) {
            // Limit the number of messages to delete to Discord's maximum (100 at a time)
            int batchSize = Math.min(messagesToDelete.size(), 100);
            List<Message> batch = messagesToDelete.subList(0, batchSize);

            // Delete the batch of messages
            dms.purgeMessages(batch);
        }
    }

    public JDA start() {
        if (BBsentials.discordConfig.botToken.isEmpty()) {
            Chat.sendPrivateMessageToSelfError("Bot Token is missing");
            return null;
        }
        JDABuilder builder = JDABuilder.createDefault(BBsentials.discordConfig.botToken);
        builder.addEventListeners(this);
        try {
            JDA JDA = builder.build();
            jda = JDA;
            return JDA;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void onReady(ReadyEvent event) {
        this.botOwner = jda.retrieveUserById(BBsentials.discordConfig.botOwnerUserId).complete();
        dms = botOwner.openPrivateChannel().complete();
        if (BBsentials.discordConfig.deleteHistoryOnStart) deleteAllDms(dms);
        if (BBsentials.discordConfig.doStartupMessage)
            dms.sendMessage("Bot is now online").setSuppressedNotifications(true).queue();
        registerGlobalCommands();
        Chat.sendPrivateMessageToSelfSuccess("BB DC-Bot is up!");
    }

    public void receivedInGameMessage(de.hype.bbsentials.client.common.chat.Message message) {
        if (!BBsentials.discordConfig.useBridgeBot || !BBsentials.discordConfig.discordIntegration || BBsentials.discordConfig.isDisableTemporary())
            return;
        // Always send non-silent messages immediately
        if (!filter(message)) return;
        if (getAfk()) {
            if (isImportant(message)) {
                updateDMs();
                dms.sendMessage("**" + message + "**").setSuppressedNotifications(false).queue();
            }
            else {
                // Add silent messages to the buffer
                silentMessageBuffer.add(message.getUnformattedString());
            }
        }
    }

    /**
     * @return when returned true message is important and not silent
     */
    public boolean isImportant(de.hype.bbsentials.client.common.chat.Message advancedMessage) {
        if (BBsentials.discordConfig.alwaysSilent) return false;
        String simpleMessage = advancedMessage.getUnformattedString();
        if (simpleMessage.contains("afk") || simpleMessage.contains("hub")) {
            return true;
        }
        return false;
    }

    public boolean filter(de.hype.bbsentials.client.common.chat.Message advancedMessage) {
        String simpleMessage = advancedMessage.getUnformattedString();
        if (simpleMessage.isEmpty()) return false;
        if (advancedMessage.actionBar) return false;
        if (simpleMessage.contains("stash")) return false;
        if (simpleMessage.contains("to pick them up")) return false;
        return true;
    }

    public void updateDMs() {
        if (afkCheck()) {
            List<String> copiedBuffer = new ArrayList<>();
            copiedBuffer.addAll(silentMessageBuffer);
            silentMessageBuffer.clear();
            // Send the silent messages as a batch
            StringBuilder combinedMessage = new StringBuilder(getLastMessage().getContentRaw());
            boolean sendNew = false;
            for (String msg : copiedBuffer) {
                if ((combinedMessage.length() + msg.length() + 1 <= 2000) && !sendNew) {
                    if (!(combinedMessage.length() == 0)) {
                        combinedMessage.append('\n'); // Add a newline if not the first message
                    }
                    combinedMessage.append(msg);
                }
                else {
                    if ((combinedMessage.length() <= 1) && !sendNew) {
                        getLastMessage().editMessage(combinedMessage).queue();
                        combinedMessage = new StringBuilder(msg); // Start a new batch
                        sendNew = true;
                    }
                    else {
                        if ((msg.length() + 2 + combinedMessage.length()) >= 2000) {
                            dms.sendMessage(combinedMessage.toString()).setSuppressedNotifications(true).queue();
                            sendNew = false;
                            combinedMessage = new StringBuilder(msg); // Start a new batch
                        }
                        combinedMessage.append("\n").append(msg);
                    }
                }
            }
            // Send the remaining messages, if any
            if (sendNew) {
                dms.sendMessage(combinedMessage.toString()).setSuppressedNotifications(true).queue();
            }
            else {
                getLastMessage().editMessage(combinedMessage).queue();
            }
            copiedBuffer.clear();
        }
    }

    // Helper method to get the last message in the DM channel.
    private Message getLastMessage() {
        List<Message> messages = dms.getHistory().retrievePast(1).complete();
        return messages.isEmpty() ? null : messages.get(0);
    }

    public void registerGlobalCommands() {
        jda.updateCommands().addCommands(
                Commands.slash("lobby", "/lobby"),
                Commands.slash("skyblock", "/skyblock"),
                Commands.slash("is", "warps you to your private island"),
                Commands.slash("hub", "warps you to the hub"),
                Commands.slash("warp-garden", "warps you to the garden"),
                Commands.slash("screenshot", "makes a screenshot of the screen and sends it to you"),
                Commands.slash("clear", "clears the messages sent by the bot"),
                Commands.slash("status-disable", "Disable the Output"),
                Commands.slash("status-enable", "Enable the Output"),
                Commands.slash("shutdown", "Shuts down your pc"),
                Commands.slash("suspend", "Puts your pc into sleep"),
                Commands.slash("hibernate", "Puts your pc into hibernation (shutdown but restart with all application data)"),

                Commands.slash("custom", "allows you to specify a custom command to be executed").addOption(OptionType.STRING, "command", "command to be executed", true)
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        if (event.getUser().equals(botOwner)) {
            if (event.getName().equals("lobby")) {
                BBsentials.sender.addSendTask("/lobby", 0);
                reply(event, "Performed");
            }
            else if (event.getName().equals("skyblock")) {
                BBsentials.sender.addSendTask("/skyblock", 0);
                reply(event, "Performed");
            }
            else if (event.getName().equals("is")) {
                BBsentials.sender.addSendTask("/is", 0);
                reply(event, "Performed");
            }
            else if (event.getName().equals("hub")) {
                BBsentials.sender.addSendTask("/hub", 0);
                reply(event, "Performed");
            }
            else if (event.getName().equals("warp-garden")) {
                BBsentials.sender.addSendTask("/warp garden", 0);
                reply(event, "Performed");
            }
            else if (event.getName().equals("screenshot")) {
                try {
                    sendScreenshotMessage(event);
                } catch (Exception e) {
                    reply(event, e.getMessage());
                    e.printStackTrace();
                }
            }
            else if (event.getName().equals("custom")) {
                if (!BBsentials.discordConfig.allowCustomCommands) {
                    reply(event, "Custom Commands are disabled.");
                    return;
                }
                try {
                    String command = event.getOption("command").getAsString();
                    BBsentials.sender.addSendTask(command, 0);
                    reply(event, "Done");
                } catch (Exception e) {
                    reply(event, "Invalid command");
                }
            }
            else if (event.getName().equals("clear")) {
                try {
                    event.getChannel().asPrivateChannel().purgeMessages(event.getChannel().asPrivateChannel().getHistory().getRetrievedHistory().stream().filter(message -> message.getAuthor().getId().equals(jda.getSelfUser().getId())).collect(Collectors.toList()));
                    reply(event, "Done");
                } catch (Exception ignored) {
                    reply(event, "Error Occur");
                }
            }
            else if (event.getName().equals("status-enable")) {
                if (!BBsentials.discordConfig.useBridgeBot || !BBsentials.discordConfig.discordIntegration) {
                    reply(event, new EmbedBuilder().setColor(Color.MAGENTA).setTitle("Status: Disabled").setDescription("The Current Configuration does not allow using the Bridge bot please allow it First. This needs to be done in Person!").build());
                    return;
                }
                BBsentials.discordConfig.setDisableTemporary(false);
                reply(event, new EmbedBuilder().setColor(Color.RED).setTitle("Status: Enabled").setDescription("Status is now enabled you may see messages now.").build());
            }
            else if (event.getName().equals("status-disable")) {
                if (!BBsentials.discordConfig.useBridgeBot || !BBsentials.discordConfig.discordIntegration) {
                    reply(event, new EmbedBuilder().setColor(Color.MAGENTA).setTitle("Status: Disabled").setDescription("The Current Configuration does not allow using the Bridge bot please allow it First. This needs to be done in Person!").build());
                    return;
                }
                BBsentials.discordConfig.setDisableTemporary(true);
                reply(event, new EmbedBuilder().setColor(Color.GREEN).setTitle("Status: Disabled").setDescription("Status you no longer receive messages here").build());
            }
            else if (event.getName().equals("shutdown")) {
                try {
                    EnvironmentCore.utils.shutdownPC();
                    reply(event, "Shutting down in 20 Seconds");
                } catch (IOException e) {
                    reply(event, "Error Occcur: " + e.getMessage());
                }
            }
            else if (event.getName().equals("suspend")) {
                try {
                    EnvironmentCore.utils.suspendPC();
                    reply(event, "Going into sleep in 20 Seconds");
                } catch (IOException e) {
                    reply(event, "Error Occcur: " + e.getMessage());
                }
            }
            else if (event.getName().equals("hibernate")) {
                try {
                    EnvironmentCore.utils.hibernatePC();
                    reply(event, "Going into hibernation in 20 Seconds");
                } catch (IOException e) {
                    reply(event, "Error Occcur: " + e.getMessage());
                }
            }
        }
        else {
            reply(event, "Only the Owner may execute Commands");
        }
    }

    private void reply(SlashCommandInteractionEvent event, MessageEmbed embed) {
        event.getHook().sendMessageEmbeds(embed).queue();
    }

    public boolean afkCheck() {
        return !EnvironmentCore.utils.isWindowFocused();
    }

    public boolean getAfk() {
        return afk;
    }

    public void setNewStatus(String status) {
        this.status = status;
    }

    public void updateStatus() {
        OnlineStatus oldStatus = jda.getPresence().getStatus();
        String oldStatusMessage = jda.getPresence().getActivity().getName();
        OnlineStatus newStatus = OnlineStatus.IDLE;
        if (!EnvironmentCore.utils.isWindowFocused()) {
            newStatus = OnlineStatus.ONLINE;
        }
        if (!BBsentials.discordConfig.discordIntegration) {
            newStatus = OnlineStatus.DO_NOT_DISTURB;
        }
        if (!oldStatus.equals(newStatus) && !oldStatusMessage.equals(status)) {
            jda.getPresence().setPresence(newStatus, Activity.customStatus(status));
        }
    }

    @Override
    public void onGenericMessageReaction(@NotNull GenericMessageReactionEvent event) {
        if (Objects.equals(event.getUser(), botOwner)) {
            event.getChannel().retrieveMessageById(event.getMessageId()).queue(message -> message.delete().queue());
        }
    }

    public void sendMessage(MessageCreateData data) {
        dms.sendMessage(data).queue();
    }

    public void sendEmbed(MessageEmbed embed) {
        dms.sendMessageEmbeds(embed).queue();
    }
}