package de.hype.bbsentials.client.common;

import de.hype.bbsentials.client.common.mclibraries.EnvironmentCore;

public abstract class Text {
    private String content;
    private String json;

    public void setJson(String json) {
        this.json = json;
        this.content = EnvironmentCore.textutils.getContentFromJson(json);
    }

    public void setContent(String content) {
        this.content = content;
        this.json = EnvironmentCore.textutils.getJsonFromContent(content);
    }

    public String getContent() {
        return content;
    }

    public boolean contentStartswith(String str) {
        return content.startsWith(str);
    }

    public boolean contentEquals(String str) {
        return content.equals(str);
    }

    public boolean contentEqualsIgnorecase(String str) {
        return content.equalsIgnoreCase(str);
    }

    public boolean contentEndswith(String str) {
        return content.endsWith(str);
    }

    public boolean contentContains(String str) {
        return content.contains(str);
    }

    public boolean contentContainsRegex(String regex) {
        return content.matches(regex);
    }

    public String replaceInContent(String string, String replacement) {
        setContent(content.replace(string, replacement));
        return content;
    }

    public String replaceRegexContent(String regex, String replacement) {
        setContent(content.replaceAll(regex, replacement));
        return content;
    }

    public String replaceFirstRegexContent(String regex, String replacement) {
        setContent(content.replaceFirst(regex, replacement));
        return content;
    }


}
