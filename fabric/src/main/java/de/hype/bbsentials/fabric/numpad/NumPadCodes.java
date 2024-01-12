package de.hype.bbsentials.fabric.numpad;

import com.google.common.reflect.TypeToken;
import de.hype.bbsentials.client.common.api.Formatting;
import de.hype.bbsentials.client.common.chat.Message;
import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.fabric.DebugThread;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static de.hype.bbsentials.environment.packetconfig.PacketUtils.gson;

public class NumPadCodes {
    private static final int NUMPAD_KEY_COUNT = 10;
    public List<NumCode> numCodes = new ArrayList<>();
    int enterPresses = 0;
    private KeyBinding[] numpadKeybindings = new KeyBinding[NUMPAD_KEY_COUNT];
    private String enteredCode = "";
    private boolean[] keyReleased = new boolean[NUMPAD_KEY_COUNT + 3];
    private long lastKeyPressTime = 0;
    private boolean overidedActionBar = false;

    public NumPadCodes() {
        loadNumCodesFromFile();
        if (numCodes == null) {
            numCodes = new ArrayList<>();
        }
        if (numCodes.isEmpty()) {
            addDefaultCodes(true);
        }
        else {
            addDefaultCodes(false);
        }
        for (int i = 0; i < NUMPAD_KEY_COUNT; i++) {
            int keyCode = GLFW.GLFW_KEY_KP_0 + i;
            numpadKeybindings[i] = new KeyBinding(
                    String.valueOf(i),
                    keyCode,
                    "bbsentials.numpad"
            );
        }
        KeyBinding enterKey = new KeyBinding("Enter", GLFW.GLFW_KEY_KP_ENTER, "bbsentials.numpad");
        KeyBinding clear = new KeyBinding("Clear", GLFW.GLFW_KEY_KP_ADD, "bbsentials.numpad");
        KeyBinding remove = new KeyBinding("Remove", GLFW.GLFW_KEY_KP_DECIMAL, "bbsentials.numpad");
        Thread t = new Thread(() -> {
            while (true) {
                for (int i = 0; i < numpadKeybindings.length; i++) {
                    if (numpadKeybindings[i].isPressed() && keyReleased[i]) {
                        enteredCode = enteredCode + i;
                        keyReleased[i] = false; // Mark key as not released
                        lastKeyPressTime = System.currentTimeMillis();
                        break;
                    }
                    else if (!numpadKeybindings[i].isPressed()) {
                        keyReleased[i] = true; // Mark key as released
                    }
                }
                if (enterKey.isPressed() && keyReleased[10]) {
                    executeCode();
                    keyReleased[10] = false;
                }
                else if (!enterKey.isPressed()) {
                    keyReleased[10] = true;
                }
                if (clear.isPressed() && keyReleased[11]) {
                    resetCode();
                    keyReleased[11] = false;
                }
                else if (!clear.isPressed()) {
                    keyReleased[11] = true;
                }
                if (remove.isPressed() && keyReleased[12]) {
                    if (!enteredCode.isEmpty()) {
                        enteredCode = enteredCode.substring(0, enteredCode.length() - 1);
                        keyReleased[12] = false;
                    }
                }
                else if (!remove.isPressed()) {
                    keyReleased[12] = true;
                }
                // Reset key cooldowns after 5 seconds
                if (System.currentTimeMillis() - lastKeyPressTime >= 5000) {
                    resetCode();
                }
                if (!enteredCode.isEmpty()) {
                    String actionbarText = getColorCode() + enteredCode;
                    if (enterPresses > 0) {
                        int count = getMorePressesNeeded();
                        if (count != 0) {
                            actionbarText = Formatting.GOLD + enteredCode + " (" + count + ")";
                        }
                    }
                    BBsentials.funConfig.overwriteActionBar = actionbarText;
                    overidedActionBar = true;
                    EnvironmentCore.chat.showActionBar(Message.of(actionbarText));
                }
                else if (overidedActionBar) {
                    BBsentials.funConfig.overwriteActionBar = "";
                    overidedActionBar = false;
                    EnvironmentCore.chat.showActionBar(Message.of(""));
                }

                try {
                    Thread.sleep(50);
                } catch (Exception ignored) {
                }
            }
        });
        t.start();
    }

    public void addDefaultCodes(boolean all) {
        List<NumCode> defaultCodes = new ArrayList();
        defaultCodes.add((new NumCode("042", Formatting.DARK_BLUE, "", () -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NumPadCodesConfigScreen(this))))));
        defaultCodes.add((new NumCode("0", Formatting.DARK_BLUE, "dev", () -> BBsentials.executionService.execute(() -> ((DebugThread) EnvironmentCore.debug).onNumpadCode()))));
        defaultCodes.add((new NumCode("11", "/l")));
        if (all) {
            numCodes.addAll(defaultCodes);
            return;
        }
        for (int i = 0; i < defaultCodes.size(); i++) {
            if (defaultCodes.get(i).codeIsTransient) {
                numCodes.add(defaultCodes.get(i));
            }
        }
    }

    public String getColorCode() {
        int index = getCodeIndex();
        boolean exists = codeExists();
        boolean devCode = enteredCode.startsWith("0");
        if (devCode) {
            if (index != -1 && !exists) {
                return Formatting.DARK_BLUE.toString();
            }
            else if (index != -1) {
                return Formatting.DARK_AQUA.toString();
            }
            else if (exists) {
                return Formatting.AQUA.toString();
            }
        }
        else {
            if (index != -1 && !exists) {
                return Formatting.DARK_GREEN.toString();
            }
            else if (index != -1) {
                return Formatting.GREEN.toString();
            }
            else if (exists) {
                return Formatting.YELLOW.toString();
            }
        }
        return Formatting.RED.toString();
    }

    public void executeCode() {
        int index = getCodeIndex();
        if (index != -1) {
            if (getMorePressesNeeded() <= 1) {
                numCodes.get(index).execute();
                resetCode();
            }
            else {
                enterPresses++;
            }
        }
    }

    public int getMorePressesNeeded() {
        int index = getCodeIndex();
        if (index == -1) return 0;
        int extraInputNeeded = numCodes.get(index).pressCount() - enterPresses;
        if (extraInputNeeded < 0) {
            return 0;
        }
        return extraInputNeeded;
    }

    public int getCodeIndex() {
        for (int i = 0; i < numCodes.size(); i++) {
            if (numCodes.get(i).code.equals(enteredCode)) return i;
        }
        return -1;
    }


    /**
     * @return returns true if there is at least 1 code to go on. otherwise false
     */
    private boolean codeExists() {
        for (int i1 = 0; i1 < numCodes.size(); i1++) {
            if (numCodes.get(i1).code.startsWith(enteredCode) && !numCodes.get(i1).code.equals(enteredCode)) {
                return true;
            }
        }
        return false;
    }

    public void saveNumCodesToFile() {
        List<NumCode> toSaveCodes = new ArrayList<>();
        for (NumCode numCode : numCodes) {
            if (!numCode.codeIsTransient) {
                toSaveCodes.add(numCode);
            }
        }
        try (Writer writer = new FileWriter(new File(EnvironmentCore.utils.getConfigPath(), "BBsentials_Numpad_codes.json"))) {
            gson.toJson(toSaveCodes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadNumCodesFromFile() {
        File file = new File(EnvironmentCore.utils.getConfigPath(), "BBsentials_Numpad_codes.json");
        try (Reader reader = new FileReader(file)) {
            Type listType = new TypeToken<List<NumCode>>() {
            }.getType();
            numCodes = gson.fromJson(reader, listType);
        } catch (IOException e) {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                    loadNumCodesFromFile();
                } catch (IOException ex) {
                    System.out.println("Couldnt create new file");
                    e.printStackTrace();
                }
            }
            else {
                e.printStackTrace();
            }
        }
    }

    void resetCode() {
        enteredCode = "";
        enterPresses = 0;
    }
}
