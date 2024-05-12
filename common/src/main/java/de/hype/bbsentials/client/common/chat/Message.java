package de.hype.bbsentials.client.common.chat;

import com.google.gson.JsonObject;
import de.hype.bbsentials.client.common.client.BBsentials;

import static de.hype.bbsentials.shared.objects.Message.MessageSource.PRIVATE_MESSAGE_RECEIVE;
import static de.hype.bbsentials.shared.objects.Message.MessageSource.SELFCREATED;


public class Message extends de.hype.bbsentials.shared.objects.Message {
    protected Boolean isFromReportedUser;

    public Message(String textJson, String string) {
        this(textJson, string, false);
    }

    public Message(String textJson, String string, boolean actionbar) {
        super(textJson, string, actionbar);
        isFromReportedUser = isFromReportedUser();
    }

    public static Message of(String string) {
        JsonObject obj = new JsonObject();
        obj.addProperty("text", string);
        Message message = new Message(obj.toString(), string);
        message.source = SELFCREATED;
        return message;
    }

    public static Message tellraw(String json) {
        Message message = new Message(json, "");
        message.source = SELFCREATED;
        return message;
    }

    @Override
    public String getSelfUsername() {
        return BBsentials.generalConfig.getUsername();
    }

    public boolean isFromReportedUser() {
        return BBsentials.temporaryConfig.alreadyReported.contains(getPlayerName()) && !getPlayerName().isEmpty();
    }

    public void replyToUser(String message) {
        String commandStart = source.replyCommandStart;
        if (commandStart == null) return;
        if (source != PRIVATE_MESSAGE_RECEIVE)
            BBsentials.sender.addImmediateSendTask(commandStart + getPlayerName() + " " + message);
        else
            BBsentials.sender.addImmediateSendTask("/r " + getPlayerName() + " " + message);
    }
}
