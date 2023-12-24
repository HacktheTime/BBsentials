package de.hype.bbsentials.client.common.client.objects;

public class ServerSwitchTask {
    public Runnable runnable;
    public boolean permanent;

    public ServerSwitchTask(Runnable runnable) {
        this.runnable = runnable;
    }

    public ServerSwitchTask(Runnable runnable, boolean permanent) {
        this.runnable = runnable;
        this.permanent = permanent;
    }

    public void run() {
        runnable.run();
    }
}