package wtf.choco.rljp;

import wtf.choco.rljp.structures.KeyFrame;
import wtf.choco.rljp.structures.ReplayHeader;
import wtf.choco.rljp.structures.properties.ArrayProperty;
import wtf.choco.rljp.structures.properties.BooleanProperty;
import wtf.choco.rljp.structures.properties.EnumProperty;
import wtf.choco.rljp.structures.properties.FloatProperty;
import wtf.choco.rljp.structures.properties.IntegerProperty;
import wtf.choco.rljp.structures.properties.LongProperty;
import wtf.choco.rljp.structures.properties.Property;
import wtf.choco.rljp.structures.properties.StringProperty;
import wtf.choco.rljp.structures.properties.StructProperty;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class Main {

    // https://github.com/tanrbobanr/rocket-league-replay-format/blob/main/rpdoc_generated.md

    private static final Path INPUT_PATH = Path.of("example.replay").toAbsolutePath();

    static void main() throws Exception {
        long now = System.currentTimeMillis();
        try (ReplayStreamReader reader = new ReplayStreamReader(Files.newInputStream(INPUT_PATH, StandardOpenOption.READ))) {
            ReplayHeader header = ReplayHeader.read(reader);
            System.out.println("Header Length: " + header.headerLength());
            System.out.println("Header CRC: " + header.headerCRC());
            System.out.println("Engine Version: " + header.version().engineVersion());
            System.out.println("Licensee Version: " + header.version().licenseeVersion());
            System.out.println("Net Version: " + header.version().netVersion());

            for (Property property : header.properties()) {
                printProperty(property, 0);
            }

            System.out.println("Levels:");
            for (String level : header.levels()) {
                System.out.println(" - " + level);
            }

            System.out.println("Key Frames:");
            for (KeyFrame keyframe : header.keyframes()) {
                System.out.println(" - " + keyframe);
            }

            System.out.println("Network Stream Length: " + header.networkStreamLength());
        }
        long duration = System.currentTimeMillis() - now;
        System.out.println("Read replay header in " + duration + "ms");
    }

    private static void printProperty(Property property, int nest) {
        String prefix = "  ".repeat(nest);
        if (property instanceof ArrayProperty arrayProperty) {
            System.out.println(prefix + property.getName() + " (" + property.getType() + "): ");
            arrayProperty.getProperties().forEach(nestedProperties -> {
                for (Property prop : nestedProperties) {
                    printProperty(prop, nest + 1);
                }
                System.out.println();
            });
        } else if (property instanceof StructProperty structProperty) {
            System.out.println(prefix + property.getName() + " (" + property.getType() + "): ");
            for (Property prop : structProperty.getProperties()) {
                printProperty(prop, nest + 1);
            }
            System.out.println();
        } else {
            System.out.println(prefix + property.getName() + " (" + property.getType() + "): " + switch (property) {
                case BooleanProperty booleanProperty -> booleanProperty.getValue();
                case EnumProperty enumProperty -> "Enum[" + enumProperty.getEnumTypeName() + "]=" + enumProperty.getEnumValueName();
                case FloatProperty floatProperty -> floatProperty.getValue();
                case IntegerProperty integerProperty -> integerProperty.getValue();
                case LongProperty longProperty -> longProperty.getValue();
                case StringProperty stringProperty -> stringProperty.getValue();
                default -> throw new IllegalStateException("Unexpected value: " + property);
            });
        }
    }

}
