package de.hype.bingonet.client.common.objects;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChatPrompt {
    public ScheduledFuture<?> resetTask;
    public String command;
    public Runnable task;

    public ChatPrompt(String command, int timeBeforeReset) {
        this.command = command;
        setResetTask(timeBeforeReset);
    }

    public ChatPrompt(Runnable task, int timeBeforeReset) {
        this.task = task;
        setResetTask(timeBeforeReset);
    }


    public void setResetTask(int timeBeforeReset) {
        this.resetTask = BingoNet.executionService.schedule(() -> {
            command = null;
            task = null;
        }, timeBeforeReset, TimeUnit.SECONDS);
    }

    public String getCommandAndCancel() {
        resetTask.cancel(true);
        String savedCommand = command;
        command = null;
        return savedCommand;
    }

    public boolean isAvailable() {
        return command != null || task != null;
    }

    public boolean isCommand() {
        return command != null;
    }

    public void execute() {
        resetTask.cancel(false);
        Runnable finalTask = task;
        task=null;
        if (finalTask != null) BingoNet.executionService.execute(finalTask);
        else Chat.sendPrivateMessageToSelfError("Tried executing a non existing Chat Prompt task!");
    }
}
