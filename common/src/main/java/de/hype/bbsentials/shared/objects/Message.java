package de.hype.bbsentials.shared.objects;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.function.Predicate;

import static de.hype.bbsentials.shared.objects.Message.MessageSource.*;


public abstract class Message {
    public boolean actionBar;
    public MessageSource source = null;
    protected String text;
    protected String unformattedString = null;
    protected String playerName;
    protected String string;
    protected String unformattedStringJsonEscape = null;
    protected String noRanks;
    protected Boolean isFromSelf;
    protected String selfUserName;

    public Message(String textJson, String string) {
        this(textJson, string, false);
    }

    public Message(String textJson, String string, boolean actionbar) {
        selfUserName = getSelfUsername();
        this.text = textJson;
        if (string == null) string = "";
        this.string = string;
        this.actionBar = actionbar;
        if (actionbar) this.source = MessageSource.SERVER;
        else this.source = getMessageSource(this);
        if (source == MessageSource.PRIVATE_MESSAGE_SENT) isFromSelf = true;
    }

    public static Message of(String string) {
        JsonObject obj = new JsonObject();
        obj.addProperty("text", string);
        Message message = new Message(obj.toString(), string) {
            @Override
            public String getSelfUsername() {
                return null;
            }
        };
        message.source = SELFCREATED;
        return message;
    }

    public static Message tellraw(String json) {
        Message message = new Message(json, "") {
            @Override
            public String getSelfUsername() {
                return null;
            }
        };
        message.source = SELFCREATED;
        return message;
    }

    public abstract String getSelfUsername();

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
        if (source == SERVER) return getUnformattedString();
        return getUnformattedString().split(":", 2)[1].trim();
    }

    public String getPlayerName() {
        if (playerName != null) return playerName;
        playerName = getUnformattedString();
        if (!playerName.contains(":")) {
            playerName = "";
            return "";
        }
        playerName = playerName.split(":", 2)[0];
        if (playerName.startsWith("From") || playerName.startsWith("To")) {
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

    public boolean contains(String string) {
        return getUnformattedString().contains(string);
    }

    public boolean startsWith(String string) {
        return getUnformattedString().startsWith(string);
    }

    public boolean endsWith(String string) {
        return getUnformattedString().endsWith(string);
    }

    @Override
    public String toString() {
        return getUnformattedString();
    }

    public Boolean isFromSelf() {
        if (isFromSelf == null) isFromSelf = getPlayerName().equals(selfUserName);
        return isFromSelf;
    }

    public boolean isFromParty() {
        return source == PARTY_CHAT;
    }

    public boolean isFromGuild() {
        return source == GUILD_CHAT || source == OFFICER_CHAT;
    }

    public boolean isServerMessage() {
        return source == SERVER;
    }

    /**
     * returns true if message is a RECEIVING msg! for either type use {@link #isAnyMsg()}
     */
    public boolean isMsg() {
        return source == PRIVATE_MESSAGE_RECEIVE;
    }

    public boolean isAnyMsg() {
        return source == PRIVATE_MESSAGE_RECEIVE;
    }

    public boolean isActionBar() {
        return actionBar;
    }

    public enum MessageSource {
        COOP("Coop Chat", s -> s.startsWith("Co-op >"), "/cc @"),
        GUILD_CHAT("Guild Chat", s -> s.startsWith("Guild >"), "/gc @"),
        OFFICER_CHAT("Guild Officer Chat", s -> s.startsWith("Officer >"), "/oc @"),
        PARTY_CHAT("Party Chat", s -> s.startsWith("Party >"), "/pc @"),
        PRIVATE_MESSAGE_RECEIVE("Private Message", s -> s.startsWith("From "), "/msg "),
        PRIVATE_MESSAGE_SENT("Private Message", s -> s.startsWith("To "), null),
        NPC("Server Chat (NPC)", s -> s.startsWith("[NPC]"), null),
        ALL_CHAT("All Chat", null, "@"),
        SERVER("Server Message", null, "@"),
        SELFCREATED("Generated by Client Code", null, null),

        ;
        public final String sourceName;
        public final String replyCommandStart;
        public final Predicate<String> detection;

        MessageSource(String sourceName, Predicate<String> detection, String replyCommandStart) {
            this.sourceName = sourceName;
            this.replyCommandStart = replyCommandStart;
            this.detection = detection;
        }

        public static MessageSource getMessageSource(Message message) {
            String string = message.getUnformattedString();
            for (MessageSource value : MessageSource.values()) {
                if (value.detection.test(string)) return value;
            }
            if (message.getPlayerName() != null) return ALL_CHAT;
            return SERVER;

        }

    }
}
