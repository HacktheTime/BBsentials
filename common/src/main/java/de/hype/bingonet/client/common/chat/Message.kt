package de.hype.bingonet.client.common.chat

import de.hype.bingonet.client.common.client.BingoNet
import de.hype.bingonet.shared.objects.Message

class Message @JvmOverloads constructor(textJson: String, string: String, actionbar: Boolean = false) :
    Message(textJson, string, actionbar) {

    fun isFromReportedUser(): Boolean {
        return BingoNet.temporaryConfig.alreadyReported.contains(getPlayerName()) && !getPlayerName().isEmpty()
    }

    fun replyToUser(message: String?) {
        val commandStart = source.replyCommandStart
        if (commandStart.isEmpty()) return
        if (source != MessageSource.PRIVATE_MESSAGE_RECEIVE) BingoNet.sender.addImmediateSendTask(commandStart + getPlayerName() + " " + message)
        else BingoNet.sender.addImmediateSendTask("/r " + getPlayerName() + " " + message)
    }

    companion object {
        fun tellraw(replace: kotlin.String): Message {
            return de.hype.bingonet.client.common.chat.Message.tellraw(replace)
        }
    }
}
