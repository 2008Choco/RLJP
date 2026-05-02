package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.Nullable;
import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public final class EnumProperty extends Property {

    private final String enumTypeName;
    private final String enumValueName;

    private EnumProperty(CommonPropertyData propertyData, String enumTypeName, @Nullable String enumValueName) {
        super(propertyData, PropertyType.ENUM);

        this.enumTypeName = enumTypeName;
        this.enumValueName = enumValueName;
    }

    public String getEnumTypeName() {
        return enumTypeName;
    }

    @Nullable
    public String getEnumValueName() {
        return enumValueName;
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException {
        String enumTypeName = reader.readString();

        // Apparently "None" can be used as a type name, in which case the byte value that follows means nothing.
        // Maybe this is effectively "null"?
        if (enumTypeName.equals("None")) {
            reader.readUnsignedByte();
            return new EnumProperty(propertyData, enumTypeName, null);
        }

        String enumValueName = reader.readString();
        return new EnumProperty(propertyData, enumTypeName, enumValueName);
    }

}
