package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;
import wtf.choco.rljp.structures.uniqueid.UniqueId;

import java.io.IOException;

public final class UIDProperty extends Property {

    private final UniqueId value;

    private UIDProperty(CommonPropertyData propertyData, UniqueId value) {
        super(propertyData, PropertyType.UID);
        this.value = value;
    }

    public UniqueId getValue() {
        return value;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData, ReplayVersionData version) throws IOException {
        return new UIDProperty(propertyData, UniqueId.read(reader, version));
    }

}
