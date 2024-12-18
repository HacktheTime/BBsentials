package de.hype.bbsentials.shared.packets.function;

import com.google.gson.annotations.Expose;
import de.hype.bbsentials.environment.packetconfig.AbstractPacket;
import de.hype.bbsentials.shared.objects.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class CommandChatPromptPacket extends AbstractPacket {
    @Expose(serialize = false, deserialize = false)
    private static final String baseModPrefix = "BBsentials (Client Mod)";
    private final String message;
    private List<CommandRecord> commands;

    protected CommandChatPromptPacket(List<CommandRecord> commands, String message) {
        super(1, 1);
        this.commands = commands;
        this.message = message;
    }

    /**
     * @return The for maliciously checked command list
     */
    public List<CommandRecord> getCommands() {
        commands = commands.stream().filter(v -> !v.isMalicious()).toList();
        return commands;
    }

    public Message getPrintMessage() {
        //{"jformat":8,"jobject":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"BBsentials is a mod that you use"}],"text":"BBsentials Server "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"is suggesting you to execute commands. "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"green","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"show_text","hover_event_value":"","hover_event_children":[],"text":"(Hover for List) "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"To execute them press "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"gold","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"keybind":"Chat Prompt Yes / Open Menu"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"\n"},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"none","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"Attached Message: "},{"bold":false,"italic":false,"underlined":false,"strikethrough":false,"obfuscated":false,"font":null,"color":"dark_gray","insertion":"","click_event_type":"none","click_event_value":"","hover_event_type":"none","hover_event_value":"","hover_event_children":[],"text":"@message"}],"command":"%s","jtemplate":"tellraw"}
        String base = "[{\"text\":\"BBsentials Server \",\"color\":\"gold\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"BBsentials is a mod that you use\"]}},\"is suggesting you to execute commands. \",{\"text\":\"(Hover for List) \",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"contents\":[\"@hover\"]}},\"To execute them press \",{\"keybind\":\"Chat Prompt Yes / Open Menu\",\"color\":\"gold\"},\"\\n\",\"@warnings\",\"Attached Message: \",{\"text\":\"@message\",\"color\":\"dark_gray\"}]";
        String warningString = commands.stream().anyMatch(v -> v.command.startsWith("/warp")) ? "§cThe Command List contains a command that can cause changing Servers!" : "";
        return Message.tellraw(base.replace("@message", StringEscapeUtils.escapeJson(message).replace("@hover", String.join("\n", commands.stream().map(v -> v.command).toList()))).replace("@warnings", warningString));
    }

    public static class CommandRecord {
        public String command;
        public double delay;

        public CommandRecord(String command, double delay) {
            this.command = command;
            this.delay = delay;
        }

        public CommandRecord(String command) {
            this.command = command;
            this.delay = 0;
        }

        /**
         * This method checks whether a command could be malicious.
         */
        private boolean isMalicious() {
            //I decided to allow some commands which are not used by me but that are not malicious.
            if (command.startsWith("/p ")) return false;
            if (command.startsWith("/party ")) return false;
            if (command.startsWith("/boop ")) return false;

            //These commands get a prefix so its an indicator they were send by the mod.
            if (command.startsWith("/msg ")) {
                if (!command.startsWith("/msg %s".formatted(baseModPrefix))) {
                    command = command.replace("/msg ", "/msg %s: ".formatted(baseModPrefix));
                }
                return false;
            }
            if (command.startsWith("/r ")) {
                if (!command.startsWith("/r %s".formatted(baseModPrefix))) {
                    command = command.replace("/r ", "/r %s: ".formatted(baseModPrefix));
                }
                return false;
            }
            if (command.startsWith("/pc ")) {
                if (!command.startsWith("/pc %s".formatted(baseModPrefix))) {
                    command = command.replace("/pc ", "/pc %s: ".formatted(baseModPrefix));
                }
                return false;
            }
            if (command.startsWith("/ac ")) {
                if (!command.startsWith("/ac %s".formatted(baseModPrefix))) {
                    command = command.replace("/ac ", "/ac %s: ".formatted(baseModPrefix));
                }
                return false;
            }
            if (command.startsWith("/gc ")) {
                if (!command.startsWith("/gc %s".formatted(baseModPrefix))) {
                    command = command.replace("/gc ", "/gc %s: ".formatted(baseModPrefix));
                }
                return false;
            }

            //This command prints a extra warning message into chat
            if (command.startsWith("/warp ")) return false;
            return true;
        }
    }
}
