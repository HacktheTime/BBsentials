package de.hype.bbsentials.forge.common.api;

public enum Formatting{
    BLACK("§0"),
    DARK_BLUE("§1"),
    DARK_GREEN("§2"),
    DARK_AQUA("§3"),
    DARK_RED("§4"),
    DARK_PURPLE("§5"),
    GOLD("§6"),
    GRAY("§7"),
    DARK_GRAY("§8"),
    BLUE("§9"),
    GREEN("§a"),
    AQUA("§b"),
    RED("§c"),
    LIGHT_PURPLE("§d"),
    YELLOW("§e"),
    BOLD("§l"),
    ITALIC("§o"),
    UNDERLINE("§n"),
    STRIKETHROUGH("§m"),
    RESET("§r"),
    WHITE("§f");

    private final String code;

    Formatting(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}
