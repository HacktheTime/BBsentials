package de.hype.bbsentials.forge.client;

import com.google.gson.annotations.Expose;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.config.ConfigManager;
import de.hype.bbsentials.forge.client.categories.FirstCategory;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;



public class MoulConfig extends Config {
    @Category(name = "First Category", desc = "This is the first category.")
    @Expose
    public FirstCategory firstCategory = new FirstCategory();

    @Override
    public String getTitle() {
        return "BBsentials " + BBsentials.generalConfig.getApiVersion();
    }

    @Override
    public void saveNow() {
        ConfigManager.saveAll();
    }

    @Override
    public void executeRunnable(int runnableId) {
        Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("Just executed runnableId " + runnableId));
    }

    @Override
    public boolean shouldAutoFocusSearchbar() {
        return true;
    }
}
