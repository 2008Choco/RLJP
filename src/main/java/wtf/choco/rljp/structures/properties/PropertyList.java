package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class PropertyList implements Iterable<Property> {

    private Map<String, Property> propertiesById;

    private final List<Property> properties;

    public PropertyList(List<Property> properties) {
        this.properties = List.copyOf(properties);
    }

    public List<Property> properties() {
        return properties;
    }

    @Nullable
    public Property property(String propertyName) {
        if (propertiesById == null) {
            this.propertiesById = properties.stream()
                .filter(property -> property.getName() != null)
                .collect(Collectors.toUnmodifiableMap(Property::getName, property -> property));
        }

        return propertiesById.get(propertyName);
    }

    public Property propertyOrThrow(String propertyName) {
        Property property = property(propertyName);
        if (property == null) {
            throw new IllegalArgumentException("No property with name '" + propertyName + "' exists!");
        }

        return property;
    }

    public <T extends Property> T propertyOrThrow(String propertyName, Class<T> propertyType) {
        Property property = propertyOrThrow(propertyName);
        if (!propertyType.isInstance(property)) {
            throw new IllegalArgumentException(propertyName + " exists, but could not be coerced to type " + propertyType.getName());
        }

        return propertyType.cast(property);
    }

    public Optional<Property> tryProperty(String propertyName) {
        return Optional.ofNullable(property(propertyName));
    }

    public <T extends Property> Optional<T> tryProperty(String propertyName, Class<T> propertyType) {
        Property property = property(propertyName);
        if (property == null || !propertyType.isInstance(property)) {
            return Optional.empty();
        }

        return Optional.of(propertyType.cast(property));
    }

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
