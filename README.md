# RLJP (Rocket League Java Parser)
This project is a library to assist in parsing Rocket League .replay files directly in-memory. This program is as up-to-date as Rocket League v2.66 (Season 22).

## What is this?
Rocket League allows players to save their games so they can rewatch them later. These are called replays and are saved in files with the .replay extension. These files are binary and contain all the information about the game, such as player positions, ball trajectory, and more. This library provides a way to read and parse these files in Java, allowing developers to extract useful information from them.

Currently, RLJP only parses the header portion of replay files as that contains most of the useful information. The body and footer of the replay files are less important. Unless you intend on recreating parts of the match, analyzing statistics about the game such (e.g. ball possession, aggressive/defensive stats, or other information that the header does not provide), the rest of the replay file is often unnecessary.

## Why not other replay parsers?
Well, you can use other replay parsers! In fact, this one may not be as good as others and may skip a few steps in favour of better performance. RLJP is designed to be very fast and does not export its data to JSON like other parsers do. This library is meant to be all in-memory for Java applications to filter through replay data as POJOs. The alternative for Java applications is to use a third-party standalone application to parse a replay file, export it to JSON, then read and parse the JSON output. Or make use of native calls, which is never ideal when making a platform-agnostic application.

RLJP is not meant to replace other replay parsers and likely will never surpass their rigorousness or speed. Therefore, it is encouraged to use them over RLJP if you don't have an express need to have replay header data as in-memory Java objects.

Here are a few replay parsers that I recommend (and which helped while writing RLJP!) in no particular order:
- [RocketLeagueReplayParser](https://github.com/jjbott/RocketLeagueReplayParser/) by jjbott - Written in C#, is a standalone app, or a library for C# developers
- [Boxcars](https://github.com/nickbabcock/boxcars/) by nickbabcock - Written in Rust, is a blazingly fast replay parser
- [RocketRP](https://github.com/Drogebot/RocketRP) by Drogebot - Written in C#, is a standalone parser that exports to JSON
- [CPPRP](https://github.com/Bakkes/CPPRP) by Bakkes - Written in C++, was the core library parsing library for the famous, now EOL'd, BakkesMod
- [Rattletrap](https://github.com/tfausak/rattletrap) by tfausak - Written in Haskell, but has reached end of maintenance as of mid-2025

There are likely other very popular replay parsers and I strongly encourage you to check them out. They're really the backbone of replay-based programs.

## So how do I use it?
There are two ways to use RLJP. You can either run this as an executable program, or you can include it in your project.

### Running As Executable
Running this program as an executable will get you some top-level information about the header. In order to run this program, you must have Maven and Java 25 installed.

Firstly, this program will need to be compiled into a binary. You can do that with the following command in the project's root directory:
```
mvn clean package
```
This will create a .jar file in the `target` directory. You can run this .jar file with the following command:
```
java -jar target/RLJP-1.0-SNAPSHOT.jar <path_to_replay_file>
```

### Dependency Information
#### Maven
```xml
<repositories>
    <repository>
        <id>Choco Repository</id>
        <url>https://repo.choco.wtf/snapshots</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>wtf.choco</groupId>
        <artifactId>RLJP</artifactId>
        <!-- This version information will likely change! -->
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

#### Gradle (Groovy)
```groovy
repositories {
    maven { url 'https://repo.choco.wtf/snapshots' }
}

dependencies {
    implementation 'wtf.choco:RLJP:1.0-SNAPSHOT'
}
```

#### Gradle (Kotlin)
```kotlin
repositories {
    maven("https://repo.choco.wtf/snapshots")
}

dependencies {
    implementation("wtf.choco:RLJP:1.0-SNAPSHOT")
}
```

This project is licensed as MIT, therefore you can do with this project whatever you please. Distribute it however you'd like and in whatever form you'd like. I would appreciate credit if you do, but you are not required to do so by the license.

### Example Snippet
You can find an example of how to use this library either in the [Main](src/main/java/wtf/choco/rljp/Main.java) file, or in the [ReplayHeaderTest](src/test/java/wtf/choco/rljp/structures/ReplayHeaderTest.java) file. But here's a brief snippet of how you might want to use this library:
```java
static void example() {
    Path path = Path.of("path/to/replay/file.replay");
    try (ReplayStreamReader reader = new ReplayStreamReader(Files.newBufferedReader(path, StandardCharsets.UTF_16))) {
        ReplayHeader header = ReplayHeader.read(reader);

        String replayId = header.properties().tryProperty("Id", StringProperty.class)
                .map(StringProperty::value)
                .orElse("Unknown replay ID!");
        System.out.println("Replay ID: " + replayId);

        // There's lots of information available in the header. See ReplayHeader for all that's available
    } catch (IOException e) {
        // Something went wrong here!
    }
}
```

## Credits
Thank you to the following resources which helped develop this program. They were absolutely vital in understanding the replay format where Psyonix has failed to provide official documentation for modders:
- [Rocket League Replay Format](https://github.com/tanrbobanr/rocket-league-replay-format/blob/main/rpdoc_generated.md) by tanrbobanr - A comprehensive community documentation of all that's known about the .replay file format
- [RocketLeagueReplayParser](https://github.com/jjbott/RocketLeagueReplayParser/) by jjbott - A C# replay parser that helped explain some quirks of the .replay format that the aforementioned documentation omitted
