package de.hype.bbsentials.client.common.chat;

import com.google.gson.JsonObject;
import de.hype.bbsentials.client.common.client.BBsentials;
import org.apache.commons.lang3.StringEscapeUtils;

public class Message {
    public boolean actionBar = false;
    Boolean guild = null;
    Boolean party = null;
    Boolean msg = null;
    Boolean server = null;
    private String text;
    private String unformattedString = null;
    private String playerName = null;
    private String string;
    private String unformattedStringJsonEscape = null;
    private String noRanks;

    public Message(String textJson, String string) {
        this.text = textJson;
        if (string == null) string = "";
        this.string = string;
    }

    public Message(String textJson, String string, boolean actionbar) {
        this.text = textJson;
        if (string == null) string = "";
        this.string = string;
        this.actionBar = actionbar;
    }

    public static Message of(String string) {
        JsonObject obj = new JsonObject();
        obj.addProperty("text", string);
        return new Message(obj.toString(), string);
    }

    public static Message tellraw(String json) {
        json = json.replace("@username", BBsentials.generalConfig.getUsername());
        return new Message(json, "");
    }

    //
    public String getJson() {
        return text;
    }

    public String getString() {
        return string;
    }

    public String getUnformattedString() {
        if (unformattedString != null) return unformattedString;
        unformattedString = string.replaceAll("§.", "").trim();
        return unformattedString;
    }

    public String getMessageContent() {
        if (isServerMessage()) return unformattedString;
        return getUnformattedString().split(":", 2)[1].trim();
    }

    public boolean isFromGuild() {
        if (guild != null) return guild;
        guild = getUnformattedString().startsWith("Guild >");
        return guild;
    }

    public boolean isFromParty() {
        if (party != null) return party;
        party = getUnformattedString().startsWith("Party >");
        return party;
    }

    public boolean isMsg() {
        if (msg != null) return msg;
        msg = getUnformattedString().startsWith("From") || getUnformattedString().startsWith("To");
        return msg;
    }

    public boolean isServerMessage() {
        if (server != null) return server;
        return !(isFromParty() || isFromGuild() || isMsg());
    }

    public String getPlayerName() {
        if (playerName != null) return playerName;
        playerName = getUnformattedString();
        if (!playerName.contains(":")) {
            playerName = "";
            return "";
        }
        playerName = playerName.split(":", 2)[0];
        if (isMsg()) {
            playerName = playerName.replaceFirst("From", "").replace("To", "").trim();
        }
        if (playerName.contains(">")) {
            playerName = playerName.split(">", 2)[1];
        }
//        playerName = playerName.replaceFirst("\\[[^\\]]*\\](?:\\s?[^\\x00-\\x7F]+\\s*?\\s?\\[[^\\]]*\\])*", "").trim()// replaces every [] and unicode character before a asci character.
        playerName = playerName.replaceAll("[^\\x00-\\x7F]+\\s*", "").replaceAll("\\[[^\\]]*\\]", "").trim();
        if (playerName.matches("[^a-zA-Z0-9_-]+")) playerName = "";
        return playerName;
    }

    public String getNoRanks() {
        if (noRanks != null) return noRanks;
        return getUnformattedString().replaceAll("[^\\x00-\\x7F]+\\s*", "").replaceAll("\\[[^\\]]*\\]", "").trim().replaceAll("\\s+", " ");
    }

    public void replaceInJson(String replace, String replaceWith) {
        text = text.replaceFirst(replace, StringEscapeUtils.escapeJson(replaceWith));
    }

    public boolean isFromReportedUser() {
        return BBsentials.temporaryConfig.alreadyReported.contains(getPlayerName()) && !getPlayerName().isEmpty();
    }

    public boolean contains(String string) {
        return getUnformattedString().contains(string);
    }

    public boolean startsWith(String string) {
        return getUnformattedString().startsWith(string);
    }

    public boolean endsWith(String string) {
        return getUnformattedString().endsWith(string);
    }

    public void replyToUser(String message) {
        if (isMsg()) BBsentials.sender.addImmediateSendTask("/r " + message);
        else if (isFromParty()) BBsentials.sender.addImmediateSendTask("/pc @" + getPlayerName() + " " + message);
        else if (isServerMessage()) BBsentials.sender.addImmediateSendTask("/ac @" + getPlayerName() + " " + message);
        else if (isFromGuild()) BBsentials.sender.addImmediateSendTask("/gc @" + getPlayerName() + " " + message);
    }

    @Override
    public String toString() {
        return getUnformattedString();
    }
}
