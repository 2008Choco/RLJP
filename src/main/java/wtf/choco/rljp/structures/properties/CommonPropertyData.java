package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.Nullable;

/**
 * A set of common data present on all {@link Property Properties}.
 *
 * @param name The name of the property
 * @param dataLength The data length of the property in bytes
 * @param arrayIndex The array index of this property
 */
public record CommonPropertyData(@Nullable String name, int dataLength, int arrayIndex) { }
