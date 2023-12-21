package de.hype.bbsentials.forge.client;

import com.google.gson.annotations.Expose;
import de.hype.bbsentials.forge.client.categories.FirstCategory;
import io.github.moulberry.moulconfig.Config;
import io.github.moulberry.moulconfig.annotations.Category;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

import static de.hype.bbsentials.client.common.client.BBsentials.config;


public class MoulConfig extends Config {
    @Expose
    @Category(name = "First Category", desc = "This is the first category.")
    public FirstCategory firstCategory = new FirstCategory();

    @Override
    public String getTitle() {
        return "BBsentials " + de.hype.bbsentials.client.common.client.Config.apiVersion;
    }

    @Override
    public void saveNow() {
        config.save();
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
