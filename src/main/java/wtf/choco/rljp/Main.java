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
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public final class Main {

    // Roughly documented replay format on which this parser is based. May or may not be up-to-date!
    // https://github.com/tanrbobanr/rocket-league-replay-format/blob/main/rpdoc_generated.md

    static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Missing required argument to path to replay file!");
            return;
        }

        Path path = Paths.get(args[0]);
        if (Files.notExists(path)) {
            System.err.println("File at path \"" + args[0] + "\" could not be located!");
            return;
        }

        if (!path.getFileName().toString().endsWith(".replay")) {
            System.err.println("File at path \"" + args[0] + "\" is not a .replay file!");
            return;
        }

        System.out.println("Reading replay file at " + path.toAbsolutePath() + "... Please wait...");
        long now = System.currentTimeMillis();
        try (ReplayStreamReader reader = new ReplayStreamReader(Files.newInputStream(path))) {
            ReplayHeader header = ReplayHeader.read(reader);
            long duration = System.currentTimeMillis() - now;
            System.out.println("Done reading replay header in " + duration + "ms. Here's the data we found:");
            System.out.println();

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
