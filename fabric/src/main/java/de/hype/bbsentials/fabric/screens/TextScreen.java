package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.chat.Chat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.text.Text;

public class TextScreen extends Screen {
    protected TextScreen() {
        super(Text.of("Test"));
        setScreen();
    }

    public void updateScreen() {
        clearChildren();
        GridWidget gridWidget = new GridWidget();
        gridWidget.setSpacing(10);
        gridWidget.getMainPositioner().marginX(5).marginY(2);
        GridWidget.Adder adder = gridWidget.createAdder(5);
        for (int i = 0; i < 20; i++) {
            int finalI = i;
            adder.add(ButtonWidget.builder(Text.of(String.valueOf(finalI)), (buttonWidget) -> Chat.sendPrivateMessageToSelfSuccess(String.valueOf(finalI))).size(30, 30).build());
        }
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, 0, width, height);
//        SimplePositioningWidget.setPos(gridWidget, 0, this.height - 64, this.width, 64);
        gridWidget.forEachChild(this::addDrawableChild);

    }

    public void setScreen() {
        MinecraftClient.getInstance().execute(() -> {
                    MinecraftClient.getInstance().setScreen(this);
                    updateScreen();
                }
        );
    }
}