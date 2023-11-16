package de.hype.bbsentials.forge.client;

import com.google.gson.annotations.Expose;
import de.hype.bbsentials.forge.client.categories.FirstCategory;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;
import io.github.moulberry.moulconfig.annotations.ConfigEditorDropdown;
import io.github.moulberry.moulconfig.annotations.ConfigOption;
import io.github.moulberry.moulconfig.gui.GuiScreenElementWrapper;
import io.github.moulberry.moulconfig.gui.MoulConfigEditor;
import io.github.moulberry.moulconfig.processor.BuiltinMoulConfigGuis;
import io.github.moulberry.moulconfig.processor.ConfigProcessorDriver;
import io.github.moulberry.moulconfig.processor.MoulConfigProcessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.config.ConfigCategory;

import static de.hype.bbsentials.common.client.BBsentials.config;


public class MoulConfig extends Config {
    MoulConfigEditor<MoulConfig> editor;
    MoulConfigProcessor<MoulConfig> processor;
    @Expose
    @Category(name = "First Category", desc = "This is the first category.")
    public FirstCategory firstCategory = new FirstCategory();

    public MoulConfig() {
        processor = new MoulConfigProcessor<>(this);
        editor = new MoulConfigEditor<>(processor);
        BuiltinMoulConfigGuis.addProcessors(processor);
        ConfigProcessorDriver.processConfig(MoulConfig.class, this, processor);
    }

    @Override
    public String getTitle() {
        return "BBsentials " + de.hype.bbsentials.common.client.Config.apiVersion;
    }

    @Override
    public void saveNow() {
        config.save();
    }

    public void openConfigGui() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenElementWrapper(editor));
    }
}
