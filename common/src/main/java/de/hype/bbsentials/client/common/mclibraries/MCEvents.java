package de.hype.bbsentials.client.common.mclibraries;

public interface MCEvents {

    default void registerAll() {
        registerOffline();
        registerOverlays();
        registerUseClick();
    }

    default void registerOffline() {
        registerWaypoints();
    }

    void registerOverlays();

    void registerUseClick();

    void registerWaypoints();

}
