package de.hype.bbsentials.client;

import net.minecraft.scoreboard.Scoreboard;

public class DebugThread implements Runnable {
    Scoreboard temp;
    @Override
    public void run() {
    loop();
        //place a breakpoint for only this thread here.
    }

    public void loop() {

    }
}
