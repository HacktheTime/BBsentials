package de.hype.bbsentials.fabric;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

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
        if (chr == '.' || chr == ',') return super.typeChar('.', modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public String getText() {
        return super.getText().replaceAll(",", ".");
    }
}
