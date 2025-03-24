package de.hype.bingonet.forge.client;

import com.google.gson.annotations.Expose;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.config.ConfigManager;
import de.hype.bingonet.forge.client.categories.FirstCategory;
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
        return "Bingo Net" + BingoNet.generalConfig.getApiVersion();
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
