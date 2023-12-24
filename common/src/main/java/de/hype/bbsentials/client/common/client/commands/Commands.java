package de.hype.bbsentials.client.common.client.commands;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.client.Config;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

public class Commands {
    public Commands() {
        EnvironmentCore.commands.registerMain();
        Config config = BBsentials.getConfig();
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