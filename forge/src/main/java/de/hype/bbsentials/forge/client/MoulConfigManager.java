package de.hype.bbsentials.forge.client;

import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper;
import io.github.moulberry.moulconfig.gui.MoulConfigEditor;
import io.github.moulberry.moulconfig.gui.MoulGuiOverlayEditor;
import io.github.moulberry.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.moulberry.moulconfig.processor.ConfigProcessorDriver;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.Minecraft;

public class MoulConfigManager {
    static MoulConfig moulConfig = new MoulConfig();
    MoulConfigProcessor<MoulConfig> testConfigMoulConfigProcessor;

    public MoulConfigManager() {
        testConfigMoulConfigProcessor = new MoulConfigProcessor<>(moulConfig);
        BuiltinMoulConfigGuis.addProcessors(testConfigMoulConfigProcessor);
        ConfigProcessorDriver.processConfig(moulConfig.getClass(), moulConfig, testConfigMoulConfigProcessor);
    }

    public void openConfigGui() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenElementWrapper(new MoulConfigEditor<>(testConfigMoulConfigProcessor)));
    }
}
