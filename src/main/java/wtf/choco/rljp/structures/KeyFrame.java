package wtf.choco.rljp.structures;

import wtf.choco.rljp.ReplayStreamReader;

import java.io.IOException;

public record KeyFrame(float time, int frame, int filePosition) {

    public static KeyFrame read(ReplayStreamReader reader) throws IOException {
        float time = reader.readFloat();
        int frame = reader.readUnsignedInt();
        int filePosition = reader.readUnsignedInt();
        return new KeyFrame(time, frame, filePosition);
    }

}
