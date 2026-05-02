package wtf.choco.rljp.structures;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.properties.PropertyList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A header definition for a .replay file (as defined in the Rocket League .replay file spec).
 *
 * @param headerLength The length of the header in bytes
 * @param headerCRC The header's CRC value (see <a href="https://github.com/tanrbobanr/rocket-league-replay-format/blob/main/rpdoc_generated.md#-71verifying-replay-integrity-with-cyclic-redundancy-checks">Verifying Replay Integrity with Cyclic Redundancy Checks</a>)
 * @param version The {@link ReplayVersionData replay version data}
 * @param properties All defined {@link PropertyList properties}
 * @param eofLength {@literal <unknown property. documentation unclear>}
 * @param eofCRC {@literal <unknown property. documentation unclear>}
 * @param levels The Rocket League levels used by this replay. Not to be confused with the map id, which is a property
 * @param keyframes A list of keyframes defined in this replay
 * @param networkStreamLength The length of the network stream in bytes
 */
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
