package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.client.objects.TrustedPartyMember;

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
        for (TrustedPartyMember trustedPartyMember : trustedPartyMembers) {
            if (trustedPartyMember.getUsername().equals(username)) return trustedPartyMember;
        }
        if (useRecommendedTrustedMembers) {
            for (TrustedPartyMember recommendedTrustedMember : recommendedTrustedMembers) {
                if (recommendedTrustedMember.getUsername().equals(username)) return recommendedTrustedMember;
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
    }
}
