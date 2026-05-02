package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;

/**
 * Represents a property defined in the {@link PropertyList} of a .replay file header. Each property has a name,
 * type, data length (in bytes), and its index in the array. A property can be a variety of types, such as an integer,
 * float, string, enum, struct, UID, and so on and so forth.
 * <p>
 * Properties are designed to be type safe for switch expression patterns for more easy parsing, and so that primitive
 * values can be held as their primitive values rather than being unnecessarily boxed and unboxed when needed.
 */
public abstract sealed class Property permits ArrayProperty, BooleanProperty, EnumProperty, FloatProperty, IntegerProperty, LongProperty, NullProperty, StringProperty, StructProperty, UIDProperty {

    @Nullable
    private final String name;
    private final PropertyType type;
    private final int dataLength;
    private final int arrayIndex;

    protected Property(@Nullable String name, PropertyType type, int dataLength, int arrayIndex) {
        this.name = name;
        this.type = type;
        this.dataLength = dataLength;
        this.arrayIndex = arrayIndex;
    }

    protected Property(CommonPropertyData data, PropertyType type) {
        this(data.name(), type, data.dataLength(), data.arrayIndex());
    }

    @Nullable
    public String getName() {
        return name;
    }

    public PropertyType getType() {
        return type;
    }

    public int getDataLength() {
        return dataLength;
    }

    public int getArrayIndex() {
        return arrayIndex;
    }

    protected abstract String stringifyData();

    @NotNull
    @Override
    public String toString() {
        String string = getClass().getSimpleName() + "{" +
            "name='" + name + '\'' +
            ", type=" + type +
            ", dataLength=" + dataLength +
            ", arrayIndex=" + arrayIndex;

        String data = stringifyData();
        if (!data.isBlank()) {
            string += ", " + data;
        }

        return string + "}";
    }

    public static Property read(ReplayStreamReader reader, ReplayVersionData version) throws IOException {
        String name = reader.readString();
        if (PropertyType.fromIdentifier(name) == PropertyType.NULL) {
            return NullProperty.INSTANCE;
        }

        String type = reader.readString();
        int dataLength = reader.readUnsignedInt();
        int arrayIndex = reader.readUnsignedInt();

        PropertyType propertyType = PropertyType.fromIdentifier(type);
        if (propertyType == null) {
            throw new IOException("Unknown property type: " + type);
        }

        return propertyType.read(reader, new CommonPropertyData(name, dataLength, arrayIndex), version);
    }

}
