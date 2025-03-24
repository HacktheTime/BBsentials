package de.hype.bingonet.fabric.tutorial;

import com.google.gson.annotations.Expose;
import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.fabric.ModInitialiser;
import de.hype.bingonet.fabric.tutorial.nodes.CoordinateNode;
import de.hype.bingonet.shared.constants.Islands;

import java.io.*;
import java.util.*;

public class Tutorial {
    @Expose(serialize = false, deserialize = false)
    public transient static final File TUTORIAL_PATH = new File(EnvironmentCore.utils.getConfigPath(), "tutorials");
    public Integer currentNode = 0;
    public String tutorialName;
    List<AbstractTutorialNode> nodes = new ArrayList<>();
    private transient Set<AbstractTutorialNode> toCheckNodes = null;
    private transient Collection<CoordinateNode> coordinateNodesToRender = null;

    public Tutorial(String name) {
        tutorialName = name;
    }

    public static void saveToFile(Tutorial tutorial, File file) throws IOException {
        file.mkdirs();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(tutorial);
        }
    }

    public static Tutorial loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Tutorial) ois.readObject();
        }
    }

    public AbstractTutorialNode getNextNode() {
        if (ModInitialiser.tutorialManager.recording) {
            if (!nodes.isEmpty()) return nodes.getLast();
            else return null;
        }
        else {
            try {
                if (isCompleted()) return null;
                for (AbstractTutorialNode node : nodes) {
                    if (!node.completed) {
                        return node;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public Set<AbstractTutorialNode> getToCheckNodes() {
        try {
            if (toCheckNodes != null) return toCheckNodes;
            toCheckNodes = new HashSet<>();
            for (AbstractTutorialNode node : nodes) {
                if (node.completed) continue;
                toCheckNodes.add(node);
                if (!node.canBeSkipped) break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return toCheckNodes;
    }

    public boolean isCompleted() {
        return nodes.stream().allMatch(n -> n.completed);
    }

    public synchronized void completeNode(AbstractTutorialNode node) {
        try {
            boolean done = false;
            for (int i = 0; i < nodes.size(); i++) {
                AbstractTutorialNode abstractTutorialNode = nodes.get(i);
                boolean equals = Objects.equals(abstractTutorialNode, node);
                if (!equals && abstractTutorialNode.completed) continue;
                if (!done) abstractTutorialNode.completed = true;
                else {
                    abstractTutorialNode.onPreviousCompleted();
                    if (!abstractTutorialNode.completed) break;
                    done = false;
                    node = abstractTutorialNode;
                }
                if (equals) {
                    done = true;
                }
            }
            toCheckNodes = null;
            coordinateNodesToRender = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CoordinateNode> getCoordinateNodesToRender() {
        try {
            if (coordinateNodesToRender != null) return new ArrayList<>(coordinateNodesToRender);
            coordinateNodesToRender = Collections.synchronizedCollection(new ArrayList<CoordinateNode>());
            Islands island = EnvironmentCore.utils.getCurrentIsland();
            boolean displayAnyway = ModInitialiser.tutorialManager.recording;
            for (AbstractTutorialNode node : nodes) {
                if (node.completed && !displayAnyway) continue;
                if (node instanceof CoordinateNode coordinateNode && coordinateNode.getIsland() == island) {
                    coordinateNodesToRender.add(coordinateNode);
                }
                else return new ArrayList<>(coordinateNodesToRender);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(coordinateNodesToRender);

    }

    public void addNode(AbstractTutorialNode node) {
        nodes.add(node);
        if (node instanceof CoordinateNode coordinateNode) coordinateNodesToRender.add(coordinateNode);
    }

    public void reset(boolean persistent) {
        coordinateNodesToRender = null;
        toCheckNodes = null;
        for (AbstractTutorialNode node : nodes) {
            if (persistent || !node.persistent) node.completed = false;
        }
    }

    public void saveToFile() throws IOException {
        saveToFile(this, getAsFile());
    }

    public File getAsFile() {
        return new File(TUTORIAL_PATH, "%s.bbtutorial".formatted(tutorialName));
    }

    public void deleteLastNode() {
        if (!nodes.isEmpty()) Chat.sendPrivateMessageToSelfSuccess("Deleted Node: %s".formatted(nodes.removeLast()));
    }

    public void resetTravel() {
        toCheckNodes = null;
        coordinateNodesToRender = null;

    }
}
