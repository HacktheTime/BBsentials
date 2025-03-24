package de.hype.bingonet.shared.constants;

public interface BBDisplayNameProviderWithCustom<T extends Enum<T>> extends BBDisplayNameProvider {
    T setDisplayName(String displayname);

}
