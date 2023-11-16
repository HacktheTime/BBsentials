package de.hype.bbsentials.common.client;


import java.util.Collections;
import java.util.List;

public interface DebugThread extends Runnable {

    @Override
    default void run() {
        while (true) {
        loop();
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
            }
        }
        //place a breakpoint for only this thread here.
    }

    default void loop() {
    }

    default List<String> test() {
        return Collections.singletonList("");
    }
}
