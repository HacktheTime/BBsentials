package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.client.objects.TrustedPartyMember;

import java.util.ArrayList;
import java.util.List;


public class PartyConfig extends BBsentialsConfig {
    public List<String> partyMembers = new ArrayList<>();
    public boolean allowBBinviteMe = true;
    public List<TrustedPartyMember> trustedPartyMembers = new ArrayList<>();
    public boolean acceptReparty=true;
    public transient boolean isPartyLeader=false;
    public boolean allowServerPartyInvite = true;

    public PartyConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
