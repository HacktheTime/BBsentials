package de.hype.bbsentials.common.client;


import java.util.ArrayList;
import java.util.List;

public class DebugThread implements Runnable {

    @Override
    public void run() {
         loop();
        //place a breakpoint for only this thread here.
    }

    public void loop() {

    }

    public static List<String> test() {
        return List.of("");
    }
}
