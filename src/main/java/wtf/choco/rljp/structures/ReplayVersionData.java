package wtf.choco.rljp.structures;

/**
 * Replay version data as part of the {@link ReplayHeader} format.
 *
 * @param engineVersion The game engine version
 * @param licenseeVersion The licensee version
 * @param netVersion The netcode version
 */
public record ReplayVersionData(int engineVersion, int licenseeVersion, int netVersion) {  }
