package de.hype.bbsentials.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.text.Text;

public class ModMenueScreen implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (!FabricLoader.getInstance().isModLoaded("cloth-config2")) {
            return parent -> new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(parent), Text.of("BBsentials"), Text.of("BBsentials requires Cloth ConfigManager to be able to show the configManager."));
        }
        return BBsentialsConfigScreenFactory::create;
    }
}
