package de.hype.bingonet.client.common;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SystemUtils {
    public static SystemType getSystem() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        for (SystemType value : SystemType.values()) {
            if (os.contains(value.osName)) {
                return value;
            }
        }
        return SystemType.LINUX;
    }

    public static void openInBrowser(String url) {
        // More comprehensive URL validation
        if (!url.matches("^https?://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            Chat.sendPrivateMessageToSelfError("Invalid URL format: " + url);
            return;
        }

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            SystemType system = getSystem();

            if (system == SystemType.WINDOOF) {
                processBuilder.command("rundll32", "url.dll,FileProtocolHandler", url);
            } else if (system == SystemType.VERAEPPELT) {
                processBuilder.command("open", url);
            } else {
                processBuilder.command("xdg-open", url);
            }

            processBuilder.start();
        } catch (IOException e) {
            Chat.sendPrivateMessageToSelfError("Error trying to open %s in Browser".formatted(url));
        }
    }

    public enum SystemType {
        WINDOOF("windows"),
        VERAEPPELT("mac"),
        LINUX("linux");

        String osName;

        SystemType(String osName) {
            this.osName = osName;
        }
    }


    public static void setClipboardContent(String text) {
        Process process;
        try {
            switch (getSystem()) {
                case SystemType.WINDOOF:
                    process = new ProcessBuilder("cmd", "/c", "clip").start();
                    break;
                case SystemType.VERAEPPELT:
                    process = new ProcessBuilder("pbcopy").start();
                    break;
                default:
                    process = new ProcessBuilder("xclip", "-selection", "clipboard").start();
                    break;
            }
            try (OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream())) {
                writer.write(text);
                writer.flush();
            }
        } catch (Exception ignored) {
            Chat.sendPrivateMessageToSelfError("Error Occur trying to copy the following Text into the clipboard: \n" + text);
        }

    }

    public static void sendNotification(String title, String text) {
        sendNotification(title, text, 1);
    }

    public static void sendNotification(String title, String text, float volume) {
        BingoNet.executionService.execute(() -> {
            EnvironmentCore.utils.playCustomSound("/sounds/mixkit-sci-fi-confirmation-914.wav", 0);
        });
        List<String> argsList = new ArrayList<>();
        argsList.add("--title");
        argsList.add(title);
        argsList.add("--passivepopup");
        argsList.add(text);
        argsList.add("5");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("kdialog");
            processBuilder.command().addAll(argsList);

            Process process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
