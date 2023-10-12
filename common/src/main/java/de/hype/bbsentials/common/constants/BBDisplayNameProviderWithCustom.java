package de.hype.bbsentials.common.constants;

public interface BBDisplayNameProviderWithCustom<T extends Enum<T>> extends BBDisplayNameProvider {
    T setDisplayName(String displayname);

}
