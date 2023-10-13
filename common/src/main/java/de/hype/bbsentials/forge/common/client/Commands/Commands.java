package de.hype.bbsentials.forge.common.client.Commands;

import de.hype.bbsentials.forge.common.client.Config;
import de.hype.bbsentials.forge.common.mclibraries.EnvironmentCore;

import static de.hype.bbsentials.forge.common.client.BBsentials.getConfig;
public class Commands {
    public Commands() {
        EnvironmentCore.commands.registerMain();
        Config config = getConfig();
        if (config.bbsentialsRoles != null) {
            EnvironmentCore.commands.registerRoleRequired(
                    config.hasBBRoles("dev"),
                    config.hasBBRoles("admin"),
                    config.hasBBRoles("mod"),
                    config.hasBBRoles("splasher"),
                    config.hasBBRoles("beta"),
                    config.hasBBRoles("mining_events"),
                    config.hasBBRoles("mining_events")
                    );
        }
    }
}