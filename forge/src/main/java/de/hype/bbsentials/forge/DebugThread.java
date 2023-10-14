package de.hype.bbsentials.forge;

import java.util.Collections;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.common.client.DebugThread {
    @Override
    public void loop() {

    }

    @Override
    public List<String> test() {
        return Collections.singletonList("");
    }
}
