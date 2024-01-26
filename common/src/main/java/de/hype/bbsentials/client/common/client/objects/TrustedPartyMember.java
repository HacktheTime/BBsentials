package de.hype.bbsentials.client.common.client.objects;

import de.hype.bbsentials.client.common.client.BBsentials;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TrustedPartyMember {
    public String mcUuid;
    //Permissions
    private boolean canKick = true;
    private boolean canBan = true;
    private boolean canInvite = true;
    private boolean canMute = false;
    private boolean partyAdmin = false;
    private boolean canRequestWarp = false;
    private boolean canRequestPolls = true;

    private transient String username = null;

    private TrustedPartyMember(String mcuuid) {
        this.mcUuid = mcuuid;
    }

    public TrustedPartyMember(String mcuuid, String username) {
        this.mcUuid = mcuuid;
        this.username = username;
    }

    public static TrustedPartyMember fromUsername(String username) {
        return new TrustedPartyMember(getMcUUIDbyUsername(username), username);
    }

    public static TrustedPartyMember fromUUID(String uuid) {
        return new TrustedPartyMember(uuid);
    }

    private static String getMcUUIDbyUsername(String username) {
        try {
            String url = "https://api.mojang.com/users/profiles/minecraft/" + username;
            URL mojangAPI = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) mojangAPI.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                reader.close();

                // Parse the JSON response
                String uuid = response.toString().split("\"")[3];
                return uuid;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        BBsentials.partyConfig.trustedPartyMembers.add(this);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TrustedPartyMember)) return false;
        return ((TrustedPartyMember) obj).mcUuid.equals(mcUuid);
    }

    public String getUsername() {
        if (username == null) {
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
        canRequestPolls= value;
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
}
