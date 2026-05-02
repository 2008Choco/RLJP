package wtf.choco.rljp.structures.uniqueid;

import java.util.HashMap;
import java.util.Map;

public enum UniqueIdType {

    UNKNOWN(0),
    STEAM(1),
    PS4(2),
    PS3(3),
    XBOX(4),
    SWITCH(6),
    PSYNET(7),
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

    public int getId() {
        return id;
    }

    public static UniqueIdType fromId(int id) {
        return BY_ID.getOrDefault(id, UNKNOWN);
    }

}
