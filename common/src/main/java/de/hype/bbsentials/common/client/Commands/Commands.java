package de.hype.bbsentials.common.client.Commands;

import de.hype.bbsentials.common.client.Config;
import de.hype.bbsentials.common.mclibraries.EnvironmentCore;

import static de.hype.bbsentials.common.client.BBsentials.getConfig;
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