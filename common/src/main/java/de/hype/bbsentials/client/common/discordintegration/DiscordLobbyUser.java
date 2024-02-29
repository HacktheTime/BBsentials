package de.hype.bbsentials.client.common.discordintegration;

import de.hype.bbsentials.client.common.api.Formatting;
import de.hype.bbsentials.client.common.chat.Message;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.LobbyManager;
import de.jcm.discordgamesdk.VoiceManager;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.util.Map;
import java.util.Objects;

public class DiscordLobbyUser {
    public final Long id;
    public final String dcUsername;
    public Lobby lobby;
    DiscordUser user;
    boolean isTalking;

    public DiscordLobbyUser(DiscordUser user, Lobby lobby) {
        this.user = user;
        id = user.getUserId();
        dcUsername = user.getUsername();
        this.lobby = lobby;
    }

    public void setIsTalking(boolean talking) {
        this.isTalking = talking;
    }

    public Map<String, String> getMetaData(LobbyManager mgn) {
        return mgn.getMemberMetadata(lobby, id);
    }

    public String getMetaData(LobbyManager mgn, String key) {
        String value = mgn.getMemberMetadata(lobby, id).get(key);
        if (value == null) value = "";
        return value;
    }

    public boolean isLocalMuted(VoiceManager mgn) {
        return mgn.isLocalMute(id);
    }

    public Message getAsDisplayName(Core core) {
        if (isLocalMuted(core.voiceManager())) {
            if (isTalking) return Message.of(Formatting.DARK_PURPLE + dcUsername);
            else return Message.of(Formatting.RED + dcUsername);
        }
        if (isTalking) return Message.of(Formatting.GREEN + dcUsername);
        else
            if (Objects.equals(dcUsername, "mininoob46")) return Message.of(Formatting.DARK_AQUA + "Mininoob46 (The Best)");
            else return Message.of(Formatting.GRAY + dcUsername);
    }

    public boolean isTalking() {
        return isTalking;
    }
}
