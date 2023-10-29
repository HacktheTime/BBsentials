package de.hype.bbsentials.fabric.numpad;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.BBsentials;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class NumPadCodesConfigScreen extends Screen {
    public List<NumCode> hiddenNumCodes = new ArrayList<>();

    ButtonWidget addButton;
    ButtonWidget okButton;

    ButtonWidget firstPage;
    ButtonWidget lastPage;
    ButtonWidget nextPage;
    ButtonWidget previosPage;
    NumPadCodes codes;
    int page = 0;

    protected NumPadCodesConfigScreen(NumPadCodes codes) {
        super(Text.of("NumPadCodesConfigScreen"));
        this.codes = codes;
    }

    @Override
    protected void init() {
        if (okButton == null) {
            for (NumCode numCode : codes.numCodes) {
                if (numCode.codeIsTransient) {
                    hiddenNumCodes.add(numCode);
                }
            }
            okButton = new ButtonWidget.Builder(Text.of("Done"), button -> done()).build();
            addButton = new ButtonWidget.Builder(Text.of("+"), button -> {
                addNewRow();
            }).build();
            firstPage = new ButtonWidget.Builder(Text.of("First"), button -> setPage(0)).build();
            lastPage = new ButtonWidget.Builder(Text.of("Last"), button -> setPage(-2)).build();
            nextPage = new ButtonWidget.Builder(Text.of("Next"), button -> setPage(page + 1)).build();
            previosPage = new ButtonWidget.Builder(Text.of("Previous"), button -> setPage(page - 1)).build();
        }
        okButton.setY(height - okButton.getHeight());
        okButton.setX((width / 2) - okButton.getWidth() / 2);
        addButton.setMessage(Text.of(("+")));
        addButton.setWidth(width / 2);
        addButton.setPosition((width / 2) - addButton.getWidth() / 2, 20);
        nextPage.setWidth(width / 12);
        previosPage.setWidth(width / 12);
        lastPage.setWidth(width / 12);
        firstPage.setWidth(width / 12);
        nextPage.setX(width / 12);
        previosPage.setX(width / 12);
        lastPage.setX(width / 12);
        firstPage.setX(width / 12);
        firstPage.setPosition(0, 20);
        lastPage.setPosition(0, height - 10 - lastPage.getHeight());
        nextPage.setPosition(0, firstPage.getHeight() + firstPage.getY() + 10);
        previosPage.setPosition(0, lastPage.getY() - lastPage.getHeight() - 10);
        updateFields();

    }

    public void setPage(int newPage) {
        int max = codes.numCodes.size() / ((height - 100) / 30);
        if (newPage < 0) newPage = 0;
        if (newPage > max) newPage = max;
        page = newPage;
        updateFields();
    }

    private void addNewRow() {
        NumCode newCode = new NumCode("", "");
        codes.numCodes.add(newCode);
        updateFields();
    }

    void removeRow(int index) {
        try {
            codes.numCodes.remove(index);
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
        updateFields();
    }

    private void updateFields() {
        int codeX = width / 2 - (width / 6); // Start position for commandTextFields

        clearChildren();
        int min = getMinimumEntry();
        int max = getHighestEntry();
        int skipped = 0;
        for (int i = min; i <= max; i++) {
            if (codes.numCodes.get(i).codeIsTransient) {
                skipped++;
                continue;
            }
            int hight = 60 + (i - getMinimumEntry() - skipped) * 30;
            int finalI = i;
            ButtonWidget removeButton = ButtonWidget.builder(Text.of("-"), button -> removeRow(finalI)).build();

            // Set the positions for commandTextFields
            int finalI1 = i;
            Text buttonText = Text.of(codes.numCodes.get(i).toString());
            ButtonWidget codeButton = ButtonWidget.builder(buttonText, (buttonWidget) -> {
                setScreen(new NumPadCodeConfigScreen(codes, finalI1, this));
            }).build();
            codeButton.setX(codeX);
            codeButton.setY(hight);
            codeButton.setWidth(width / 3);

            // Set positions for removeButtonFields
            removeButton.setWidth(width / 12);
            removeButton.setX(width - width / 6); // Place the remove button to the right
            removeButton.setY(hight);
            addDrawableChild(codeButton);
            addDrawableChild(removeButton);
        }
        addDrawableChild(nextPage);
        addDrawableChild(previosPage);
        addDrawableChild(lastPage);
        addDrawableChild(firstPage);
        addDrawableChild(addButton);
        addDrawableChild(okButton);
    }

    public int getMinimumEntry() {
        int toDisplay = 0;
        int index = -1;
        while ((toDisplay <= entriesPerPage() * page) && index + 1 < codes.numCodes.size()) {
            index++;
            if (!codes.numCodes.get(index).codeIsTransient) {
                toDisplay++;
            }
        }
        return index;
    }

    public int entriesPerPage() {
        return Math.min((height - 100) / 30, codes.numCodes.size());
    }

    public int getHighestEntry() {
        int index = getMinimumEntry() - 1;
        int toDisplay = 0;
        while ((toDisplay <= entriesPerPage()) && index < codes.numCodes.size() - 1) {
            index++;
            if (!codes.numCodes.get(index).codeIsTransient) {
                toDisplay++;
            }
        }
        return index;
    }

    public void done() {
        codes.saveNumCodesToFile();
        close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return BBsentials.config.devMode;
    }

    @Override
    public void close() {
        codes.saveNumCodesToFile();
        super.close();
    }

    public void setScreen(Screen screen) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(screen));
    }

}

