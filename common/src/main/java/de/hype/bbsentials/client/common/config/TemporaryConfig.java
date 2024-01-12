package de.hype.bbsentials.client.common.config;

import de.hype.bbsentials.client.common.objects.ChatPrompt;

import java.util.ArrayList;
import java.util.List;
@AToLoadBBsentialsConfig

public class TemporaryConfig implements BBsentialsConfig{
    public transient List<String> alreadyReported = new ArrayList<>();
    public transient ChatPrompt lastChatPromptAnswer = null;

    @Override
    public void setDefault() {

    }
}
