package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class ArrayProperty extends Property {

    private final List<PropertyList> properties;

    private ArrayProperty(CommonPropertyData propertyData, List<PropertyList> properties) {
        super(propertyData, PropertyType.ARRAY);
        this.properties = properties;
    }

    public List<PropertyList> getProperties() {
        return properties;
    }

    @Override
    protected String stringifyData() {
        return "properties={" + properties + "}";
    }

    public static Property read(ReplayStreamReader reader, CommonPropertyData propertyData, ReplayVersionData version) throws IOException {
        int size = reader.readUnsignedInt();
        List<PropertyList> propertiesList = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            propertiesList.add(PropertyList.read(reader, version));
        }

        return new ArrayProperty(propertyData, propertiesList);
    }

}
