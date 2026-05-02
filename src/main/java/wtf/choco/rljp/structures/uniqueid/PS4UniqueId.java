package wtf.choco.rljp.structures.uniqueid;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.Arrays;
import java.util.OptionalLong;

/**
 * A {@link UniqueId} for a PSN client.
 */
public final class PS4UniqueId extends UniqueId {

    /*
     * PS4 Unique ID format:
     * 00000000 00000000 00000000 00000000 00000000 (32 - 40 bytes)
     *
     * Bytes 0 - 15: PSN name (UTF-8 null terminated string)
     * Bytes 16 - 23: Unknown (UTF-8 null terminated string). Used by PS4 API
     * Bytes 24 - 31: Unknown (64 bit long)
     * Bytes 32 - 39: PSN ID (64 bit long). Not always present
     */

    private String psnName;
    private long psnId;

    /**
     * Construct a new {@link PS4UniqueId}.
     *
     * @param id the unique id sequence
     * @param playerNumber the player number, or 0 if not using split screen
     */
    public PS4UniqueId(byte[] id, int playerNumber) {
        super(UniqueIdType.PS4, id, playerNumber);
    }

    /**
     * Get the name of the PSN user.
     *
     * @return the PSN name
     */
    public String getPsnName() {
        if (psnName == null) {
            this.psnName = new String(id, 0, 16).replace("\0", "");
        }

        return psnName;
    }

    /**
     * Get an {@link OptionalLong} containing the PSN ID of the user, if it exists.
     *
     * @return the PSN ID
     */
    public OptionalLong getPsnId() {
        if (id.length <= 32) {
            return OptionalLong.empty();
        }

        if (psnId == 0) {
            byte[] longBytes = Arrays.copyOfRange(id, 32, Long.BYTES + 1);
            long value = 0;
            for (int i = 0; i < Long.BYTES; i++) {
                value = (value << Byte.SIZE) | Byte.toUnsignedInt(longBytes[i]);
            }
            this.psnId = value;
        }

        return OptionalLong.of(psnId);
    }

    public static PS4UniqueId read(ReplayStreamReader reader, ReplayVersionData version) throws IOException {
        byte[] id = reader.readUnsignedBytes(version.netVersion() >= 1 ? 40 : 32);
        int playerIndex = reader.readUnsignedByte();
        return new PS4UniqueId(id, playerIndex);
    }

}
