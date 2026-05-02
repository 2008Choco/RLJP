package wtf.choco.rljp.structures.uniqueid;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.Arrays;

public sealed class UniqueId permits SteamUniqueId, PS4UniqueId {

    public static final byte[] ZERO = { 0, 0, 0 };

    private final UniqueIdType type;
    protected final byte[] id;
    private final int playerIndex; // Always 0 unless in split screen mode

    public UniqueId(UniqueIdType type, byte[] id, int playerIndex) {
        this.type = type;
        this.id = id;
        this.playerIndex = playerIndex;
    }

    public UniqueIdType getType() {
        return type;
    }

    public final byte[] getId() {
        return id.clone();
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public static UniqueId read(ReplayStreamReader reader, ReplayVersionData version) throws IOException {
        int typeId = reader.readUnsignedByte();
        UniqueIdType type = UniqueIdType.fromId(typeId);
        if (type == null) {
            throw new IOException("Unknown UniqueId type: " + typeId);
        }

        return switch (type) {
            case STEAM -> SteamUniqueId.read(reader);
            case PS4 -> PS4UniqueId.read(reader, version);
            case PS3 -> throw new UnsupportedOperationException("Don't know how to decode PS3 unique id!");
            case UNKNOWN -> {
                if (version.licenseeVersion() >= 18 && version.netVersion() == 0) {
                    // In these versions, there are no id bytes to read for UNKNOWN type
                    yield new UniqueId(UniqueIdType.UNKNOWN, new byte[0], 0);
                }

                byte[] id = reader.readUnsignedBytes(3);
                if (!Arrays.equals(id, ZERO) && (version.licenseeVersion() < 18 || version.netVersion() > 0)) {
                    throw new IOException("Id of type UNKNOWN has non-zero bytes where it was expected: " + Arrays.toString(id));
                }

                int playerIndex = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.UNKNOWN, id, playerIndex);
            }
            case XBOX -> {
                byte[] id = reader.readUnsignedBytes(8);
                int playerIndex = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.XBOX, id, playerIndex);
            }
            case SWITCH -> {
                byte[] id = reader.readUnsignedBytes(32);
                int playerIndex = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.SWITCH, id, playerIndex);
            }
            case PSYNET -> {
                byte[] id = reader.readUnsignedBytes(version.netVersion() >= 10 ? 8 : 32);
                int playerIndex = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.PSYNET, id, playerIndex);
            }
            case EPIC -> {
                // Credit to RocketLeagueReplayParser for this fuckery
                // https://github.com/jjbott/RocketLeagueReplayParser/blob/d6d3a461a7c04a4df5b0aa0afcabe99f9c34fb7b/RocketLeagueReplayParser/NetworkStream/UniqueId.cs#L91-L98
                byte[] id = reader.readUnsignedBytes(4);
                int length = (id[3] << 24) + (id[2] << 16) + (id[1] << 8) + id[0];

                byte[] actualId = Arrays.copyOf(id, id.length + length);
                for (int i = 0; i < length; i++) {
                    actualId[id.length + i] = (byte) reader.readUnsignedByte();
                }

                int playerIndex = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.EPIC, actualId, playerIndex);
            }
        };
    }

}
