package de.hype.bbsentials.common.chat;

import de.hype.bbsentials.common.client.BBsentials;

public class Message {
    private String text;
    private String unformattedString = null;
    private String playerName = null;
    private String string;
    public boolean actionBar = false;

    public Message(String textJson,String string) {
        this.text = textJson;
        if (string==null) string = "";
        this.string=string;
    }
    public Message(String textJson,String string,boolean actionbar) {
        this.text = textJson;
        if (string==null) string = "";
        this.string=string;
        this.actionBar = actionbar;
    }
    public static Message of(String string){
        return new Message("{\"text\":\""+string+"\"}",string);
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
        return getUnformattedString().split(":", 2)[1];
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
        return ((space + 2 < doublepoint)||doublepoint==-1||space==-1);
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
        if (playerName.contains(">")){
            playerName=playerName.split(">",2)[1];
        }
//        playerName = playerName.replaceFirst("\\[[^\\]]*\\](?:\\s?[^\\x00-\\x7F]?\\s?\\[[^\\]]*\\])*", "").trim()// replaces every [] and unicode character before a asci character.
        playerName = playerName.replaceAll("[^\\x00-\\x7F]","").replaceAll("\\[[^\\]]*\\]","").trim();
        if (playerName.matches("[^a-zA-Z0-9_-]+")) playerName = "";
        return playerName;
    }

    public void replaceInJson(String replace, String replaceWith) {
        text = text.replaceFirst(replace, replaceWith);
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

    @Override
    public String toString() {
        return getUnformattedString();
    }
}
