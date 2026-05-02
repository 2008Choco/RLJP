package wtf.choco.rljp.structures;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

/**
 * Represents a keyframe in a replay.
 *
 * @param time the time at which this keyframe is positioned
 * @param frame the frame at which the keyframe is positioned
 * @param filePosition the file position in the network stream where this keyframe is positioned
 */
public record KeyFrame(float time, int frame, int filePosition) {

    public static KeyFrame read(ReplayStreamReader reader) throws IOException {
        float time = reader.readFloat();
        int frame = reader.readUnsignedInt();
        int filePosition = reader.readUnsignedInt();
        return new KeyFrame(time, frame, filePosition);
    }

}
