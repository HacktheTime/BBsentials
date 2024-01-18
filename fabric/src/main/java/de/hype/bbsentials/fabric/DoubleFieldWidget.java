package de.hype.bbsentials.fabric;

import de.hype.bbsentials.fabric.numpad.TextFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * Sets the Maximum length by default to 32767 from Minecraft default of like 30.
 */
public class DoubleFieldWidget extends IntegerFieldWidget {
    public DoubleFieldWidget(TextRenderer textRenderer, int width, int height, Text text) {
        super(textRenderer, width, height, text);
    }

    public DoubleFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public DoubleFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text) {
        super(textRenderer, x, y, width, height, copyFrom, text);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr == '.' || chr == ',') return super.typeChar('.', modifiers, true);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public String getText() {
        return super.getText().replaceAll(",", ".");
    }
}
