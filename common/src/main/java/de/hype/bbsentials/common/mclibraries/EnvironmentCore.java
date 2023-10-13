package de.hype.bbsentials.common.mclibraries;

public class EnvironmentCore {
    public static BBUtils utils;
    public static Chat chat;
    public static MCUtils mcUtils;

    public EnvironmentCore(BBUtils utils, Chat chat, MCUtils mcUtils) {
        EnvironmentCore.utils = utils;
        EnvironmentCore.chat = chat;
        EnvironmentCore.mcUtils = mcUtils;
    }
}
