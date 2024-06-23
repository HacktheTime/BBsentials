package de.hype.bbsentials.shared.packets.function;

import de.hype.bbsentials.environment.packetconfig.AbstractPacket;

public class PlaySoundPacket extends AbstractPacket {
    public boolean streamFromUrl;
    public int durationInSeconds;
    public String soundId;

    /**
     * @param soundId Sound id. may use namespace:id. default is minecraft so no:minecraft required.
     * Use {@link #streamFromUrl(String, int)} to stream play an audio file from the given url.
     */
    public PlaySoundPacket(String soundId, int durationInSeconds) {
        super(1, 1);
        this.soundId = soundId;
        this.durationInSeconds = durationInSeconds;

    }

    public static PlaySoundPacket streamFromUrl(String url, int durationInSeconds) {
        PlaySoundPacket packet = new PlaySoundPacket(url, durationInSeconds);
        packet.streamFromUrl = true;
        return packet;
    }

    public boolean isStreamFromUrl() {
        return streamFromUrl;
    }
}
