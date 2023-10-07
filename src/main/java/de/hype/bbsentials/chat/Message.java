package de.hype.bbsentials.chat;

import de.hype.bbsentials.client.BBsentials;
import net.minecraft.text.Text;

public class Message {
    public Text text;
    private String unformattedString = null;
    private String playerName = null;

    public Message(Text text) {
        this.text = text;
    }

    public Text getText() {
        return text;
    }

    public String getString() {
        return text.getString();
    }

    public String getUnformattedString() {
        if (unformattedString != null) return unformattedString;
        unformattedString = text.getString().replaceAll("§.", "").trim();
        return unformattedString;
    }

    public String getMessageContent() {
        if (isServerMessage()) return unformattedString;
        return getUnformattedString().split(":", 1)[1];
    }

    Boolean guild = null;

    public boolean isFromGuild() {
        if (guild != null) return guild;
        guild = getUnformattedString().startsWith("Guild >");
        return guild;
    }

    Boolean party = null;

    public boolean isFromParty() {
        if (party != null) return party;
        party = getUnformattedString().startsWith("Party >");
        return party;
    }

    Boolean msg = null;

    public boolean isMsg() {
        if (msg != null) return msg;
        msg = getUnformattedString().startsWith("From") || getUnformattedString().startsWith("To");
        return msg;
    }

    Boolean server = null;

    public boolean isServerMessage() {
        if (server != null) return server;
        int space = getUnformattedString().indexOf(" ");
        int doublepoint = getUnformattedString().indexOf(":");
        return ((space + 2 < doublepoint)&&doublepoint!=-1&&space!=-1);
    }

    public String getPlayerName() {
        if (playerName != null) return playerName;
        playerName = getUnformattedString();
        if (!playerName.contains(":")) {
            playerName = "";
            return "";
        }
        playerName = playerName.split(":", 1)[0];
        if (isMsg()) {
            playerName = playerName.replaceFirst("From", "").replace("To", "").trim();
        }
        playerName = playerName.replaceFirst("\\[[^\\]]*\\](?:\\s?\\[[^\\]]*\\])*", "").trim();
        if (playerName.matches("^[a-zA-Z0-9_-]+$")) playerName = "";
        return playerName;
    }

    public void replaceInJson(String replace, String replaceWith) {
        String textString = toJson();
        if (textString.contains(replace)) {
            textString = textString.replaceAll("\\w{8}-\\w{4}-\\w{4}-\\w{4}-\\w{12}", "");
        }
        textString = textString.replace(replace, replaceWith);
        text = Text.Serializer.fromJson(textString);
    }

    public static Message fromJson(String json) {
        return new Message(Text.Serializer.fromJson(json));
    }

    public String toJson() {
        return Text.Serializer.toJson(text);
    }

    public boolean isFromReportedUser() {
        return BBsentials.config.alreadyReported.contains(getPlayerName()) && !getPlayerName().isEmpty();
    }

    public boolean contains(String string) {
        return getUnformattedString().contains(string);
    }

    public boolean startsWith(String string) {
        return getUnformattedString().startsWith(string);
    }
}
