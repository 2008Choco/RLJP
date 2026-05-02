package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.Nullable;
import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

/**
 * An enum {@link Property}.
 * <p>
 * Enum properties are defined by a type name and a value name. The type name is the name of the enum (presumably)
 * in Rocket League's source. The value name is the value of that enum.
 */
public final class EnumProperty extends Property {

    /**
     * An enum type name that represents an enum with no value.
     *
     * @see #getEnumTypeName()
     */
    public static final String TYPE_NAME_NONE = "None";

    private final String enumTypeName;
    private final String enumValueName;

    private EnumProperty(CommonPropertyData propertyData, String enumTypeName, @Nullable String enumValueName) {
        super(propertyData, PropertyType.ENUM);

        this.enumTypeName = enumTypeName;
        this.enumValueName = enumValueName;
    }

    /**
     * @return the name of the enum type
     */
    public String getEnumTypeName() {
        return enumTypeName;
    }

    /**
     * @return the enum value of this property, or {@code null} if this enum property has no value, which will
     * be the case if {@link #getEnumTypeName()} is {@value TYPE_NAME_NONE}.
     */
    @Nullable
    public String getEnumValueName() {
        return enumValueName;
    }

    /**
     * @return true if this enum property represents an empty/null/"None" value
     */
    public boolean isNone() {
        return TYPE_NAME_NONE.equals(enumTypeName) && enumValueName == null;
    }

    @Override
    protected String stringifyData() {
        return "enumTypeName=" + enumTypeName + ", enumValueName=" + enumValueName;
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
