package de.hype.bbsentials.common.client;


import java.util.Collections;
import java.util.List;

public interface DebugThread extends Runnable {

    @Override
    public default void run() {
        loop();
        //place a breakpoint for only this thread here.
    }

    public default void loop() {
    }

    public default List<String> test() {
        return Collections.singletonList("");
    }
}
