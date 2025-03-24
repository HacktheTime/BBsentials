package de.hype.bingonet.fabric.numpad;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * Sets the Maximum length by default to 32767 from Minecraft default of like 30.
 */
public class TextFieldWidget extends net.minecraft.client.gui.widget.TextFieldWidget {
    public TextFieldWidget(TextRenderer textRenderer, int width, int height, Text text) {
        super(textRenderer, width, height, text);
        super.setMaxLength(32767);
    }

    public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
        super.setMaxLength(32767);
    }

    public TextFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable net.minecraft.client.gui.widget.TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
        super.setMaxLength(32767);
    }
}
