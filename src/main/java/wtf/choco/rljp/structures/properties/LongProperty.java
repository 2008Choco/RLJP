package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public final class LongProperty extends Property {

    private final long value;

    private LongProperty(CommonPropertyData propertyData, long value) {
        super(propertyData, PropertyType.LONG);
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        return new LongProperty(propertyData, reader.readUnsignedLong());
    }

}
