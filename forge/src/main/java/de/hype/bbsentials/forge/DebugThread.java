package de.hype.bbsentials.forge;

import de.hype.bbsentials.client.common.mclibraries.CustomItemTexture;
import de.hype.bbsentials.forge.client.MoulConfigManager;

import java.util.Collections;
import java.util.List;

public class DebugThread implements de.hype.bbsentials.client.common.client.DebugThread {
    @Override
    public void loop() {
        test();
    }

    @Override
    public List<String> test() {
        return Collections.singletonList("");
    }
}
