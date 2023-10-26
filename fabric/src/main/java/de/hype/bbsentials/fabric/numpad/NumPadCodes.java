package de.hype.bbsentials.fabric.numpad;

import com.google.common.reflect.TypeToken;
import de.hype.bbsentials.common.api.Formatting;
import de.hype.bbsentials.common.chat.Message;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static de.hype.bbsentials.common.packets.PacketUtils.gson;

public class NumPadCodes {
    private static final int NUMPAD_KEY_COUNT = 10;
    public List<NumCode> numCodes = new ArrayList<>();
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
            numpadKeybindings[i] = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                    String.valueOf(i),
                    keyCode,
                    "bbsentials.numpad"
            ));
        }
        KeyBinding enterKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("Enter", GLFW.GLFW_KEY_KP_ENTER, "bbsentials.numpad"));
        KeyBinding clear = KeyBindingHelper.registerKeyBinding(new KeyBinding("Clear", GLFW.GLFW_KEY_KP_ADD, "bbsentials.numpad"));
        KeyBinding remove = KeyBindingHelper.registerKeyBinding(new KeyBinding("Remove", GLFW.GLFW_KEY_KP_DECIMAL, "bbsentials.numpad"));
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
                    enteredCode = "";
                    keyReleased[10] = false;
                }
                else if (!enterKey.isPressed()) {
                    keyReleased[10] = true;
                }
                if (clear.isPressed() && keyReleased[11]) {
                    enteredCode = "";
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
                    enteredCode = "";
                }
                if (!enteredCode.isEmpty()) {
                    BBsentials.config.overwriteActionBar = getColorCode() + enteredCode;
                    overidedActionBar = true;
                    EnvironmentCore.chat.showActionBar(Message.of(getColorCode() + enteredCode));
                }
                else if (overidedActionBar) {
                    BBsentials.config.overwriteActionBar = "";
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
        defaultCodes.add((new NumCode("042", Formatting.DARK_BLUE, "dev", () -> MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen(new NumPadConfigScreen(this))))));
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
            numCodes.get(index).execute();
        }
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
        try (Writer writer = new FileWriter(new File(EnvironmentCore.mcUtils.getConfigPath(), "BBsentials_Numpad_codes.json"))) {
            gson.toJson(toSaveCodes, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void loadNumCodesFromFile() {
        File file = new File(EnvironmentCore.mcUtils.getConfigPath(), "BBsentials_Numpad_codes.json");
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
}
