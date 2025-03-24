package de.hype.bingonet.client.common.client.commands;

import de.hype.bingonet.client.common.client.BingoNet;
import de.hype.bingonet.client.common.config.ConfigManager;
import de.hype.bingonet.client.common.config.GeneralConfig;
import de.hype.bingonet.client.common.mclibraries.EnvironmentCore;
import de.hype.bingonet.shared.objects.BBRole;

public class Commands {
    public Commands() {
        EnvironmentCore.commands.registerMain();
        GeneralConfig configManager = BingoNet.generalConfig;
        if (configManager.bingonetRoles != null) {
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