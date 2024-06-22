package de.hype.bbsentials.fabric;

import de.hype.bbsentials.client.common.chat.Chat;
import dev.xpple.clientarguments.arguments.CTestClassNameArgument;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
        //For Security
        if (url.contains("&") || url.contains("|")) return;
        Runtime rt = Runtime.getRuntime();
        if (getSystem() == SystemType.WINDOOF) {
            try {
                rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
            } catch (IOException e) {
                Chat.sendPrivateMessageToSelfError("Error trying to open %s in Browser".formatted(url));
            }
        }
        else
            try {
                rt.exec("open " + url);
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


    public static void setClipboardContent(String text){
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
        }catch (Exception ignored){
            Chat.sendPrivateMessageToSelfError("Error Occur trying to copy the following Text into the clipboard: \n"+ text);
        }

    }
}
