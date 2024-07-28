package de.hype.bbsentials.client.common.discordintegration;

import de.hype.bbsentials.shared.constants.Formatting;
import de.hype.bbsentials.client.common.chat.Message;
import de.jcm.discordgamesdk.Core;
import de.jcm.discordgamesdk.LobbyManager;
import de.jcm.discordgamesdk.VoiceManager;
import de.jcm.discordgamesdk.lobby.Lobby;
import de.jcm.discordgamesdk.user.DiscordUser;

import java.util.Map;

public class DiscordLobbyUser {
    public final Long id;
    public final String dcUsername;
    public Lobby lobby;
    String mcusername;
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
        String displayName;
        if (mcusername != null) {
            displayName = mcusername;
        }
        else {
            displayName = "(" + dcUsername + ")";
        }
        if (isLocalMuted(core.voiceManager())) {
            if (isTalking) return Message.of(Formatting.DARK_PURPLE + displayName);
            else return Message.of(Formatting.RED + displayName);
        }
        if (isTalking) return Message.of(Formatting.GREEN + displayName);
        else return Message.of(Formatting.GRAY + displayName);
    }

    public boolean isTalking() {
        return isTalking;
    }

    public void updateMetaData(LobbyManager mgn) {
        mcusername = getMetaData(mgn, "hoster");
        if (mcusername.isEmpty()) mcusername = null;
    }
}
