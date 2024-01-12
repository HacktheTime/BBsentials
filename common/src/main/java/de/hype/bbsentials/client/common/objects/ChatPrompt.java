package de.hype.bbsentials.client.common.objects;

import de.hype.bbsentials.client.common.client.BBsentials;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ChatPrompt {
    public ScheduledFuture<?> resetTask;
    public String command;

    public ChatPrompt(String command, int timeBeforeReset) {
        this.command = command;
        setResetTask(timeBeforeReset);
    }


    public void setResetTask(int timeBeforeReset) {
        this.resetTask = BBsentials.executionService.schedule(() -> {
            command=null;
        }, timeBeforeReset, TimeUnit.SECONDS);
    }
    public String getCommandAndCancel(){
        resetTask.cancel(true);
        String savedCommand = new String(command);
        command=null;
        return savedCommand;
    }
    public boolean isAvailibel(){
        return command!=null;
    }

}
