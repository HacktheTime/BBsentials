package de.hype.bbsentials.client.common.client.objects;

import de.hype.bbsentials.client.common.client.BBsentials;

public class TrustedPartyMember {
    public String mcUuid;
    //Permissions
    private boolean canKick = true;
    private boolean canBan = true;
    private boolean canInvite = true;
    private boolean canMute = false;
    private boolean partyAdmin = false;
    private boolean canRequestWarp = true;
    private transient String username = null;

    private TrustedPartyMember(String mcuuid) {
        this.mcUuid = mcuuid;
    }

    public TrustedPartyMember(String mcuuid, String username) {
        this.mcUuid = mcuuid;
        this.username = username;
    }

    public static TrustedPartyMember fromUsername(String username) {
        return new TrustedPartyMember(getMcUuidByUsername(username), username);
    }

    public static TrustedPartyMember fromUUID(String uuid) {
        return new TrustedPartyMember(uuid);
    }

    public static String getMcUuidByUsername(String username) {
        //TODO do the request
        return null;
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
            //TODO request usernme via api and set it
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
}
