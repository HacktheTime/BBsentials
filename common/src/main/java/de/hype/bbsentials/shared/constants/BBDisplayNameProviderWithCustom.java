package de.hype.bbsentials.shared.constants;

public interface BBDisplayNameProviderWithCustom<T extends Enum<T>> extends BBDisplayNameProvider {
    T setDisplayName(String displayname);

}
