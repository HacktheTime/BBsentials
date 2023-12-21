package de.hype.bbsentials.forge;

import java.util.Collections;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.client.common.client.DebugThread {
    @Override
    public void loop() {
        test();
    }

    @Override
    public List<String> test() {
//        new MoulConfigManager().openConfigGui();
        return Collections.singletonList("");
    }
}
