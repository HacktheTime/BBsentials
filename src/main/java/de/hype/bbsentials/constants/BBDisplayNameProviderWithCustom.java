package de.hype.bbsentials.constants;

public interface BBDisplayNameProviderWithCustom<T extends Enum<T>> extends BBDisplayNameProvider {
    T setDisplayName(String displayname);

}
