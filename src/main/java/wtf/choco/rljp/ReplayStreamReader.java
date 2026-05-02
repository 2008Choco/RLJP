package wtf.choco.rljp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ReplayStreamReader implements AutoCloseable {

    private static final int BYTE_END = (Byte.SIZE - 1);

    private int currentByte;
    private int currentPosition = Byte.SIZE;

    private final InputStream stream;

    public ReplayStreamReader(InputStream stream) {
        this.stream = stream;
    }

    public void skip(int bytes) throws IOException {
        this.stream.skip(bytes);
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

    public int readBits(int length) throws IOException {
        int value = 0;

        for (int i = 0; i < length; i++) {
            if (readBit()) {
                value |= (1 << i);
            }
        }

        return value;
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
    public void close() throws Exception {
        this.stream.close();
    }

}
