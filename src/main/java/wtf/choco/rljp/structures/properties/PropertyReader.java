package wtf.choco.rljp.structures.properties;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.ReplayVersionData;

import java.io.IOException;

public interface PropertyReader {

    public Property read(ReplayStreamReader reader, CommonPropertyData propertyData, ReplayVersionData version) throws IOException;

}
