package wtf.choco.rljp;

import wtf.choco.rljp.structures.ReplayHeader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * A wrapper for an {@link InputStream} to read data saved in the .replay file format designed by
 * Psyonix for the Rocket League game.
 * <p>
 * The most common use case for this class will be to wrap an input stream for a replay file and
 * immediately pass the instance to {@link ReplayHeader#read(ReplayStreamReader)} where it will
 * handle the parsing using the methods defined in this class. It's unlikely you, as the library
 * user, will need to use this class unless intending on manually parsing data from a replay file.
 * {@snippet lang="java" :
 * static void main() {
 *     Path path = Path.of("path/to/replay/file.replay");
 *     try (var reader = new ReplayStreamReader(Files.newInputStream(path))) {  // @highlight substring="new ReplayStreamReader"
 *         ReplayHeader header = ReplayHeader.read(reader);
 *         // Do what you want with your "header" object here!
 *     }
 * }
 * }
 * The caller is responsible for ensuring that the ReplayStreamReader is properly closed once all
 * data has been read. Closing a ReplayStreamReader with {@link #close()} (or via its auto-closing
 * mechanism) will also automatically close the wrapped {@link InputStream}.
 *
 * @see ReplayHeader#read(ReplayStreamReader)
 */
public final class ReplayStreamReader implements AutoCloseable {

    private int currentByte;
    private int currentPosition = Byte.SIZE;

    private final InputStream stream;

    /**
     * Construct a new {@link ReplayStreamReader} around an {@link InputStream}.
     *
     * @param stream the stream from which to read data
     */
    public ReplayStreamReader(InputStream stream) {
        this.stream = stream;
    }

    public boolean readBit() throws IOException {
        if (currentPosition >= Byte.SIZE) {
            this.currentByte = stream.read();
            this.currentPosition = 0;
        }

        boolean bit = (currentByte & (1 << currentPosition)) != 0;
        this.currentPosition++;
        return bit;
    }

    public int readUnsignedByte() throws IOException {
        byte value = 0;

        for (int i = 0; i < Byte.SIZE; i++) {
            if (readBit()) {
                value |= (byte) (1 << i);
            }
        }

        return value;
    }

    public boolean readBoolean() throws IOException {
        return readUnsignedByte() != 0;
    }

    public byte[] readUnsignedBytes(int length, boolean reverseBytes) throws IOException {
        byte[] bytes = new byte[length];

        if (reverseBytes) {
            for (int i = length - 1; i >= 0; i--) {
                bytes[i] = (byte) readUnsignedByte();
            }
        } else {
            for (int i = 0; i < length; i++) {
                bytes[i] = (byte) readUnsignedByte();
            }
        }

        return bytes;
    }

    public byte[] readUnsignedBytes(int length) throws IOException {
        return readUnsignedBytes(length, false);
    }

    public int readUnsignedInt() throws IOException {
        byte[] bytes = readUnsignedBytes(Integer.BYTES, true);

        int value = 0;
        for (int i = 0; i < Integer.BYTES; i++) {
            value = (value << Byte.SIZE) | Byte.toUnsignedInt(bytes[i]);
        }

        return value;
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readUnsignedInt());
    }

    public long readUnsignedLong() throws IOException {
        byte[] bytes = readUnsignedBytes(Long.BYTES, true);

        long value = 0;
        for (int i = 0; i < Long.BYTES; i++) {
            value = (value << Byte.SIZE) | Byte.toUnsignedInt(bytes[i]);
        }

        return value;
    }

    public String readString() throws IOException {
        int length = readUnsignedInt();
        if (length == 0) {
            return "";
        }

        if (length < 0) {
            // UTF-16
            length = Math.abs(length) * 2;
            byte[] bytes = readUnsignedBytes(length - 2, false);
            if ((readUnsignedByte() | readUnsignedByte()) != 0) {
                throw new IOException("UTF-16 string not null-terminated");
            }

            return new String(bytes, StandardCharsets.UTF_16LE);
        } else {
            // UTF-8
            byte[] bytes = readUnsignedBytes(length - 1, false);
            if (readUnsignedByte() != 0) {
                throw new IOException("UTF-8 string not null-terminated");
            }

            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }

}
