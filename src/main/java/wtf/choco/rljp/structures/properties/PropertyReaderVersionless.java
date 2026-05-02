package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;

public interface PropertyReaderVersionless extends PropertyReader {

    public Property read(ReplayStreamReader reader, CommonPropertyData propertyData) throws IOException;

    @Override
    public default Property read(ReplayStreamReader reader, CommonPropertyData propertyData, ReplayVersionData version) throws IOException {
        return read(reader, propertyData);
    }

}
