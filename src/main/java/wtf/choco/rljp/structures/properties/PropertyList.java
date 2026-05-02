package wtf.choco.rljp.structures.properties;

import org.jetbrains.annotations.NotNull;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public record PropertyList(List<Property> properties) implements Iterable<Property> {

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

    public int size() {
        return properties.size();
    }

    @NotNull
    @Override
    public Iterator<Property> iterator() {
        return properties.iterator();
    }

}
