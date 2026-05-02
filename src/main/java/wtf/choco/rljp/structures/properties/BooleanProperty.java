package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public final class BooleanProperty extends Property {

    private final boolean value;

    private BooleanProperty(CommonPropertyData propertyData, boolean value) {
        super(propertyData, PropertyType.BOOLEAN);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    protected String stringifyData() {
        return "value=" + value;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        return new BooleanProperty(propertyData, reader.readBoolean());
    }

}
