package de.hype.bbsentials.fabric.numpad;

import de.hype.bbsentials.common.api.Formatting;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.fabric.IntegerTextField;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class NumCodeInputField extends IntegerTextField {
    public final NumPadConfigScreen codeConfigScreen;

    public NumCodeInputField(TextRenderer textRenderer, int width, int height, Text text, NumPadConfigScreen codeConfigScreen) {
        super(textRenderer, width, height, text);
        this.codeConfigScreen = codeConfigScreen;
        setChangedListener(this::onChangeEvent);
    }

    public NumCodeInputField(TextRenderer textRenderer, int x, int y, int width, int height, Text text, NumPadConfigScreen codeManager) {
        super(textRenderer, x, y, width, height, text);
        this.codeConfigScreen = codeManager;
        setChangedListener(this::onChangeEvent);
    }

    public NumCodeInputField(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text, NumPadConfigScreen codeManager) {
        super(textRenderer, x, y, width, height, copyFrom, text);
        this.codeConfigScreen = codeManager;
        setChangedListener(this::onChangeEvent);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    private boolean codeIsFree(String input) {
        int usedAlready = 0;
        for (int i1 = 0; i1 < codeConfigScreen.codes.numCodes.size(); i1++) {
            if (codeConfigScreen.codes.numCodes.get(i1).code.equals(input)) {
                usedAlready++;
                if (usedAlready > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean forbiddenCode(String input) {
        if (BBsentials.config.hasBBRoles("dev")) return false;
        if (input.startsWith(String.valueOf(0))) {
            return true;
        }
        return false;
    }

    public void onChangeEvent(String newText) {
        codeConfigScreen.updateCodes();
        if (!codeIsFree(newText) || forbiddenCode(newText)) {
            setTooltip(Tooltip.of(Text.of(Formatting.RED + "You can not use this code.§r\nWarning: This is only updated once clicking into the field!")));
        }
        else {
            setTooltip(Tooltip.of(Text.of("")));
        }
    }
}
