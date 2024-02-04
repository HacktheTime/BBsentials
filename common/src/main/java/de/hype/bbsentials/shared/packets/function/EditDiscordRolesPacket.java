package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

import java.util.List;

public class EditDiscordRolesPacket extends AbstractPacket {
    public List<RoleActionData> roles;

    public EditDiscordRolesPacket(List<RoleActionData> roles) {
        super(1,1);
        this.roles = roles;
    }

    public enum Operation {
        ADD,
        REMOVE,
    }

    public enum Role {
        BINGO_GUIDE_PING("Bingo Guide Ping"),
        CARD_REVEAL_PING("Card Reveal Ping"),
        NO_BINGO("No Bingo"),
        NO_DUPE_PING("No Dupe Ping"),
        NO_SPLASH_PING("No Splash Ping"),
        OLDIE("oldie"),
        MOD_MODERN_BETA("1.20 Mod Beta"),
        MOD_MODERN_RELEASE("1.20 Mod Release"),
        MOD_OLD_BETA("1.8.9 Mod Beta"),
        MOD_OLD_RELEASE("1.8.9 Mod Release"),
        ALL_CH_CHEST_ITEM("All ChChest Item"),
        ALL_ROBO_PART("All Robo Part"),
        ALL_BETTER_TOGETHER("All-Better Together"),
        ALL_DOUBLE_POWDER("All-Double Powder"),
        ALL_EVENTS("All-Events"),
        ALL_GONE_WITH_THE_WIND("All-Gone with the Wind"),
        BINGOSHED("Bingoshed"),
        BOUNTY_ACCESS("Bounty Access"),
        CARD_RACE("Card Race"),
        CH_BETTER_TOGETHER("CH-Better Together"),
        CH_DOUBLE_POWDER("CH-Double Powder"),
        CH_EVENTS("CH-Events"),
        CH_GONE_WITH_THE_WIND("CH-Gone with the Wind"),
        CONTROL_SWITCH("Control Switch"),
        CUSTOM_CH_CHEST_ITEM("Custom ChChest Item"),
        DC_ANNOUNCEMENTS("DC Announcements"),
        DW_BETTER_TOGETHER("DW-Better Together"),
        DW_DOUBLE_POWDER("DW-Double Powder"),
        DW_EVENTS("DW-Events"),
        DW_GOBLIN_RAID("DW-Goblin Raid"),
        DW_GONE_WITH_THE_WIND("DW-Gone with the Wind"),
        DW_MITHRIL_GOURMAND("DW-Mithril Gourmand"),
        DW_RAFFLE("DW-Raffle"),
        ELECTRON_TRANSMITTER("Electron Transmitter"),
        FLAWLESS_GEMSTONE("Flawless Gemstone"),
        FTX_3070("FTX 3070"),
        JUNGLE_HEART("Jungle Heart"),
        PICKONIMBUS_2000("Pickonimbus 2000"),
        PREHISTORIC_EGG("Prehistoric Egg"),
        ROBOTRON_REFLECTOR("Robotron Reflector"),
        SUPERLITE_MOTOR("Superlite Motor"),
        SYNTHETIC_HEART("Synthetic Heart"),
        VOTE_PING("Vote Ping");

        String roleName;

        Role(String roleName) {
            this.roleName = roleName;
        }

        public String getRoleName() {
            return roleName;
        }
        /**
         * DO NOT MODIFY / OVERWRITE THIS METHOD! IS A SECURITY RISK IF DONE DIFFERENT → limit roles by only listing allowed but giving by name
         */
        @Override
        public String toString() {
            return super.toString();
        }
    }

    public static class RoleActionData {
        public Role role;
        public Operation action;

        public RoleActionData(Role role, Operation operation) {
            this.role = role;
            this.action = operation;
        }
    }
}
