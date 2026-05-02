package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public final class StringProperty extends Property {

    private final String value;

    private StringProperty(CommonPropertyData propertyData, String value) {
        super(propertyData, PropertyType.STRING);
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        return new StringProperty(propertyData, reader.readString());
    }

}
