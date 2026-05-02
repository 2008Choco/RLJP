package wtf.choco.rljp.structures.uniqueid;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public final class SteamUniqueId extends UniqueId {

    private long steamId;
    private String steamProfileURL;

    public SteamUniqueId(byte[] id, int playerIndex) {
        super(UniqueIdType.STEAM, id, playerIndex);
    }

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
