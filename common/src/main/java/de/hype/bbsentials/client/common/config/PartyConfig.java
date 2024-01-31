package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.objects.TrustedPartyMember;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class PartyConfig extends BBsentialsConfig {
    public List<String> partyMembers = new ArrayList<>();
    public boolean allowBBinviteMe = true;
    public List<TrustedPartyMember> trustedPartyMembers = new ArrayList<>();
    public boolean useRecommendedTrustedMembers = false;
    public boolean acceptReparty = true;
    public transient boolean isPartyLeader = false;
    public boolean allowServerPartyInvite = true;
    public transient List<TrustedPartyMember> recommendedTrustedMembers;

    public PartyConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }

    public TrustedPartyMember getTrustedUsername(String username) {
        String uuid = getMcUUIDbyUsername(username);
        if (uuid == null) {
            Chat.sendPrivateMessageToSelfError("Invalid Username → uuid could not be retrieved");
            return null;
        }
        Chat.sendPrivateMessageToSelfImportantInfo("Collecting Data. May take some time");
        for (TrustedPartyMember trustedPartyMember : trustedPartyMembers) {
            if (trustedPartyMember.mcUuid.equals(uuid)) return trustedPartyMember;
        }
        if (useRecommendedTrustedMembers) {
            for (TrustedPartyMember recommendedTrustedMember : recommendedTrustedMembers) {
                if (recommendedTrustedMember.mcUuid.equals(uuid)) return recommendedTrustedMember;
            }
        }
        return null;
    }

    @Override
    public void onInit() {
        recommendedTrustedMembers = new ArrayList<>();
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("4fa1228c8dd647c48fe3b04b580311b8").partyAdmin(true));//Hype_the_Time: Dev
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("09856db6f0214c659ed43a7b3a5f2ca1").partyAdmin(true));//Bossflea Splasher
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("2b2c370c6dc04707931758c99101c6e6").partyAdmin(true));//Aphased big party hoster
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("322eb7ef60a44956a707a22e9630bdb8").partyAdmin(true));//HunterhiHunter
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("ca9b4a62a38b481d94653383ba0d371c").partyAdmin(true));//ooffyy
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("d36c28e4157f407394c385123d8b42cf"));//pois, because troller no admin perms
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("5a4245b07efa45019f162028ba713bb1").partyAdmin(true));//skyrezz bingo Hypixel admin
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("858ee9a1ba6f4ef9a512fd0eb63d0663").partyAdmin(true));//Arithemonkey
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("c1127c0f10cd489c9d0f27c2df3d76f1").partyAdmin(true));//Godwyn

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
}
