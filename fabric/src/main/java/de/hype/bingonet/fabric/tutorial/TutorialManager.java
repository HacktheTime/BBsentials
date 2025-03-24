package de.hype.bingonet.fabric.tutorial;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.fabric.tutorial.nodes.*;
import de.hype.bingonet.shared.constants.Islands;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TutorialManager {
    public boolean recording;
    public Tutorial current;
    public ScheduledFuture<?> coordsSaver;
    List<Tutorial> tutorials = new ArrayList<>();

    public TutorialManager() {
        BingoNet.executionService.execute(() -> {
            while (MinecraftClient.getInstance().isFinishedLoading()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {

                }
            }
            BingoNet.executionService.scheduleAtFixedRate(this::checkCoordNodes, 0, 500, TimeUnit.MILLISECONDS);
        });
        Runtime.getRuntime().addShutdownHook(new Thread(this::save));
        loadAll();
    }

    private void save() {
        for (Tutorial tutorial : tutorials) {
            try {
                tutorial.saveToFile();
            } catch (IOException e) {
                System.err.println("Error Occur when trying to save tutorial %s".formatted(tutorial.tutorialName));
                e.printStackTrace();
            }
        }
    }

    private void loadAll() {
        if (Tutorial.TUTORIAL_PATH.exists() && Tutorial.TUTORIAL_PATH.isDirectory()) {
            for (File file : Tutorial.TUTORIAL_PATH.listFiles((dir, name) -> name.endsWith(".bbtuturial"))) {
                if (file.isFile()) {
                    try {
                        try {
                            tutorials.add(Tutorial.loadFromFile(file));
                        } catch (ClassNotFoundException e) {
                            System.err.println("Error Trying to load tutorial: " + file.getName());
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    public void clickedEntity(Entity entity, boolean use) {
        List<Entity> entities = entity.getWorld().getOtherEntities(entity, Box.of(entity.getPos(), 1, 3, 1));
        List<String> entityNames = entities.stream().map(Entity::getCustomName).filter(Objects::nonNull).map(Text::getString).toList();
        String entityName = null;
        if (entityNames.contains("CLICK")) {
            for (String possiblyEntityName : entityNames) {
                if (possiblyEntityName.equals("CLICK")) continue;
                entityName = possiblyEntityName;
            }
        }
        if (entityName == null) return;
        if (!recording) {
            String finalEntityName = entityName;
            current.getToCheckNodes().forEach((node) -> {
                        if (node.completed) return;
                        if (node instanceof NPCInteractionNode npcnode) {
                            if (npcnode.name.equals(finalEntityName) && npcnode.use == use) {
                                current.completeNode(node);
                            }
                        }
                    }
            );
        }
        else {
            current.addNode(new NPCInteractionNode(entityName, entity.getBlockPos(), use));
        }
    }

    private void checkCoordNodes() {
        if (current == null || recording) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        Islands island = EnvironmentCore.utils.getCurrentIsland();
        BlockPos coords = player.getBlockPos();
        if (coords == null) return;
        for (AbstractTutorialNode toCheckNode : current.getToCheckNodes()) {
            if (toCheckNode instanceof CoordinateNode coordinateNode) {
                if (coordinateNode.getPositionBlockPos().isWithinDistance(coords, 5) && coordinateNode.getIsland() == island) {
                    current.completeNode(coordinateNode);
                }
            }
        }
    }

    public void onTravel(Islands destination) {
        if (current == null) return;
        if (recording) {
            if (!current.nodes.isEmpty()) {
                if (current.nodes.getLast() instanceof TravelNode travelNode) {
                    travelNode.island = destination;
                    return;
                }
            }
            current.addNode(new TravelNode(destination));
        }
    }

    public void onTravel(String destination) {
        if (current == null) return;
        if (recording) {
            if (!current.nodes.isEmpty()) {
                if (current.nodes.getLast() instanceof TravelNode travelNode) {
                    travelNode.warpArgument = destination;
                    return;
                }
            }
            current.addNode(new TravelNode(destination));
        }
    }

    public void startRecording(String name) {
        if (recording) {
            Chat.sendPrivateMessageToSelfError("Already Recording the Tutorial");
            return;
        }
        if (EnvironmentCore.utils.getCurrentIsland() == null) {
            Chat.sendPrivateMessageToSelfError("Unknown Island! Are you in Skyblock?");
            return;
        }
        current = new Tutorial(name);
        tutorials.add(current);
        recording = true;
        current.addNode(new TravelNode(EnvironmentCore.utils.getCurrentIsland()));
        coordsSaver = BingoNet.executionService.scheduleAtFixedRate(this::saveCoords, 500, 300, TimeUnit.MILLISECONDS);
    }

    public void stopRecording() {
        recording = false;
        coordsSaver.cancel(false);
    }

    private void saveCoords() {
        BlockPos currentPos = MinecraftClient.getInstance().player.getBlockPos();
        List<AbstractTutorialNode> nodes = current.nodes;
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (!(nodes.get(i) instanceof CoordinateNode coordinateNode)) continue;
            if (coordinateNode.getPositionBlockPos().isWithinDistance(currentPos, 3)) return;
        }
        Islands island = BingoNet.dataStorage.island;
        if (island == null) return;
        for (int i = current.nodes.size() - 1; i >= 0; i--) {
            if (current.nodes.get(i) instanceof CoordinateNode coordinateNode) {
                if (!(coordinateNode.getIsland() == island)) return;
                break;
            }
            if (current.nodes.get(i) instanceof TravelNode travelNode) {
                if (!(travelNode.island == island)) return;
                break;
            }
        }
        current.addNode(new CoordinateNode(currentPos, island));
    }

    public void openedInventory(HandledScreen screen) {
        String title = screen.getTitle().getString();
        if (recording) {
            current.addNode(new OpenScreenNode(title));
            return;
        }
        for (AbstractTutorialNode toCheckNode : current.getToCheckNodes()) {
            if (toCheckNode.completed) continue;
            if (toCheckNode instanceof OpenScreenNode screenNode) {
                if (screenNode.title.equals(title)) current.completeNode(toCheckNode);
            }
            if (toCheckNode instanceof ClickItemNode clickItemNode) {
                clickItemNode.checkAndMarkConditional(screen);
            }
        }

    }

    public void clickedItemInInventory(ItemStack stack, Integer slotId, String title) {
        if (current == null) return;
        if (recording) {
            current.addNode(new ClickItemNode(stack, slotId, title));
            return;
        }
        for (AbstractTutorialNode toCheckNode : current.getToCheckNodes()) {
            if (toCheckNode instanceof ClickItemNode clickItemNode) {
                if (clickItemNode.itemMatches(stack) && Objects.equals(clickItemNode.slot, slotId) && title.equals(clickItemNode.title))
                    current.completeNode(toCheckNode);
            }
        }

    }

    public void obtainItem(ItemStack stack) {
        if (current == null) return;
        for (AbstractTutorialNode toCheckNode : current.getToCheckNodes()) {
            if (toCheckNode instanceof ObtainItemNode obtainItemNode) {
                if (obtainItemNode.check()) {
                    current.completeNode(toCheckNode);
                }
            }
        }

    }

    public void loadTutorial(Tutorial tutorial) {
        current = tutorial;
        tutorial.reset(false);
        tutorial.nodes.get(0).onPreviousCompleted();
    }

    public List<Tutorial> getAllTutorials() {
        return tutorials;
    }

    public void deleteLastNode() {
        if (current == null) return;
        current.deleteLastNode();
    }

    public void skipNode(int count) {
        if (current == null) return;
        int oCount = count;
        for (int i = 0; i < current.nodes.size(); i++) {
            if (current.nodes.get(i).completed) continue;
            current.nodes.get(i).completed = true;
            count--;
            if (count == 0) break;
        }
        Chat.sendPrivateMessageToSelfInfo("Skipped %d Node. This may break the Tutorial.".formatted(oCount - count));
    }

    public void goBackNode(int count) {
        if (current == null) return;
        int index = current.nodes.size() - 1;
        int oCount = count;
        for (int i = 0; i < current.nodes.size(); i++) {
            if (current.nodes.get(i).completed) continue;
            index = i - 1;
        }
        if (index < 0) {
            Chat.sendPrivateMessageToSelfError("No Nodes completed yet");
            return;
        }
        for (int i = index - 1; i >= 0; i--) {
            current.nodes.get(i).completed = false;
            count--;
            if (count == 0) break;
        }
        Chat.sendPrivateMessageToSelfInfo("Went back %d Node. use skip to skip nodes if not obtainable again.".formatted(oCount - count));

    }
}

