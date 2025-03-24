package de.hype.bingonet.client.common.mclibraries;

public interface MCEvents {

    default void registerAll() {
        registerOffline();
        registerOverlays();
        registerUseClick();
    }

    default void registerOffline() {
    }

    void registerOverlays();

    void registerUseClick();

}
