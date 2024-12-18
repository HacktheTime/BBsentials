package de.hype.bbsentials.client.common.chat;

import de.hype.bbsentials.client.common.client.BBsentials;

import static de.hype.bbsentials.shared.objects.Message.MessageSource.PRIVATE_MESSAGE_RECEIVE;


public class Message extends de.hype.bbsentials.shared.objects.Message {
    protected Boolean isFromReportedUser;

    public Message(String textJson, String string) {
        this(textJson, string, false);
    }

    public Message(String textJson, String string, boolean actionbar) {
        super(textJson, string, actionbar);
        isFromReportedUser = isFromReportedUser();
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
