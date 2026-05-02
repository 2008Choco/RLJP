package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.Nullable;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A type of {@link Property} recognized by the Psyonix .replay file format.
 */
public enum PropertyType {

    STRING(StringProperty::read, "StrProperty", "NameProperty"), // String (UTF-16)
    INTEGER(IntegerProperty::read, "IntProperty"), // int
    FLOAT(FloatProperty::read, "FloatProperty"), // float
    LONG(LongProperty::read, "QWordProperty"), // long
    BOOLEAN(BooleanProperty::read, "BoolProperty"), // boolean
    ENUM(EnumProperty::read, "ByteProperty"), // Enums are stored as "byte properties"
    UID((PropertyReader) UIDProperty::read, "Uid"),
    STRUCT((PropertyReader) StructProperty::read, "StructProperty"),
    ARRAY((PropertyReader) ArrayProperty::read, "ArrayProperty"),
    NULL((_, _) -> NullProperty.INSTANCE, "None", "\0\0\0None");

    private static final Map<String, PropertyType> BY_IDENTIFIER = new HashMap<>();

    static {
        for (PropertyType type : values()) {
            for (String identifier : type.identifiers) {
                BY_IDENTIFIER.put(identifier, type);
            }
        }
    }

    private final PropertyReader reader;
    private final String[] identifiers;

    private PropertyType(PropertyReader reader, String... identifiers) {
        this.reader = reader;
        this.identifiers = identifiers;
    }

    private PropertyType(PropertyReaderVersionless reader, String... identifiers) {
        this((PropertyReader) reader, identifiers);
    }

    public Property read(ReplayStreamReader reader, CommonPropertyData propertyData, ReplayVersionData version) throws IOException {
        return this.reader.read(reader, propertyData, version);
    }

    /**
     * Gets a {@link PropertyType} based on a case-sensitive property name in the .replay file format.
     *
     * @param identifier the unique property identifier
     *
     * @return the property type, or null if no property type has the given identifier
     */
    @Nullable
    public static PropertyType fromIdentifier(String identifier) {
        return BY_IDENTIFIER.get(identifier);
    }

}
