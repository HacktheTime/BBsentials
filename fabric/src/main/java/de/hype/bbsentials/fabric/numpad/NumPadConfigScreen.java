package de.hype.bbsentials.fabric.numpad;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class NumPadConfigScreen extends Screen {
    public List<NumCode> hiddenNumCodes = new ArrayList<>();
    ButtonWidget addButton;
    ButtonWidget okButton;
    NumPadCodes codes;
    private List<NumCodeInputField> codeTextFields;
    private List<TextFieldWidget> commandTextFields;
    private List<ButtonWidget> removeButtonFields;

    protected NumPadConfigScreen(NumPadCodes codes) {
        super(Text.of("NumPadConfigScreen"));
        codeTextFields = new ArrayList<>();
        commandTextFields = new ArrayList<>();
        removeButtonFields = new ArrayList<>();
        this.codes = codes;
    }

    @Override
    protected void init() {
        if (okButton == null) {
            for (NumCode numCode : codes.numCodes) {
                if (!numCode.codeIsTransient) {
                    addRow(numCode.code, numCode.command);
                    okButton = new ButtonWidget.Builder(Text.of("Done"), button -> done()).build();
                    addButton = new ButtonWidget.Builder(Text.of("+"), button -> {
                        addNewRow();
                    }).build();
                }
                else {
                    hiddenNumCodes.add(numCode);
                }
            }
        }
        okButton.setY(height - okButton.getHeight());
        okButton.setX((width / 2) - okButton.getWidth() / 2);
        addButton.setMessage(Text.of(("+")));
        addButton.setWidth(width / 2);
        addButton.setPosition((width / 2) - addButton.getWidth() / 2, 20);
        codeTextFields.forEach((this::addDrawableChild));
        commandTextFields.forEach((this::addDrawableChild));
        removeButtonFields.forEach((this::addDrawableChild));
        updateFields();

    }

    private void addRow(String code, String command) {
        NumCodeInputField textField2 = new NumCodeInputField(textRenderer, width / 3, 20, Text.of(("")), this);
        textField2.setText(code);
        codeTextFields.add(textField2);
        TextFieldWidget textField = new TextFieldWidget(textRenderer, width / 3, 20, Text.of(("")));
        textField.setText(command);
        commandTextFields.add(textField);
        int finalRemoveId = commandTextFields.size();
        ButtonWidget removeButton = ButtonWidget.builder(Text.of("-"), button -> removeRow(finalRemoveId)).build();
        removeButtonFields.add(removeButton);
    }

    private void addNewRow() {
        addRow("", "");
        updateFields();
    }

    private void removeRow(int i) {
        if (!codeTextFields.isEmpty()) {
            TextFieldWidget removedCodeField = codeTextFields.remove(i - 1);
            TextFieldWidget removedCommandField = commandTextFields.remove(i - 1);
            ButtonWidget removeButton = removeButtonFields.remove(i - 1);
            remove(removeButton);
            remove(removedCodeField);
            remove(removedCommandField);
            updateFields();
        }
    }

    private void updateFields() {
        int leftX = width / 9; // Start position for codeTextFields
        int rightX = leftX * 2 + width / 3; // Start position for commandTextFields

        clearChildren();

        for (int i = 0; i < codeTextFields.size(); i++) {
            int hight = 60 + i * 30;

            // Set the positions for codeTextFields
            codeTextFields.get(i).setX(leftX);
            codeTextFields.get(i).setY(hight);
            codeTextFields.get(i).setWidth(width / 3);

            // Set the positions for commandTextFields
            commandTextFields.get(i).setX(rightX);
            commandTextFields.get(i).setY(hight);
            commandTextFields.get(i).setWidth(width / 3);

            // Set positions for removeButtonFields
            removeButtonFields.get(i).setWidth(width / 12);
            removeButtonFields.get(i).setX(rightX + width / 3 + width / 18 - removeButtonFields.get(i).getWidth() / 2); // Place the remove button to the right
            removeButtonFields.get(i).setY(hight);

            addDrawableChild(codeTextFields.get(i));
            addDrawableChild(commandTextFields.get(i));
            addDrawableChild(removeButtonFields.get(i));
        }

        addDrawableChild(addButton);
        addDrawableChild(okButton);
    }

    public void updateCodes() {
        List<NumCode> newCodes = new ArrayList<>(hiddenNumCodes);
        for (int i = 0; i < commandTextFields.size(); i++) {
            newCodes.add(new NumCode(codeTextFields.get(i).getText(), commandTextFields.get(i).getText()));
        }
        codes.numCodes = newCodes;
    }

    public void done() {
        codes.saveNumCodesToFile();
        close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void close() {
        updateCodes();
        codes.saveNumCodesToFile();
        super.close();
    }


}

