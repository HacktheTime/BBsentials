package de.hype.bingonet.shared.packets.function;

import de.hype.bingonet.environment.packetconfig.AbstractPacket;
import de.hype.bingonet.shared.objects.Message;
import org.apache.commons.text.StringEscapeUtils;

import java.util.List;

public class PacketChatPromptPacket extends AbstractPacket {
    private final String message;
    private List<AbstractPacket> packets;

    public PacketChatPromptPacket(List<AbstractPacket> packets, String message) {
        super(1, 1);
        this.packets = packets;
        this.message = message;
    }

    public List<AbstractPacket> getPackets() {
        return packets;
    }

    public Message getPrintMessage() {
        String base = "[\"\",{\"text\":\"Bingo Net Server: \",\"color\":\"gold\"},\"\\\"%s\\\" \",\"press (\",{\"keybind\":\"Chat Prompt Yes / Open Menu\",\"color\":\"green\"},\") to perform.\"]";
        return Message.tellraw(base.formatted(StringEscapeUtils.escapeJson(message)));
    }
}