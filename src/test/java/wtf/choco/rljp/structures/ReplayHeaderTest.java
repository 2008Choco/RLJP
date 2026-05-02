package wtf.choco.rljp.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import wtf.choco.rljp.ReplayStreamReader;
import wtf.choco.rljp.structures.properties.Property;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReplayHeaderTest {

    private static final Path RESOURCE_DIR = Path.of("src/test/resources");

    static Stream<String> allReplayFiles() throws IOException {
        try (Stream<Path> files = Files.list(RESOURCE_DIR)) {
            return files
                .map(path -> path.getFileName().toString())
                .filter(fileName -> fileName.endsWith(".replay"))
                .toList()
                .stream();
        }
    }

    @ParameterizedTest
    @MethodSource("allReplayFiles")
    @DisplayName("ReplayHeader parses each test replay without exception and with valid structure")
    void testReplayHeaderParsing(String replayFile) throws Exception {
        Path path = RESOURCE_DIR.resolve(replayFile);
        try (InputStream in = Files.newInputStream(path);
            ReplayStreamReader reader = new ReplayStreamReader(in)) {

            // Header parsing validation
            ReplayHeader header = assertDoesNotThrow(() -> ReplayHeader.read(reader), () -> "Parsing should not throw for " + replayFile);
            assertNotNull(header, () -> "ReplayHeader should not be null for " + replayFile);

            // Header root-level field validation
            assertTrue(header.headerLength() > 0, () -> "Header length should be positive for " + replayFile);
            assertNotNull(header.version(), () -> "Version should not be null for " + replayFile);
            assertNotNull(header.properties(), () -> "Properties should not be null for " + replayFile);
            assertNotNull(header.levels(), () -> "Levels should not be null for " + replayFile);
            assertNotNull(header.keyframes(), () -> "Keyframes should not be null for " + replayFile);
            assertTrue(header.networkStreamLength() > 0, () -> "Network stream length should be positive for " + replayFile);

            // Property validation
            for (Property property : header.properties()) {
                assertNotNull(property, () -> "Property should not be null in " + replayFile);
                assertNotNull(property.getName(), () -> "Property name should not be null in " + replayFile + " for property: " + property);
                assertNotNull(property.getType(), () -> "Property type should not be null in " + replayFile + " for property: " + property);
                assertTrue(property.getArrayIndex() >= 0, () -> "Property array index should be non-negative in " + replayFile + " for property: " + property);
                // Some properties (specifically 'bForfeit', from my testing) can actually have a data length of 0!
                assertTrue(property.getDataLength() >= 0, () -> "Property data length should be non-negative in " + replayFile + " for property: " + property);
            }

            // Key frame validation
            for (KeyFrame keyframe : header.keyframes()) {
                assertNotNull(keyframe, () -> "KeyFrame should not be null in " + replayFile);
                assertTrue(keyframe.filePosition() >= 0, "KeyFrame file position should be non-negative in " + replayFile + " for keyframe: " + keyframe);
                assertTrue(keyframe.frame() >= 0, "KeyFrame frame should be non-negative in " + replayFile + " for keyframe: " + keyframe);
                assertTrue(keyframe.time() >= 0, "KeyFrame time should be non-negative in " + replayFile + " for keyframe: " + keyframe);
            }
        }
    }
}
