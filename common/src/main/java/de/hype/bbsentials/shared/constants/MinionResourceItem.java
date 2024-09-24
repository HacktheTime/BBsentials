package de.hype.bbsentials.shared.constants;

public interface MinionResourceItem {

    default Integer getCompactorLevel() {
        return 1;
    }

    String getDisplayName();

    public enum UnusedMinionItems implements MinionResourceItem {
        RED_GIFT("Purple Candy"),
        LUSH_BERRIES("Lush Berrbries"),
        PURPLE_CANDY("Purple Candy"),
        ;

        public final String displayName;

        UnusedMinionItems(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String getDisplayName() {
            return null;
        }
    }
}
