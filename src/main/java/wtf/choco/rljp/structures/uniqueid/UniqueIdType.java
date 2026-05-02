package wtf.choco.rljp.structures.uniqueid;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * A type of {@link UniqueId}.
 */
public enum UniqueIdType {

    /**
     * An unknown Unique ID. Invalid format?
     */
    UNKNOWN(0),
    /**
     * A unique Steam ID.
     *
     * @see SteamUniqueId
     */
    STEAM(1),
    /**
     * A unique PS4 ID
     *
     * @see PS4UniqueId
     */
    PS4(2),
    /**
     * A unique PS3 ID.
     */
    PS3(3),
    /**
     * A unique XBox ID.
     */
    XBOX(4),
    /**
     * A unique Switch ID.
     */
    SWITCH(6),
    /**
     * A unique Psynet ID (legacy).
     */
    PSYNET(7),
    /**
     * A unique Epic Games ID.
     */
    EPIC(11);

    private static final Map<Integer, UniqueIdType> BY_ID = new HashMap<>();

    static {
        for (UniqueIdType type : values()) {
            BY_ID.put(type.id, type);
        }
    }

    private final int id;

    private UniqueIdType(int id) {
        this.id = id;
    }

    @ApiStatus.Internal
    public int getId() {
        return id;
    }

    @ApiStatus.Internal
    public static UniqueIdType fromId(int id) {
        return BY_ID.getOrDefault(id, UNKNOWN);
    }

}
