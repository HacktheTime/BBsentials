package de.hype.bingonet.client.common.chat;

import de.hype.bingonet.client.common.client.BingoNet;

import static de.hype.bingonet.shared.objects.Message.MessageSource.PRIVATE_MESSAGE_RECEIVE;


public class Message extends de.hype.bingonet.shared.objects.Message {
    protected Boolean isFromReportedUser;

    public Message(String textJson, String string) {
        this(textJson, string, false);
    }

    public Message(String textJson, String string, boolean actionbar) {
        super(textJson, string, actionbar);
        isFromReportedUser = isFromReportedUser();
    }

    public boolean isFromReportedUser() {
        return BingoNet.temporaryConfig.alreadyReported.contains(getPlayerName()) && !getPlayerName().isEmpty();
    }

    public void replyToUser(String message) {
        String commandStart = source.replyCommandStart;
        if (commandStart == null) return;
        if (source != PRIVATE_MESSAGE_RECEIVE)
            BingoNet.sender.addImmediateSendTask(commandStart + getPlayerName() + " " + message);
        else
            BingoNet.sender.addImmediateSendTask("/r " + getPlayerName() + " " + message);
    }
}
