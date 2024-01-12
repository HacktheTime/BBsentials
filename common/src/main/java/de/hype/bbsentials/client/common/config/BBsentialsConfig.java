package de.hype.bbsentials.client.common.config;

public interface BBsentialsConfig {
    public void setDefault();

    default void onInit(BBsentialsConfig config) {
    }
}
