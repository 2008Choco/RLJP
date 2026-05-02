package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

/**
 * An integer {@link Property}.
 */
public final class IntegerProperty extends Property {

    private final int value;

    private IntegerProperty(CommonPropertyData propertyData, int value) {
        super(propertyData, PropertyType.INTEGER);
        this.value = value;
    }

    /**
     * @return the value of this property
     */
    public int getValue() {
        return value;
    }

    @Override
    protected String stringifyData() {
        return "value=" + value;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        return new IntegerProperty(propertyData, reader.readUnsignedInt());
    }

}
