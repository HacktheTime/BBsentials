package de.hype.bbsentials.shared.constants;

import de.hype.bbsentials.environment.packetconfig.TradeTypeEnvironmentRegistry;

import java.util.function.IntUnaryOperator;

public enum TradeType {
    ENCHANT_ITEMS("Item Enchanting", "Trading Grands to enchant Items.", "Have at least 2 Stacks of Grands available.", 1, TradeTypeEnvironmentRegistry.ENCHANT_ITEMS),
    DAILY_MAX_ENCHANTING("Daily Enchanting XP", "Splashing Grands to get 500k Enchanting XP.", "Have Enchanting XP Boost 3 Potion effect (on Bingo) and at least 6 Stacks of Grands available (on Carry profile)", 1, TradeTypeEnvironmentRegistry.DAILY_MAX_ENCHANTING),
    OPEN_VIKING("Open Viking", "Open Viking to obtain Raiders Axe", "Have at Magical Water Bucket, Fish Hat and 1 Raw Fish available.", -1, TradeTypeEnvironmentRegistry.OPEN_VIKING),
    KABOOM_ASSISTANCE("Kaboom Assistance", "Support for Kaboom Community Goal. Carrier Tanks for you.", "Survive Mini Bosses easily.", 1, TradeTypeEnvironmentRegistry.KABOOM_ASSISTANCE),
    AUTO_SLAYER_UNLOCK("Auto Slayer Unlock", "Carrier kills the Bosses for you. Warning Level 6 Slayers takes multiple Hours!", "Kill Zombie and Tara in under 30 Seconds and Wolf in under 90 Seconds", 2, TradeTypeEnvironmentRegistry.AUTO_SLAYER_UNLOCK),
    CARRY_ENTRANCE_TO_F3_COMPLETION("Entrance → F3", "Entrance to F3 Carry.", "Have Combat 15 on Bingo and be able to Solo F3s in under 15 Minutes consistently.", h -> 5 - 1 - h, TradeTypeEnvironmentRegistry.CARRY_ENTRANCE_TO_F3_COMPLETION),
    CATACOMBS_FLOOR_1_230_SCORE("F1 230 Score / Emerald Chest", "Floor 1 230 Score Carry.", "Have Cata 1 on Bingo and be able to Solo F1s with 230 Score.", h -> 5 - 1 - h, TradeTypeEnvironmentRegistry.CATACOMBS_FLOOR_1_230_SCORE),
    CATACOMBS_FLOOR_1_270_SCORE("F1 270 Score / Obsidian Chest", "Floor 1 270 Score Carry.", "Have Cata 1 on Bingo and be able to Solo F1s with 270 Score.", h -> 5 - 1 - h, TradeTypeEnvironmentRegistry.CATACOMBS_FLOOR_1_270_SCORE),
    CATACOMBS_FLOOR_2_250_SCORE("F2 250 Score", "Floor 2 250 Score Carry.", "Have Cata 3 on Bingo and be able to Solo F2s with 250 Score.", h -> 5 - 1 - h, TradeTypeEnvironmentRegistry.CATACOMBS_FLOOR_2_250_SCORE),
    CATACOMBS_FLOOR_2_300_SCORE("F2 300 Score", "Floor 2 300 Score Carry.", "Have Cata 3 on Bingo and be able to Solo F2s with 300 Score.", h -> 5 - 1 - h, TradeTypeEnvironmentRegistry.CATACOMBS_FLOOR_2_300_SCORE),
    GIFTS("Trade Gifts", "Gift Trading", "Have at least 3 Stacks of Gifts", 1, TradeTypeEnvironmentRegistry.GIFTS),
    CARRY_LILY_PAD_GOAL("Lily Pad Collection Goal Carry", "Carry for Nut", "Have (95+ scc) or (75+ scc and auger rod) as well as Fishing 24+", 1, TradeTypeEnvironmentRegistry.CARRY_LILY_PAD_GOAL),
    CARRY_CHALLANGING_ROD("Challenging Rod Carry", "Carry for Challanging Rod Recipe", "Have (95+ scc) or (75+ scc and auger rod) as well as Fishing 24+", 5, TradeTypeEnvironmentRegistry.CARRY_CHALLENGING_ROD),
    CARRY_INK_SACKS("Ink Sack Carry", "Carry for Ink Sacks", "Have 90+ scc and use Squid Hat", 1, TradeTypeEnvironmentRegistry.CARRY_INK_SACKS),
    CRIMSON_ISLE_BARBARIAN_FACTION("Barbarian Questline Carry", "Help to complete Barbarian Questline.", "", h -> 4, TradeTypeEnvironmentRegistry.CRIMSON_ISLE_BARBARIAN_FACTION),
    CRIMSON_ISLE_MAGE_FACTION("Mage Questline Carry", "Help to complete Mage Faction Questline", "Have a way to kill Ashfangs. This may be asking other people but have something prepared.", h -> 4, TradeTypeEnvironmentRegistry.CRIMSON_ISLE_MAGE_FACTION),
    SPIDER_ESSENCE_COM_GOAL("Spider Essence Com Goal", "10 T2 Arachne Spawns (Arachne Crystal)", "Have 10 Arachne CRYSTALS", h -> 10, TradeTypeEnvironmentRegistry.SPIDER_ESSENCE_COM_GOAL),
    MINING_COMMISSIONS("Mining Commissions", "Carrier helps you by mining out tita.", "Titanium Insanium 25+, 2000+ Mining Speed", h -> 3, TradeTypeEnvironmentRegistry.MINING_COMMISSIONS),
    SLAYER_TIER_4("Slayer Level 4 Goal", "Help for Slayer Level 4 Goal. Done with either Zombie or Spider Tier 4s.", "Be able to kill Zombie and Spider Tier 4s in 30 seconds or less.", 2, TradeTypeEnvironmentRegistry.SLAYER_TIER_4), //For Any Slayer Level 4
    SLAYER_TIER_5("Slayer Level 5 Goal", "Help for Slayer Level 5 Goal. Done with either Zombie or Spider Tier 4s.", "Be able to kill Zombie and Spider Tier 4s in 15 seconds or less.", 2, TradeTypeEnvironmentRegistry.SLAYER_TIER_5), //For Any Slayer Level 5
    SPAWN_SCATHA("Spawn Scatha", "Carrier Spawns Worms / Scatha's for you.", "Instamine Hardstone and have Mole 90+", 1, TradeTypeEnvironmentRegistry.SPAWN_SCATHA);

    public final TradeTypeEnvironmentRegistry registry;
    public final String requirements;
    public final String name;
    public final String description;
    public final IntUnaryOperator maxUsers;

    TradeType(String name, String description, String requirements, IntUnaryOperator maxUsers, TradeTypeEnvironmentRegistry registry) {
        this.registry = registry;
        this.name = name;
        this.description = description;
        this.requirements = requirements;
        this.maxUsers = maxUsers;
    }

    TradeType(String name, String description, String requirements, int maxUsers, TradeTypeEnvironmentRegistry registry) {
        this(name, description, requirements, helpers -> maxUsers, registry);
    }

    public int getMaximumUsers(int size) {
        return maxUsers.applyAsInt(size);
    }

    public String getDescription() {
        return description;
    }

    public int getMinimumTradePartners() {
        return 2;
    }
}