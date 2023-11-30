package de.hype.bbsentials.forge;

import de.hype.bbsentials.forge.client.MoulConfigManager;

import java.util.Collections;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.common.client.DebugThread {
    @Override
    public void loop() {
        test();
    }

    @Override
    public List<String> test() {
        new MoulConfigManager().openConfigGui();
        return Collections.singletonList("");
    }
}
