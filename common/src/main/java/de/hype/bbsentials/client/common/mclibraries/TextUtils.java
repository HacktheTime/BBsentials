package de.hype.bbsentials.client.common.mclibraries;

public interface TextUtils {
    de.hype.bbsentials.client.common.mclibraries.interfaces.Text createText(String content);

    String getContentFromJson(String json);

    String getJsonFromContent(String content);
}
