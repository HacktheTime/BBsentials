package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.objects.ChatPrompt;

import java.util.ArrayList;
import java.util.List;


public class TemporaryConfig extends BBsentialsConfig {
    public transient List<String> alreadyReported = new ArrayList<>();
    public transient ChatPrompt lastChatPromptAnswer = null;

    public TemporaryConfig() {
        super(1);
        doInit();
    }

    @Override
    public void setDefault() {

    }
}
