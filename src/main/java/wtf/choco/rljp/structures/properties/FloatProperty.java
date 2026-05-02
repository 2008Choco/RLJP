package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public final class FloatProperty extends Property {

    private final float value;

    private FloatProperty(CommonPropertyData propertyData, float value) {
        super(propertyData, PropertyType.FLOAT);
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    @Override
    protected String stringifyData() {
        return "value=" + value;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        return new FloatProperty(propertyData, reader.readFloat());
    }

}
