package de.hype.bbsentials.fabric.numpad;

import de.hype.bbsentials.shared.constants.Formatting;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.fabric.screens.components.IntegerFieldWidget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

/**
 * Sets the Maximum length by default to 32767 from Minecraft default of like 30.
 */
public class NumCodeInputFieldWidget extends IntegerFieldWidget {
    public final NumPadCodeConfigScreen codeConfigScreen;
    int index;
    public NumCodeInputFieldWidget(TextRenderer textRenderer, int width, int height, Text text, NumPadCodeConfigScreen codeConfigScreen, int index) {
        super(textRenderer, width, height, text);
        this.index = index;
        this.codeConfigScreen = codeConfigScreen;
        setChangedListener(this::onChangeEvent);
    }

    public NumCodeInputFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text, NumPadCodeConfigScreen codeManager) {
        super(textRenderer, x, y, width, height, text);
        this.index = index;
        this.codeConfigScreen = codeManager;
        setChangedListener(this::onChangeEvent);
    }

    public NumCodeInputFieldWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable TextFieldWidget copyFrom, Text text, NumPadCodeConfigScreen codeManager) {
        super(textRenderer, x, y, width, height, copyFrom, text);
        this.index = index;
        this.codeConfigScreen = codeManager;
        setChangedListener(this::onChangeEvent);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return super.charTyped(chr, modifiers);
    }

    private boolean codeIsFree(String input) {
        for (int i1 = 0; i1 < codeConfigScreen.codeManager.numCodes.size(); i1++) {
            if (codeConfigScreen.codeManager.numCodes.get(i1).code.equals(input)) {
                if (i1 != index) return false;
            }
        }
        return true;
    }

    public boolean forbiddenCode(String input) {
        if (BBsentials.generalConfig.hasBBRoles("dev")) return false;
        if (input.startsWith(String.valueOf(0))) {
            return true;
        }
        return false;
    }

    public void onChangeEvent(String newText) {
        if (!codeIsFree(newText) || forbiddenCode(newText)) {
            setTooltip(Tooltip.of(Text.of(Formatting.RED + "You can not use this code.§r\nWarning: This is only updated once clicking into the field!")));
            if (codeConfigScreen.okButton != null) {
                codeConfigScreen.okButton.active = false;
            }
        }
        else {
            if (codeConfigScreen.okButton != null) {
                codeConfigScreen.okButton.active = true;
            }
            setTooltip(Tooltip.of(Text.of("")));
        }
    }
}
