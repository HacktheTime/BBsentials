package de.hype.bbsentials.common.packets;

import de.hype.bbsentials.common.chat.Chat;
import de.hype.bbsentials.common.client.BBsentials;
import de.hype.bbsentials.common.client.Config;
import de.hype.bbsentials.common.communication.BBsentialConnection;
import de.hype.bbsentials.common.packets.packets.InvalidCommandFeedbackPacket;

import java.lang.reflect.Field;

public class AbstractPacket {
    public final int apiVersionMin;
    public final int apiVersionMax;

    protected AbstractPacket(int apiVersionMin, int apiVersionMax) {
        this.apiVersionMax = apiVersionMax;
        this.apiVersionMin = apiVersionMin;

    }

    public boolean isValid(BBsentialConnection connection, String[] allowedNullFields) {
        if (isApiSupported(BBsentials.config)) {
            Chat.sendPrivateMessageToSelfFatal("You are using an outdated version of the mod");
        }
        return true;
    }

    public boolean isApiSupported(Config config) {
        //int version = Core.getConfig().getVersion();
        int version = config.getApiVersion();
        if (version >= apiVersionMin && version <= apiVersionMax) {
            return true;
        }
        return false;
    }

    public String hasNullFields(String[] allowedNullFields) {
        Field[] fields = this.getClass().getDeclaredFields();
        if (!this.getClass().getSimpleName().equals(InvalidCommandFeedbackPacket.class.getSimpleName())) {
            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    if (field.get(this) == null) {
                        if (allowedNullFields == null) return field.getName();
                        if (isAllowedNull(allowedNullFields, field.getName())) {
                            return field.getName();
                        }
                    }
                } catch (IllegalAccessException e) {
                    // Handle the exception if needed
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

    public boolean isAllowedNull(String[] allowedFields, String fieldName) {
        for (String allowedField : allowedFields) {
            if (allowedField.equals(fieldName)) {
                return true;
            }
        }
        return false;
    }
}