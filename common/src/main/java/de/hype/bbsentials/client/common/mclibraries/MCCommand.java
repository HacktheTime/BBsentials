package de.hype.bbsentials.client.common.mclibraries;

public interface MCCommand {
    public void registerMain();
    public void registerRoleRequired(boolean hasDev, boolean hasAdmin, boolean hasMod, boolean hasSplasher, boolean hasBeta, boolean hasMiningEvents, boolean hasChChest);
}
