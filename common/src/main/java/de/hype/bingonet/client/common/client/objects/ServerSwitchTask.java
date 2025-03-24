package de.hype.bingonet.client.common.client.objects;

import de.hype.bingonet.client.common.client.BingoNet;

public class ServerSwitchTask {
    public static int counter = 0;
    private final int id = counter++;
    public Runnable runnable;
    public boolean permanent = false;

    private ServerSwitchTask(Runnable runnable) {
        this.runnable = runnable;
    }

    private ServerSwitchTask(Runnable runnable, boolean permanent) {
        this.runnable = runnable;
        this.permanent = permanent;
    }

    public static int onServerLeaveTask(Runnable runnable, boolean permanent){
        ServerSwitchTask task = new ServerSwitchTask(runnable,permanent);
        BingoNet.onServerLeave.put(task.id, task);
        return task.id;
    }
    public static int onServerLeaveTask(Runnable runnable){
        ServerSwitchTask task = new ServerSwitchTask(runnable);
        BingoNet.onServerLeave.put(task.id, task);
        return task.id;
    }
    public static int onServerJoinTask(Runnable runnable, boolean permanent){
        ServerSwitchTask task = new ServerSwitchTask(runnable,permanent);
        BingoNet.onServerJoin.put(task.id, task);
        return task.id;
    }
    public static int onServerJoinTask(Runnable runnable){
        ServerSwitchTask task = new ServerSwitchTask(runnable);
        BingoNet.onServerJoin.put(task.id, task);
        return task.id;
    }
    public int getId() {
        return id;
    }

    public void run() {
        runnable.run();
    }
}