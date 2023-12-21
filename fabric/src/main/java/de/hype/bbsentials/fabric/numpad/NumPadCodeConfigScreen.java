package de.hype.bbsentials.fabric.numpad;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.fabric.DoubleFieldWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;


public class NumPadCodeConfigScreen extends Screen {
    NumCode code;
    NumPadCodes codeManager;
    int codeIndex;
    private NumCodeInputFieldWidget codeTextField;
    private ButtonWidget deleteButton;
    ButtonWidget okButton;
    private ButtonWidget addButton;
    private List<TextFieldWidget> commandFields = new ArrayList<>();
    private List<DoubleFieldWidget> delayFields = new ArrayList<>();
    private NumPadCodesConfigScreen parent;


    protected NumPadCodeConfigScreen(NumPadCodes codeManager, int codeIndex, NumPadCodesConfigScreen parent) {
        super(Text.of("NumPadCodesConfigScreen"));
        this.code = codeManager.numCodes.get(codeIndex);
        this.codeIndex = codeIndex;
        this.parent = parent;
        this.codeManager = codeManager;
    }

    @Override
    protected void init() {
        if (okButton == null) {
            codeTextField = new NumCodeInputFieldWidget(textRenderer, width / 2, 20, Text.of(code.code), this, codeIndex);
            codeTextField.setText(code.code);
            addButton = new ButtonWidget.Builder(Text.of("+"), button -> {
                addNewCommand();
            }).build();
            deleteButton = new ButtonWidget.Builder(Text.of("-"), button -> {
                codeManager.numCodes.remove(codeIndex);
                close();
            }).build();
            okButton = new ButtonWidget.Builder(Text.of("Done"), button -> saveAndExit()).build();
            for (int i = 0; i < code.commands.size(); i++) {
                addCommand(code.commands.get(i), code.commandDelay.get(i));
            }
        }
        okButton.setY(height - okButton.getHeight());
        okButton.setX((width / 3) * 2 - okButton.getWidth() / 2);
        addButton.setMessage(Text.of(("add command")));
        addButton.setWidth(width / 2);
        addButton.setPosition((width / 2) - addButton.getWidth() / 2, 80);
        deleteButton.setX((width / 3) - okButton.getWidth() / 2);
        deleteButton.setY(height - deleteButton.getHeight());
        updateFields();
    }

    private void addCommand(String command, double delay) {
        TextFieldWidget commandField = new TextFieldWidget(textRenderer, width / 3, 20, Text.of(""));
        commandField.setText(command);
        commandFields.add(commandField);
        DoubleFieldWidget delayField = new DoubleFieldWidget(textRenderer, 40, 20, Text.of(""));
        delayField.setTooltip(Tooltip.of(Text.of("Delay in seconds. Supports '.' (Double type)")));
        delayField.setText(String.valueOf(delay));
        delayFields.add(delayField);
    }

    private void addNewCommand() {
        addCommand("", 1);
        code.commands.add("");
        code.commandDelay.add(1.0);
        updateFields();
    }

    private void removeCommand(int index) {
        code.commands.remove(index);
        code.commandDelay.remove(index);
        remove(commandFields.remove(index));
        remove(delayFields.remove(index));
        updateFields();
    }

    private void updateFields() {
        clearChildren();
        int leftX = width / 9; // Start position for codeTextFields
        int rightX = leftX * 2 + width / 2; // Start position for commandTextFields
        if (addButton != null) {
            if (code.commands.size() >= 10) addButton.active = false;
            else addButton.active = true;
        }
        codeTextField.setX(width / 4);
        codeTextField.setY(60);
        codeTextField.setWidth(width / 2);
        for (int i = 0; i < code.commands.size(); i++) {
            int adaptedHeight = 120 + i * 30;
            int finalI = i;
            addDrawableChild(new ButtonWidget.Builder(Text.of("-"), (button) -> removeCommand(finalI)).position(rightX + width / 6, adaptedHeight).width(width / 12).build());
            commandFields.get(i).setHeight(20);
            commandFields.get(i).setWidth(width / 2);
            commandFields.get(i).setX(leftX);
            commandFields.get(i).setY(adaptedHeight);
            delayFields.get(i).setHeight(20);
            delayFields.get(i).setWidth(40);
            delayFields.get(i).setX(rightX);
            delayFields.get(i).setY(adaptedHeight);
            addDrawableChild(commandFields.get(i));
            addDrawableChild(delayFields.get(i));
        }
        addDrawableChild(codeTextField);
        addDrawableChild(addButton);
        addDrawableChild(deleteButton);
        addDrawableChild(okButton);
    }

    public void saveAndExit() {
        if (codeTextField.forbiddenCode(codeTextField.getText())) {
            new NoticeScreen(() -> MinecraftClient.getInstance().setScreen(this), Text.of(""), Text.of("§cInvalid Code / already used!"));
            return;
        }
        code.code = codeTextField.getText();
        code.commands = new ArrayList<>(commandFields.stream().map(TextFieldWidget::getText).toList());
        code.commandDelay = new ArrayList<>(delayFields.stream().map((field) -> {
                    String text = field.getText();
                    if (text.isEmpty()) return 1.0;
                    return Double.parseDouble(text);
                }
        ).toList());
        codeManager.saveNumCodesToFile();
        setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return BBsentials.config.devMode;
    }

    @Override
    public void close() {
        super.close();
    }

    public void setScreen(Screen screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(screen));
    }
}
