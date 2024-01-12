package de.hype.bbsentials.client.common.client.commands;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.config.ConfigManager;
import de.hype.bbsentials.client.common.config.GeneralConfig;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

public class Commands {
    public Commands() {
        EnvironmentCore.commands.registerMain();
        GeneralConfig configManager = BBsentials.generalConfig;
        if (configManager.bbsentialsRoles != null) {
            EnvironmentCore.commands.registerRoleRequired(
                    configManager.hasBBRoles("dev"),
                    configManager.hasBBRoles("admin"),
                    configManager.hasBBRoles("mod"),
                    configManager.hasBBRoles("splasher"),
                    configManager.hasBBRoles("beta"),
                    configManager.hasBBRoles("mining_events"),
                    configManager.hasBBRoles("mining_events")
                    );
        }
    }
}