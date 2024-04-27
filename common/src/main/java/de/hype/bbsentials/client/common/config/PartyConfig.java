package de.hype.bbsentials.client.common.config;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import de.hype.bbsentials.client.common.chat.Chat;
import de.hype.bbsentials.client.common.client.objects.TrustedPartyMember;

import java.util.ArrayList;
import java.util.List;

import static de.hype.bbsentials.client.common.client.APIUtils.getMcUUIDbyUsername;


public class PartyConfig extends BBsentialsConfig {
    public transient List<String> partyMembers = new ArrayList<>();
    public boolean allowBBinviteMe = true;
    public List<TrustedPartyMember> trustedPartyMembers = new ArrayList<>();
    public boolean useRecommendedTrustedMembers = false;
    public boolean acceptReparty = true;
    public transient boolean isPartyLeader = false;
    public boolean allowServerPartyInvite = true;
    public transient List<TrustedPartyMember> recommendedTrustedMembers;
    public boolean announceRemoteMsgPartyCommands = true;

    public PartyConfig() {
        super(1);
        doInit();
    }

    public TrustedPartyMember getTrustedUsername(String username) {
        String uuid = getMcUUIDbyUsername(username);
        if (uuid == null) {
            Chat.sendPrivateMessageToSelfError("Invalid Username → uuid could not be retrieved");
            return null;
        }
        Chat.sendPrivateMessageToSelfImportantInfo("Collecting Trusted Party Member Data. May take some time");
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
        List<LinkedTreeMap<String, Object>> map = (List<LinkedTreeMap<String, Object>>) (Object) trustedPartyMembers;
        List<TrustedPartyMember> members = new ArrayList<>();
        for (LinkedTreeMap<String, Object> linkedTreeMap : map) {
            members.add(new Gson().fromJson(new Gson().toJson(linkedTreeMap), TrustedPartyMember.class));
        }
        trustedPartyMembers = members;

        recommendedTrustedMembers = new ArrayList<>();
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("4fa1228c8dd647c48fe3b04b580311b8").partyAdmin(true));//Hype_the_Time: Dev
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("09856db6f0214c659ed43a7b3a5f2ca1").partyAdmin(true));//Bossflea Splasher
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("2b2c370c6dc04707931758c99101c6e6").partyAdmin(true));//Aphased big party hoster
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("322eb7ef60a44956a707a22e9630bdb8").partyAdmin(true));//HunterhiHunter
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("ca9b4a62a38b481d94653383ba0d371c").partyAdmin(true));//ooffyy
//        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("d36c28e4157f407394c385123d8b42cf"));//pois, because troller no admin perms
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("5a4245b07efa45019f162028ba713bb1").partyAdmin(true));//skyrezz bingo Hypixel admin
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("858ee9a1ba6f4ef9a512fd0eb63d0663").partyAdmin(true));//Arithemonkey
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("c1127c0f10cd489c9d0f27c2df3d76f1").partyAdmin(true));//Godwyn
        recommendedTrustedMembers.add(TrustedPartyMember.fromUUID("dcf0437f120e45c799f8b6137d556d8b").partyAdmin(true));//ZerosTulip

    }

}
