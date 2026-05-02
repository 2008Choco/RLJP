package wtf.choco.rljp.structures.uniqueid;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.Arrays;

/**
 * Represents a unique ID for a player. Unique IDs generally come in a variety of formats and provide
 * various bits of information for specific platforms.
 *
 * @see SteamUniqueId
 * @see PS4UniqueId
 */
public sealed class UniqueId permits SteamUniqueId, PS4UniqueId {

    public static final byte[] ZERO = { 0, 0, 0 };

    private final UniqueIdType type;
    protected final byte[] id;
    private final int playerNumber; // Always 0 unless in split screen mode

    /**
     * Construct a new {@link UniqueId}.
     *
     * @param type the type of unique id being constructed
     * @param id the unique id sequence
     * @param playerNumber the player number, or 0 if not using split screen
     */
    public UniqueId(UniqueIdType type, byte[] id, int playerNumber) {
        this.type = type;
        this.id = id;
        this.playerNumber = playerNumber;
    }

    /**
     * Get the {@link UniqueIdType} of this UniqueId.
     *
     * @return the type
     */
    public UniqueIdType getType() {
        return type;
    }

    /**
     * Get this id as an array of bytes.
     *
     * @return the id
     */
    public final byte[] getId() {
        return id.clone();
    }

    /**
     * Get the player number, which is the number of the player on a split screen (if in use).
     * <p>
     * Players that are using split screen have the same UniqueId. Therefore, the only additional
     * unique identifier is a numeric value. If this UniqueId is not using split screen, then this
     * value is 0.
     *
     * @return the player number, or 0 if split screen is not in use
     */
    public int getPlayerNumber() {
        return playerNumber;
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

                int playerNumber = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.UNKNOWN, id, playerNumber);
            }
            case XBOX -> {
                byte[] id = reader.readUnsignedBytes(8);
                int playerNumber = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.XBOX, id, playerNumber);
            }
            case SWITCH -> {
                byte[] id = reader.readUnsignedBytes(32);
                int playerNumber = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.SWITCH, id, playerNumber);
            }
            case PSYNET -> {
                byte[] id = reader.readUnsignedBytes(version.netVersion() >= 10 ? 8 : 32);
                int playerNumber = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.PSYNET, id, playerNumber);
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

                int playerNumber = reader.readUnsignedByte();
                yield new UniqueId(UniqueIdType.EPIC, actualId, playerNumber);
            }
        };
    }

}
