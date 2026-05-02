package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

/**
 * A string {@link Property}.
 * <p>
 * A string property may be encoded in one of the following charsets: UTF-8, UTF-16, or Windows-1252
 */
public final class StringProperty extends Property {

    private final String value;

    private StringProperty(CommonPropertyData propertyData, String value) {
        super(propertyData, PropertyType.STRING);
        this.value = value;
    }

    /**
     * @return the value of this property
     */
    public String getValue() {
        return value;
    }

    @Override
    protected String stringifyData() {
        return "value=\"" + value + "\"";
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        return new StringProperty(propertyData, reader.readString());
    }

}
