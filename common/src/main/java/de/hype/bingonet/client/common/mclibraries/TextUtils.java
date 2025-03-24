package de.hype.bingonet.client.common.mclibraries;

public interface TextUtils {
    de.hype.bingonet.client.common.mclibraries.interfaces.Text createText(String content);

    String getContentFromJson(String json);

    String getJsonFromContent(String content);
}
