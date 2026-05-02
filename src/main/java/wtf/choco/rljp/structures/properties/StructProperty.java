package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;

public final class StructProperty extends Property {

    private final PropertyList properties;

    private StructProperty(CommonPropertyData propertyData, PropertyList properties) {
        super(propertyData, PropertyType.STRUCT);
        this.properties = properties;
    }

    public PropertyList getProperties() {
        return properties;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData, ReplayVersionData version) throws IOException {
        String structName = reader.readString(); // I think this is the name of the struct being saved. We can ignore it. Serves no purpose for us
        PropertyList properties = PropertyList.read(reader, version);
        return new StructProperty(propertyData, properties);
    }

}
