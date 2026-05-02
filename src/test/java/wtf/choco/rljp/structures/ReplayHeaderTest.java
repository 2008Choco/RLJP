package wtf.choco.rljp.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.properties.Property;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

class ReplayHeaderTest {

    private static final Path RESOURCE_DIR = Path.of("src/test/resources");

    static Stream<String> replayFiles() throws IOException {
        try (Stream<Path> files = Files.list(RESOURCE_DIR)) {
            return files
                .filter(p -> p.getFileName().toString().endsWith(".replay"))
                .map(p -> p.getFileName().toString())
                .toList()
                .stream();
        }
    }

    @ParameterizedTest
    @MethodSource("replayFiles")
    @DisplayName("ReplayHeader parses each test replay without exception and with valid structure")
    void testReplayHeaderParsing(String replayFile) throws Exception {
        Path path = RESOURCE_DIR.resolve(replayFile);
        try (InputStream in = Files.newInputStream(path);
            ReplayStreamReader reader = new ReplayStreamReader(in)) {
            ReplayHeader header = ReplayHeader.read(reader);
            Assertions.assertNotNull(header, "ReplayHeader should not be null for " + replayFile);
            Assertions.assertTrue(header.headerLength() > 0, "Header length should be positive for " + replayFile);
            Assertions.assertTrue(header.headerCRC() >= 0, "Header CRC should be non-negative for " + replayFile);
            Assertions.assertNotNull(header.version(), "Version should not be null for " + replayFile);
            Assertions.assertNotNull(header.properties(), "Properties should not be null for " + replayFile);
            Assertions.assertNotNull(header.levels(), "Levels should not be null for " + replayFile);
            Assertions.assertNotNull(header.keyframes(), "Keyframes should not be null for " + replayFile);
            Assertions.assertTrue(header.networkStreamLength() > 0, "Network stream length should be positive for " + replayFile);
            for (Property property : header.properties()) {
                Assertions.assertNotNull(property, "Property should not be null in " + replayFile);
            }
            for (KeyFrame keyframe : header.keyframes()) {
                Assertions.assertNotNull(keyframe, "KeyFrame should not be null in " + replayFile);
            }
        }
    }
}
