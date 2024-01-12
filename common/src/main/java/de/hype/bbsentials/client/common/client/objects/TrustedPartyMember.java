package de.hype.bbsentials.client.common.client.objects;

import de.hype.bbsentials.client.common.client.BBsentials;

public class TrustedPartyMember {
    public String username;
    public boolean fullyTrusted = false;

    public TrustedPartyMember(String username) {
        this.username = username;
    }

    public TrustedPartyMember(String username, boolean fullyTrusted) {
        this.fullyTrusted=fullyTrusted;
        this.username = username;
    }

    public TrustedPartyMember register() {
        BBsentials.partyConfig.trustedPartyMembers.add(this);
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TrustedPartyMember)) return false;
        return ((TrustedPartyMember) obj).username.equals(username);
    }
}
