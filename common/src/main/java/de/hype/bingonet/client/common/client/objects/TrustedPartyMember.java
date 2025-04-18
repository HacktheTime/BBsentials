package de.hype.bingonet.client.common.client.objects;

import de.hype.bingonet.client.common.chat.Chat;
import de.hype.bingonet.client.common.client.BingoNet;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static de.hype.bingonet.client.common.client.APIUtils.getMcUUIDbyUsername;

public class TrustedPartyMember {
    public String mcUuid;
    //Permissions
    boolean canKick = true;
    boolean canBan = true;
    boolean canInvite = true;
    boolean canMute = false;
    boolean partyAdmin = false;
    boolean canRequestWarp = false;
    boolean canRequestPolls = true;

    private transient String username = null;
    private boolean canKickOffline;

    private TrustedPartyMember(String mcuuid) {
        this.mcUuid = mcuuid.replace("-", "");
    }

    public TrustedPartyMember(String mcuuid, String username) {
        this.mcUuid = mcuuid.replace("-", "");
        this.username = username;
    }

    public boolean isUsernameInitialised() {
        return username != null;
    }
    public static TrustedPartyMember fromUsername(String username) {
        return new TrustedPartyMember(getMcUUIDbyUsername(username), username);
    }

    public static TrustedPartyMember fromUUID(String uuid) {
        return new TrustedPartyMember(uuid);
    }



    private static String getMinecraftUserNameByMCUUID(String uuid) {
        try {
            String url = "https://api.mojang.com/user/profile/" + uuid;
            URL mojangAPI = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mojangAPI.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            // Parse the JSON response
            String[] json = response.toString().split("name\" : ");
            String username = json[1].replace("\"", "").replace("}", "").trim();
            return username;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return the input value if conversion fails
    }

    public TrustedPartyMember register() {
        BingoNet.partyConfig.trustedPartyMembers.add(this);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TrustedPartyMember)) return false;
        return ((TrustedPartyMember) obj).mcUuid.equals(mcUuid);
    }

    public String getUsername() {
        if (username == null && mcUuid != null && !mcUuid.isEmpty()) {
            username = getMinecraftUserNameByMCUUID(mcUuid);
        }
        return username;
    }

    public TrustedPartyMember canKick(boolean value) {
        canKick = value;
        return this;
    }

    public TrustedPartyMember canBan(boolean value) {
        canBan = value;
        return this;
    }

    public TrustedPartyMember canInvite(boolean value) {
        canInvite = value;
        return this;
    }

    public TrustedPartyMember canRequestWarp(boolean value) {
        canRequestWarp = value;
        return this;
    }

    public TrustedPartyMember canMute(boolean value) {
        canMute = value;
        return this;
    }

    public TrustedPartyMember partyAdmin(boolean value) {
        partyAdmin = value;
        return this;
    }

    public TrustedPartyMember canRequestPolls(boolean value) {
        canRequestPolls = value;
        return this;
    }

    public TrustedPartyMember canKickOffline(boolean value) {
        canRequestPolls = value;
        return this;
    }

    public boolean canKick() {
        if (partyAdmin) return true;
        return canKick;
    }

    public boolean canBan() {
        if (partyAdmin) return true;
        return canBan;
    }

    public boolean canInvite() {
        if (partyAdmin) return true;
        return canInvite;
    }

    public boolean canMute() {
        if (partyAdmin) return true;
        return canMute;
    }

    public boolean partyAdmin() {
        if (partyAdmin) return true;
        return partyAdmin;
    }

    public boolean canRequestWarp() {
        if (partyAdmin) return true;
        return canRequestWarp;
    }

    public boolean canRequestPolls() {
        if (partyAdmin) return true;
        return canRequestPolls;
    }

    public void save(String originalUsername, String originalUUID) {
        if (!username.equals(originalUsername)) {
            try {
                mcUuid = getMcUUIDbyUsername(username);
            } catch (Exception e) {
                Chat.sendPrivateMessageToSelfError("Invalid Username: " + username);
            }
        }
        else if (!mcUuid.equals(originalUUID)) {
            try {
                username = getMinecraftUserNameByMCUUID(mcUuid);
            } catch (Exception e) {
                Chat.sendPrivateMessageToSelfError("Invalid mcuuid: " + mcUuid);
            }
        }
    }

    /**
     * DO NOT USE THIS METHOD! This is for special config purposes only
     */
    public void setUsernameOverwrite(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        if (getUsername() != null) return username;
        if (mcUuid != null) return mcUuid;
        return "";
    }

    public boolean canKickOffline() {
        if (partyAdmin) return true;
        return canKickOffline;
    }
}
