package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * A list of {@link Property Properties} defined in the Rocket League .replay header.
 */
public final class PropertyList implements Iterable<Property> {

    private Map<String, Property> propertiesById;

    private final List<Property> properties;

    /**
     * Construct a {@link PropertyList} from a {@link List} of {@link Property Properties}.
     *
     * @param properties the properties to wrap
     */
    public PropertyList(List<Property> properties) {
        this.properties = List.copyOf(properties);
    }

    /**
     * Get the properties in this list.
     *
     * @return the properties
     */
    @Unmodifiable
    public List<Property> properties() {
        return properties;
    }

    /**
     * Get a specific property by its name.
     *
     * @param propertyName the name of the property to get. Case-sensitive
     *
     * @return the corresponding {@link Property} with the given name, or {@code null} if a property with
     * the given name does not exist
     */
    @Nullable
    public Property property(String propertyName) {
        if (propertiesById == null) {
            this.propertiesById = properties.stream()
                .filter(property -> property.getName() != null)
                .collect(Collectors.toUnmodifiableMap(Property::getName, property -> property));
        }

        return propertiesById.get(propertyName);
    }

    /**
     * Get a specific property by its name and cast it to the specified type.
     *
     * @param propertyName the name of the property to get. Case-sensitive
     * @param propertyType the type to cast the property to
     *
     * @return the corresponding {@link Property} with the given name, or {@code null} if a property with
     * the given name does not exist, or if the property is not of the requested type
     */
    @Nullable
    public <T extends Property> T property(String propertyName, Class<T> propertyType) {
        return propertyType.cast(property(propertyName));
    }

    /**
     * Get a specific property by its name.
     *
     * @param propertyName the name of the property to get. Case-sensitive
     *
     * @return the corresponding {@link Property} with the given name
     *
     * @throws IllegalArgumentException if a property with the given name does not exist
     */
    public Property propertyOrThrow(String propertyName) {
        Property property = property(propertyName);
        if (property == null) {
            throw new IllegalArgumentException("No property with name '" + propertyName + "' exists!");
        }

        return property;
    }

    /**
     * Get a specific property by its name.
     *
     * @param propertyName the name of the property to get. Case-sensitive
     * @param propertyType the type to cast the property to
     *
     * @return the corresponding {@link Property} with the given name
     *
     * @throws IllegalArgumentException if a property with the given name does not exist, or if the
     * property cannot be coerced to the requested type
     */
    public <T extends Property> T propertyOrThrow(String propertyName, Class<T> propertyType) {
        Property property = propertyOrThrow(propertyName);
        if (!propertyType.isInstance(property)) {
            throw new IllegalArgumentException(propertyName + " exists, but could not be coerced to type " + propertyType.getName());
        }

        return propertyType.cast(property);
    }

    /**
     * Try to get a specific property by its name.
     *
     * @param propertyName the name of the property to get. Case-sensitive
     *
     * @return an {@link Optional} containing the corresponding {@link Property} with the given name,
     * or an empty Optional if a property with the given name does not exist
     */
    public Optional<Property> tryProperty(String propertyName) {
        return Optional.ofNullable(property(propertyName));
    }

    /**
     * Try to get a specific property by its name.
     *
     * @param propertyName the name of the property to get. Case-sensitive
     * @param propertyType the type to cast the property to
     *
     * @return an {@link Optional} containing the corresponding {@link Property} with the given name,
     * or an empty Optional if a property with the given name does not exist, or if the property cannot
     * be coerced to the requested type
     */
    public <T extends Property> Optional<T> tryProperty(String propertyName, Class<T> propertyType) {
        Property property = property(propertyName);
        if (!propertyType.isInstance(property)) {
            return Optional.empty();
        }

        return Optional.of(propertyType.cast(property));
    }

    /**
     * Get the amount of properties in this list.
     *
     * @return the property list size
     */
    public int size() {
        return properties.size();
    }

    @NotNull
    @Override
    public String toString() {
        return "PropertyList{" + properties + "}";
    }

    @NotNull
    @Override
    public Iterator<Property> iterator() {
        return properties.iterator();
    }

    public static PropertyList read(ReplayStreamReader reader, ReplayVersionData version) throws IOException {
        List<Property> properties = new ArrayList<>();

        while (true) {
            Property property;
            try {
                property = Property.read(reader, version);
            } catch (IOException e) {
                System.err.println("Tried reading property but failed, ignoring for now (message: " + e.getMessage() + ")");
                continue;
            }

            if (property == NullProperty.INSTANCE) {
                break;
            }

            properties.add(property);
        }

        return new PropertyList(properties);
    }

}
