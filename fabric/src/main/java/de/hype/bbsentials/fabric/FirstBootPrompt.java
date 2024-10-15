package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.SystemUtils;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.communication.BBsentialConnection;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirstBootPrompt extends Screen {
    public final Screen parent;
    boolean wait = true;
    MinecraftClient client = MinecraftClient.getInstance();
    TextRenderer textRenderer = client.textRenderer;

    protected FirstBootPrompt() {
        super(Text.of("BBsentials Notice"));
        parent = client.currentScreen;
        String notice =

                """
                        Before you can use BBsentials you need to do some things. If you dont want to click the mod Self Remove Button.
                        
                        BBsentials is a Network Mod. Due too this we require you to link (Your/a) Discord to your Minecraft Account.
                        
                        This is to inform about changes and have a separate way to contact you other than MC.
                        
                        We take ourselves a LOT of Freedom going further than most expect:
                        We spent Thousands of Hours into the Mod and its Server and the Website in total. Over 1 Year of mainly Development in free time adds up!
                        Due too that we take the freedom to request things from you as well.
                        We have DRM in this mod alongside a Punishment System. Those DRM's for example allow us to trigger a mod self removal.
                        We dont want to harm but we dont want some individuals to be able to use the things we spent a lot of effort in.
                        We also have a couple Trolls at our disposal but those should not cause any serious damage.
                        We collect Data about Bingo which also include some personal Data of yours such as Profiles, Contribution Numbers and more.
                        
                        We dont want to sell your Data but use them to analyze Bingos and improve our Data sets which should help the whole community.
                        
                        In the past we noticed that most people arent aware that this mod requires Registration etc which we try to Solve with this Screen.
                        
                        The First time we get Data about you is when you click the try connect Button or when you register. If you dont agree to this click the Self Remove Button. To open the Discord if you still need to link Click the Discord Button.
                        """;
        List<String> splitted = new ArrayList<>(Arrays.asList(notice.split("\n")));

        clearAndInit();
        GridWidget gridWidget = new GridWidget();
        gridWidget.setSpacing(10);
        gridWidget.getMainPositioner().marginX(5).marginY(2);
        GridWidget.Adder adder = gridWidget.createAdder(5);
        for (String s : splitted) {
            TextWidget textWidget = new TextWidget(Text.literal(s), textRenderer);
            addDrawableChild(textWidget);
        }
        ButtonWidget openDiscord = new ButtonWidget.Builder(Text.literal("Open Discord in Browser"), (b) -> this.openDiscord()).build();
        addSelectableChild(openDiscord);
        ButtonWidget connectToNetwork = new ButtonWidget.Builder(Text.literal("Connect to Network"), (b) -> this.connectToNetwork()).build();
        addSelectableChild(connectToNetwork);
        ButtonWidget modSelfRemove = new ButtonWidget.Builder(Text.literal("Mod Self Remove"), (b) -> this.selfRemove()).build();
        addSelectableChild(modSelfRemove);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, width, height);
        gridWidget.forEachChild((w) -> {
            if (w instanceof ButtonWidget) {
                addSelectableChild(w);
            } else addDrawableChild(w);
        });
    }

    private void selfRemove() {
        if (BBsentialConnection.selfDestruct()) {
            throw new RuntimeException("Mod was removed successfully. You can Relaunch (cause we cant for you)");
        } else {
            client.execute(() -> client.setScreen(new NoticeScreen(() -> {
                throw new RuntimeException("Closing MC");
            }, Text.literal("BBsentials"), Text.literal("Sorry we weren't able to remove the mod for you. You will need to remove it yourself"))));
        }
    }

    public void waitFor() {
        while (wait) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void openDiscord() {
        String discord = "ArtWfVMwxm";
        SystemUtils.openInBrowser("https://discord.gg/%s".formatted(discord));
        SystemUtils.setClipboardContent(discord);
    }

    public void connectToNetwork() {
        ((Utils) EnvironmentCore.utils).displayToast(new Utils.BBToast("Connecting to Network", "Connecting", null, null));
        BBsentials.connectToBBserver();
        while (BBsentials.connection == null) {
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (BBsentials.connection.getAuthenticated()) {
            ((Utils) EnvironmentCore.utils).displayToast(new Utils.BBToast("Connected", "Authentication Successful. Continuing", null, null));
            close();
        } else {
            ((Utils) EnvironmentCore.utils).displayToast(new Utils.BBToast("Connecting Failed", "You are not registered. Please register! (If you are registered try again. If still not working you probably have an ongoing Punishment.)", null, null));
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void close() {
        client.execute(() -> client.setScreen(parent));
        wait = false;
    }
}
