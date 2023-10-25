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

    ButtonWidget firstPage;
    ButtonWidget lastPage;
    ButtonWidget nextPage;
    ButtonWidget previosPage;
    NumPadCodes codes;
    int page = 0;
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
                    firstPage = new ButtonWidget.Builder(Text.of("First"), button -> setPage(0)).build();
                    lastPage = new ButtonWidget.Builder(Text.of("Last"), button -> setPage(-2)).build();
                    nextPage = new ButtonWidget.Builder(Text.of("Next"), button -> setPage(page + 1)).build();
                    previosPage = new ButtonWidget.Builder(Text.of("Previous"), button -> setPage(page - 1)).build();
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

    public void setPage(int newPage) {
        int max = commandTextFields.size() / ((height - 100) / 30);
        if (newPage < 0) newPage = 0;
        if (newPage > max) newPage = max;
        page = newPage;
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
            TextFieldWidget removedCodeField = codeTextFields.remove(i - 2);
            TextFieldWidget removedCommandField = commandTextFields.remove(i - 2);
            ButtonWidget removeButton = removeButtonFields.remove(i - 2);
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

        for (int i = getMinimumEntry(); i < getHighestEntry(); i++) {
            int hight = 60 + (i - getMinimumEntry()) * 30;

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
            firstPage.setPosition(0, 20);
            lastPage.setPosition(0, height - 10 - lastPage.getHeight());
            nextPage.setPosition(0, firstPage.getHeight() + firstPage.getY() + 10);
            previosPage.setPosition(0, lastPage.getY() - lastPage.getHeight() - 10);
            nextPage.setWidth(width / 12);
            previosPage.setWidth(width / 12);
            lastPage.setWidth(width / 12);
            firstPage.setWidth(width / 12);
            addDrawableChild(codeTextFields.get(i));
            addDrawableChild(commandTextFields.get(i));
            addDrawableChild(removeButtonFields.get(i));
            addDrawableChild(nextPage);
            addDrawableChild(previosPage);
            addDrawableChild(lastPage);
            addDrawableChild(firstPage);
        }

        addDrawableChild(addButton);
        addDrawableChild(okButton);
    }

    public int getMinimumEntry() {
        return Math.min(entriesPerPage() * page, commandTextFields.size());
    }

    public int entriesPerPage() {
        return Math.min((height - 100) / 30, commandTextFields.size());
    }

    public int getHighestEntry() {
        int max = Math.min(entriesPerPage() * (page + 1) - 1, commandTextFields.size());
        return max;
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

