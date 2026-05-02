package wtf.choco.rljp.structures.uniqueid;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

/**
 * A {@link UniqueId} for a Steam client.
 */
public final class SteamUniqueId extends UniqueId {

    private long steamId;
    private String steamProfileURL;

    /**
     * Construct a new {@link SteamUniqueId}.
     *
     * @param id the unique id sequence
     * @param playerNumber the player number, or 0 if not using split screen
     */
    public SteamUniqueId(byte[] id, int playerNumber) {
        super(UniqueIdType.STEAM, id, playerNumber);
    }

    /**
     * Get the user's unique steam ID.
     *
     * @return the steam ID
     */
    public long getSteamId() {
        if (steamId == 0) {
            long value = 0;
            for (int i = 0; i < Long.BYTES; i++) {
                value = (value << Byte.SIZE) | Byte.toUnsignedInt(id[i]);
            }
            this.steamId = value;
        }

        return steamId;
    }

    /**
     * Get the user's steam community profile URL.
     *
     * @return the steam community profile URL
     */
    public String getSteamProfileURL() {
        if (steamProfileURL == null) {
            this.steamProfileURL = "https://steamcommunity.com/profiles/" + getSteamId();
        }

        return steamProfileURL;
    }

    public static SteamUniqueId read(ReplayStreamReader reader) throws IOException {
        byte[] id = reader.readUnsignedBytes(8);
        int playerIndex = reader.readUnsignedByte();
        return new SteamUniqueId(id, playerIndex);
    }

}
