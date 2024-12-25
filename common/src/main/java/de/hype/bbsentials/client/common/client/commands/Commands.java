package de.hype.bbsentials.client.common.client.commands;

import de.hype.bbsentials.client.common.client.BBsentials;
import de.hype.bbsentials.client.common.config.ConfigManager;
import de.hype.bbsentials.client.common.config.GeneralConfig;
import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;
import de.hype.bbsentials.shared.objects.BBRole;

public class Commands {
    public Commands() {
        EnvironmentCore.commands.registerMain();
        GeneralConfig configManager = BBsentials.generalConfig;
        if (configManager.bbsentialsRoles != null) {
            EnvironmentCore.commands.registerRoleRequired(
                    configManager.hasBBRoles(BBRole.DEVELOPER),
                    configManager.hasBBRoles(BBRole.ADMIN),
                    configManager.hasBBRoles(BBRole.MODERATOR),
                    configManager.hasBBRoles(BBRole.SPLASHER),
                    configManager.hasBBRoles(BBRole.BETA_TESTER),
                    configManager.hasBBRoles(BBRole.MINING_EVENT_ANNOUNCE_PERM),
                    configManager.hasBBRoles(BBRole.CHCHEST_ANNOUNCE_PERM)
                    );
        }
    }
}