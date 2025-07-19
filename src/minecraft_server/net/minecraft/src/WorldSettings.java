package net.minecraft.src;

public final class WorldSettings {
	private final long seed;
	private final int gameType;
	private final boolean mapFeaturesEnabled;
	private final boolean hardcoreEnabled;
	private final boolean enableCheats;
	private final boolean snowCovered;
	private final WorldType terrainType;

	public WorldSettings(long seed, int gameMode, boolean features, boolean hardcore, boolean cheats, WorldType worldType) {
		this.seed = seed;
		this.gameType = gameMode;
		this.mapFeaturesEnabled = features;
		this.hardcoreEnabled = hardcore;
		this.terrainType = worldType;
		this.enableCheats = cheats;
		this.snowCovered = false;
	}
	
	// Alpha new world uses this constructor:
	public WorldSettings(long seed, int gameMode, boolean cheats, boolean snowCovered, WorldType worldType) {
		this.seed = seed;
		this.gameType = gameMode;
		this.mapFeaturesEnabled = true;
		this.hardcoreEnabled = false;
		this.enableCheats = cheats;
		this.terrainType = worldType;
		this.snowCovered = snowCovered;
	}
	
	public long getSeed() {
		return this.seed;
	}

	public int getGameType() {
		return this.gameType;
	}

	public boolean getHardcoreEnabled() {
		return this.hardcoreEnabled;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}

	public WorldType getTerrainType() {
		return this.terrainType;
	}

	public static int validGameType(int i0) {
		switch(i0) {
		case 0:
		case 1:
			return i0;
		default:
			return 0;
		}
	}

	public boolean isEnableCheats() {
		return enableCheats;
	}

	public boolean isSnowCovered() {
		return snowCovered;
	}
}
