package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.Nullable;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;

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

    @Nullable
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
