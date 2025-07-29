package net.minecraft.src;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

public class WorldInfo {
	private long randomSeed;
	private WorldType terrainType = WorldType.ALPHA;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private long worldTime;
	private long lastTimePlayed;
	private long sizeOnDisk;
	private NBTTagCompound playerTag;
	private int dimension;
	private String levelName;
	private int saveVersion;
	
	private boolean raining;
	private int rainTime;
	private boolean thundering;
	private int thunderTime;
	private boolean snowing;
	private int snowingTime;
	
	private boolean sandstorming;
	private int sandstormingTime;
	
	private int gameType;
	private boolean mapFeaturesEnabled;
	private boolean hardcore = false;
	private boolean enableCheats = false;
	private boolean snowCovered = false;
	private boolean enableSeasons = true;

	public WorldInfo(NBTTagCompound nBTWorldInfo) {
		this.randomSeed = nBTWorldInfo.getLong("RandomSeed");
		this.snowCovered = nBTWorldInfo.getBoolean("SnowCovered");
		
		if(nBTWorldInfo.hasKey("generatorName")) {
			String string2 = nBTWorldInfo.getString("generatorName");

			this.terrainType = WorldType.parseWorldType(string2);
			
			if(this.terrainType == null) {
				this.terrainType = WorldType.ALPHA;
			} else if(this.terrainType.func_48626_e()) {
				int i3 = 0;
				if(nBTWorldInfo.hasKey("generatorVersion")) {
					i3 = nBTWorldInfo.getInteger("generatorVersion");
				}

				this.terrainType = this.terrainType.func_48629_a(i3);
			}
		} else if(this.snowCovered) {
			this.terrainType = WorldType.ALPHA_SNOW;
		}

		if(nBTWorldInfo.hasKey("gameType")) {
			this.gameType = nBTWorldInfo.getInteger("GameType");
		} else {
			this.gameType = 0;
		}
		
		if(nBTWorldInfo.hasKey("MapFeatures")) {
			this.mapFeaturesEnabled = nBTWorldInfo.getBoolean("MapFeatures");
		} else {
			this.mapFeaturesEnabled = true;
		}
		
		this.enableCheats = nBTWorldInfo.getBoolean("EnableCheats");
		
		this.spawnX = nBTWorldInfo.getInteger("SpawnX");
		this.spawnY = nBTWorldInfo.getInteger("SpawnY");
		this.spawnZ = nBTWorldInfo.getInteger("SpawnZ");
		this.worldTime = nBTWorldInfo.getLong("Time");
		this.lastTimePlayed = nBTWorldInfo.getLong("LastPlayed");
		this.sizeOnDisk = nBTWorldInfo.getLong("SizeOnDisk");
		this.levelName = nBTWorldInfo.getString("LevelName");
		this.saveVersion = nBTWorldInfo.getInteger("version");
		this.rainTime = nBTWorldInfo.getInteger("rainTime");
		this.raining = nBTWorldInfo.getBoolean("raining");
		this.thunderTime = nBTWorldInfo.getInteger("thunderTime");
		this.thundering = nBTWorldInfo.getBoolean("thundering");
		this.snowingTime = nBTWorldInfo.getInteger("snowingTime");
		this.snowing = nBTWorldInfo.getBoolean("snowing");
		this.thunderTime = nBTWorldInfo.getInteger("thunderTime");
		this.thundering = nBTWorldInfo.getBoolean("thundering");
		this.sandstorming = nBTWorldInfo.getBoolean("sandstorming");
		this.sandstormingTime = nBTWorldInfo.getInteger("sandstormingTime");
		
		if(nBTWorldInfo.hasKey("DayOfTheYear")) {
			Seasons.dayOfTheYear = nBTWorldInfo.getInteger("DayOfTheYear");
		} else if(this.snowCovered) {
			Seasons.dayOfTheYear = 0;
		} else {
			Seasons.dayOfTheYear = Seasons.SEASON_DURATION * 2;
		}
		
		this.hardcore = nBTWorldInfo.getBoolean("hardcore");
		
		this.enableSeasons = nBTWorldInfo.getBoolean("EnableSeasons");
		
		GameRules.loadRules(nBTWorldInfo);
		
		if(nBTWorldInfo.hasKey("Player")) {
			this.playerTag = nBTWorldInfo.getCompoundTag("Player");
			this.dimension = this.playerTag.getInteger("Dimension");
		}


	}

	public String toString() {
		return  "Level Name:" + this.levelName +", RandomSeed: " + this.randomSeed + ", Terrain: " + this.terrainType + ", sizeOnDisk: " + this.sizeOnDisk + ", player: " + this.playerTag;
	}
	
	public WorldInfo(WorldSettings worldSettings, String levelName) {
		this.randomSeed = worldSettings.getSeed();
		this.gameType = worldSettings.getGameType();
		this.mapFeaturesEnabled = worldSettings.isMapFeaturesEnabled();
		this.levelName = levelName;
		this.hardcore = worldSettings.getHardcoreEnabled();
		this.enableCheats = worldSettings.isEnableCheats();
		this.terrainType = worldSettings.getTerrainType();
		this.snowCovered = worldSettings.isSnowCovered();
		this.enableSeasons = worldSettings.isEnableSeasons();
	}

	public WorldInfo(WorldInfo worldInfo) {
		this.randomSeed = worldInfo.randomSeed;
		this.terrainType = worldInfo.terrainType;
		this.gameType = worldInfo.gameType;
		this.mapFeaturesEnabled = worldInfo.mapFeaturesEnabled;
		this.enableCheats = worldInfo.enableCheats;
		this.spawnX = worldInfo.spawnX;
		this.spawnY = worldInfo.spawnY;
		this.spawnZ = worldInfo.spawnZ;
		this.worldTime = worldInfo.worldTime;
		this.lastTimePlayed = worldInfo.lastTimePlayed;
		this.sizeOnDisk = worldInfo.sizeOnDisk;
		this.playerTag = worldInfo.playerTag;
		this.dimension = worldInfo.dimension;
		this.levelName = worldInfo.levelName;
		this.saveVersion = worldInfo.saveVersion;
		this.rainTime = worldInfo.rainTime;
		this.raining = worldInfo.raining;
		this.thunderTime = worldInfo.thunderTime;
		this.thundering = worldInfo.thundering;
		this.snowingTime = worldInfo.snowingTime;;
		this.snowing = worldInfo.snowing;
		this.sandstormingTime = worldInfo.sandstormingTime;
		this.sandstorming = worldInfo.sandstorming;
		this.hardcore = worldInfo.hardcore;
		this.snowCovered = worldInfo.snowCovered;
		this.enableSeasons = worldInfo.enableSeasons;
	}

	public NBTTagCompound getNBTTagCompound() {
		NBTTagCompound nBTWorldInfo = new NBTTagCompound();
		this.updateTagCompound(nBTWorldInfo, this.playerTag);
		return nBTWorldInfo;
	}

	public NBTTagCompound getNBTTagCompoundWithPlayers(List<EntityPlayer> list1) {
		NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
		EntityPlayer entityPlayer3 = null;
		NBTTagCompound nBTTagCompound4 = null;
		if(list1.size() > 0) {
			entityPlayer3 = (EntityPlayer)list1.get(0);
		}

		if(entityPlayer3 != null) {
			nBTTagCompound4 = new NBTTagCompound();
			entityPlayer3.writeToNBT(nBTTagCompound4);
		}

		this.updateTagCompound(nBTTagCompound2, nBTTagCompound4);
		return nBTTagCompound2;
	}

	private void updateTagCompound(NBTTagCompound nBTWorldInfo, NBTTagCompound nBTPlayer) {
		nBTWorldInfo.setLong("RandomSeed", this.randomSeed);
		nBTWorldInfo.setString("generatorName", this.terrainType.getWorldTypeName());
		nBTWorldInfo.setInteger("generatorVersion", this.terrainType.getGeneratorVersion());
		nBTWorldInfo.setInteger("GameType", this.gameType);
		nBTWorldInfo.setBoolean("MapFeatures", this.mapFeaturesEnabled);
		nBTWorldInfo.setBoolean("EnableCheats", this.enableCheats);
		nBTWorldInfo.setInteger("SpawnX", this.spawnX);
		nBTWorldInfo.setInteger("SpawnY", this.spawnY);
		nBTWorldInfo.setInteger("SpawnZ", this.spawnZ);
		nBTWorldInfo.setLong("Time", this.worldTime);
		nBTWorldInfo.setLong("SizeOnDisk", this.sizeOnDisk);
		nBTWorldInfo.setLong("LastPlayed", System.currentTimeMillis());
		nBTWorldInfo.setString("LevelName", this.levelName);
		nBTWorldInfo.setInteger("version", this.saveVersion);
		nBTWorldInfo.setInteger("rainTime", this.rainTime);
		nBTWorldInfo.setBoolean("raining", this.raining);
		nBTWorldInfo.setInteger("thunderTime", this.thunderTime);
		nBTWorldInfo.setBoolean("thundering", this.thundering);
		nBTWorldInfo.setInteger("snowingTime", this.snowingTime);
		nBTWorldInfo.setBoolean("snowing", this.snowing);
		nBTWorldInfo.setInteger("sandstromingTime", this.sandstormingTime);
		nBTWorldInfo.setBoolean("sandstorming", this.sandstorming);
		nBTWorldInfo.setInteger("DayOfTheYear", Seasons.dayOfTheYear);
		nBTWorldInfo.setBoolean("hardcore", this.hardcore);
		nBTWorldInfo.setBoolean("SnowCovered", this.snowCovered);
		nBTWorldInfo.setBoolean("EnableSeasons", this.enableSeasons);

		GameRules.saveRules(nBTWorldInfo);
		
		if(nBTPlayer != null) {
			nBTWorldInfo.setCompoundTag("Player", nBTPlayer);
		}

	}

	public boolean isSnowCovered() {
		return snowCovered;
	}

	public void setSnowCovered(boolean snowCovered) {
		this.snowCovered = snowCovered;
	}

	public long getSeed() {
		return this.randomSeed;
	}

	public int getSpawnX() {
		return this.spawnX;
	}

	public int getSpawnY() {
		return this.spawnY;
	}

	public int getSpawnZ() {
		return this.spawnZ;
	}

	public long getWorldTime() {
		return this.worldTime;
	}

	public long getSizeOnDisk() {
		return this.sizeOnDisk;
	}

	public NBTTagCompound getPlayerNBTTagCompound() {
		return this.playerTag;
	}

	public int getDimension() {
		return this.dimension;
	}

	public void setSpawnX(int i1) {
		this.spawnX = i1;
	}

	public void setSpawnY(int i1) {
		this.spawnY = i1;
	}

	public void setSpawnZ(int i1) {
		this.spawnZ = i1;
	}

	public void setWorldTime(long j1) {
		this.worldTime = j1;
	}

	public void setPlayerNBTTagCompound(NBTTagCompound nBTPlayer) {
		this.playerTag = nBTPlayer;
	}

	public void setSpawnPosition(int x, int y, int z) {
		this.spawnX = x;
		this.spawnY = y;
		this.spawnZ = z;
	}

	public String getWorldName() {
		return this.levelName;
	}

	public void setWorldName(String string1) {
		this.levelName = string1;
	}

	public int getSaveVersion() {
		return this.saveVersion;
	}

	public void setSaveVersion(int i1) {
		this.saveVersion = i1;
	}

	public long getLastTimePlayed() {
		return this.lastTimePlayed;
	}

	public boolean isThundering() {
		return this.thundering;
	}

	public void setThundering(boolean z1) {
		this.thundering = z1;
	}

	public int getThunderTime() {
		return this.thunderTime;
	}

	public void setThunderTime(int i1) {
		this.thunderTime = i1;
	}

	public boolean isRaining() {
		return this.raining;
	}

	public void setRaining(boolean z1) {
		this.raining = z1;
	}

	public int getRainTime() {
		return this.rainTime;
	}

	public void setRainTime(int i1) {
		this.rainTime = i1;
	}
	
	public boolean isSandstorming() {
		return this.sandstorming;
	}
	
	public void setSandstorming(boolean sandstorming) {
		this.sandstorming = sandstorming; 
	}
	
	public int getSandstormingTime() {
		return this.sandstormingTime;
	}
	
	public void setSandstormingTime(int sandstormingTime) {
		this.sandstormingTime = sandstormingTime;
	}

	public int getGameType() {
		return this.gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	public boolean isMapFeaturesEnabled() {
		return this.mapFeaturesEnabled;
	}
	
	public boolean isEnableCheats() {
		return this.enableCheats;
	}

	public boolean isHardcoreModeEnabled() {
		return this.hardcore;
	}

	public WorldType getTerrainType() {
		return this.terrainType;
	}

	public void setTerrainType(WorldType worldType1) {
		this.terrainType = worldType1;
	}

	public boolean isSnowing() {
		return snowing;
	}

	public void setSnowing(boolean snowing) {
		this.snowing = snowing;
	}

	public int getSnowingTime() {
		return snowingTime;
	}

	public void setSnowingTime(int snowingTime) {
		this.snowingTime = snowingTime;
	}

	public boolean isEnableSeasons() {
		return enableSeasons;
	}

	public void setEnableSeasons(boolean enableSeasons) {
		this.enableSeasons = enableSeasons;
	}
}
