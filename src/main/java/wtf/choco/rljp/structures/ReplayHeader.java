package wtf.choco.rljp.structures;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.properties.PropertyList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public record ReplayHeader(int headerLength, int headerCRC, ReplayVersionData version, PropertyList properties, int eofLength, int eofCRC, List<String> levels, List<KeyFrame> keyframes, int networkStreamLength) {

    private static final String REPLAY_OBJECT_CLASS_NAME = "TAGame.Replay_Soccar_TA";

    /*
       header_length         => UInt_32
       header_crc            => UInt_32
       version               => Version
       properties            => Properties
       eof_length            => UInt_32
       eof_crc               => UInt_32
       levels                => List[UInt_32, String8]
       keyframes             => List[UInt_32, KeyFrame]
       network_stream_length => UInt_32
     */

    public static ReplayHeader read(ReplayStreamReader reader) throws IOException {
        int headerLength = reader.readUnsignedInt();
        int headerCRC = reader.readUnsignedInt();

        int engineVersion = reader.readUnsignedInt();
        int licenseeVersion = reader.readUnsignedInt();
        int netVersion = reader.readUnsignedInt();
        ReplayVersionData version = new ReplayVersionData(engineVersion, licenseeVersion, netVersion);

        // We can ignore this, it's not useful to us. We just need to verify that it exists
        if (!reader.readString().equals(REPLAY_OBJECT_CLASS_NAME)) {
            throw new IOException("Replay object class not found");
        }

        PropertyList properties = PropertyList.read(reader, version);

        int eofLength = reader.readUnsignedInt();
        int eofCRC = reader.readUnsignedInt();

        int levelCount = reader.readUnsignedInt();
        List<String> levels = new ArrayList<>(levelCount);
        for (int i = 0; i < levelCount; i++) {
            levels.add(reader.readString());
        }

        int keyframeCount = reader.readUnsignedInt();
        List<KeyFrame> keyframes = new ArrayList<>();
        for (int i = 0; i < keyframeCount; i++) {
            keyframes.add(KeyFrame.read(reader));
        }

        int networkStreamLength = reader.readUnsignedInt();

        return new ReplayHeader(headerLength, headerCRC, version, properties, eofLength, eofCRC, levels, keyframes, networkStreamLength);
    }

}
