package de.hype.bbsentials.fabric.screens;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.objects.WaypointRoute;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RoutesConfigScreen extends Screen {
    public List<WaypointRoute> routes;
    ButtonWidget addButton;
    ButtonWidget okButton;
    ButtonWidget firstPage;
    ButtonWidget lastPage;
    ButtonWidget nextPage;
    ButtonWidget previosPage;
    Screen parent;
    int page = 0;

    public RoutesConfigScreen(Screen parent) {
        super(Text.of("Routes"));
        this.routes = getRoutes();
        this.parent = parent;
//        setScreen(this);
    }

    public static List<WaypointRoute> getRoutes() {
        List<WaypointRoute> routes = new ArrayList<>();
        try {
            for (File file : WaypointRoute.waypointRouteDirectory.listFiles()) {
                try {

                } catch (Exception ignored) {
                    if (file.getName().endsWith(".json")) {
                        routes.add(WaypointRoute.loadFromFile(file));
                    }
                }
            }
        } catch (Exception ignored) {
        }
        return routes;
    }

    @Override
    protected void init() {
        if (okButton == null) {
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
        int max = routes.size() / ((height - 100) / 30);
        if (newPage < 0) newPage = 0;
        if (newPage > max) newPage = max;
        page = newPage;
        updateFields();
    }

    private void addNewRow() {
        BlockPos pos = null;
        try {
            pos = MinecraftClient.getInstance().player.getBlockPos();
        } catch (Exception ignored) {
        }
        if (pos == null) {
            pos = new BlockPos(0, 100, 0);
        }
        routes.add(new WaypointRoute(null, new ArrayList<>()));
        updateFields();
    }

    void removeRow(WaypointRoute node) {
        try {
            routes.remove(node);
        } catch (Exception e) {
            Chat.sendPrivateMessageToSelfError(e.getMessage());
        }
        updateFields();
    }

    private void updateFields() {
        int codeX = width / 2 - (width / 6); // Start position for commandTextFields

        clearChildren();
        int count = 0;
        routes.forEach((route) -> {
            int hight = 60 + count * 30;

            ButtonWidget removeButton = ButtonWidget.builder(Text.of("-"), button -> removeRow(route)).build();

            // Set the positions for commandTextFields
            Text buttonText = Text.of(route.toString());
            ButtonWidget codeButton = ButtonWidget.builder(buttonText, (buttonWidget) -> {
                setScreen(new RouteConfigScreen(this, route));
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
        });
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
        while ((toDisplay <= entriesPerPage() * page) && index + 1 < routes.size()) {
            index++;
            toDisplay++;

        }
        return index;
    }

    public int entriesPerPage() {
        return Math.min((height - 100) / 30, routes.size());
    }

    public int getHighestEntry() {
        int index = getMinimumEntry() - 1;
        int toDisplay = 0;
        while ((toDisplay <= entriesPerPage()) && index < routes.size() - 1) {
            index++;
            toDisplay++;
        }
        return index;
    }

    public void done() {
        close();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        setScreen(parent);
        return true;
    }

    @Override
    public void close() {
        setScreen(parent);
        super.close();
    }

    public void setScreen(Screen screen) {
        if (screen == null) return;
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> client.setScreen(screen));
    }
}

