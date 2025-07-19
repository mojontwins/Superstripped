package net.minecraft.src;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class WorldClient extends World {
	private LinkedList<WorldBlockPositionType> blocksToReceive = new LinkedList<WorldBlockPositionType>();
	private NetClientHandler sendQueue;
	private ChunkProviderClient chunkProviderClient;
	private IntHashMap entityHashSet = new IntHashMap();
	private Set<Entity> entityList = new HashSet<Entity>();
	private Set<Entity> entitySpawnQueue = new HashSet<Entity>();

	public WorldClient(NetClientHandler netClientHandler1, WorldSettings worldSettings2, int i3, int i4) {
		super(new SaveHandlerMP(), "MpServer", (WorldProvider)WorldProvider.getProviderForDimension(i3), (WorldSettings)worldSettings2);
		this.sendQueue = netClientHandler1;
		this.difficultySetting = i4;
		this.setSpawnPoint(new ChunkCoordinates(8, 64, 8));
	}

	public void tick() {
		this.setWorldTime(this.getWorldTime() + 1L);

		int i1;
		for(i1 = 0; i1 < 10 && !this.entitySpawnQueue.isEmpty(); ++i1) {
			Entity entity2 = (Entity)this.entitySpawnQueue.iterator().next();
			this.entitySpawnQueue.remove(entity2);
			if(!this.loadedEntityList.contains(entity2)) {
				this.spawnEntityInWorld(entity2);
			}
		}

		this.sendQueue.processReadPackets();

		for(i1 = 0; i1 < this.blocksToReceive.size(); ++i1) {
			WorldBlockPositionType worldBlockPositionType3 = (WorldBlockPositionType)this.blocksToReceive.get(i1);
			if(--worldBlockPositionType3.acceptCountdown == 0) {
				super.setBlockAndMetadata(worldBlockPositionType3.posX, worldBlockPositionType3.posY, worldBlockPositionType3.posZ, worldBlockPositionType3.blockID, worldBlockPositionType3.metadata);
				super.markBlockNeedsUpdate(worldBlockPositionType3.posX, worldBlockPositionType3.posY, worldBlockPositionType3.posZ);
				this.blocksToReceive.remove(i1--);
			}
		}

		this.chunkProviderClient.unload100OldestChunks();
		this.tickBlocksAndAmbiance();
	}

	public void invalidateBlockReceiveRegion(int i1, int i2, int i3, int i4, int i5, int i6) {
		for(int i7 = 0; i7 < this.blocksToReceive.size(); ++i7) {
			WorldBlockPositionType worldBlockPositionType8 = (WorldBlockPositionType)this.blocksToReceive.get(i7);
			if(worldBlockPositionType8.posX >= i1 && worldBlockPositionType8.posY >= i2 && worldBlockPositionType8.posZ >= i3 && worldBlockPositionType8.posX <= i4 && worldBlockPositionType8.posY <= i5 && worldBlockPositionType8.posZ <= i6) {
				this.blocksToReceive.remove(i7--);
			}
		}

	}

	protected IChunkProvider createChunkProvider() {
		this.chunkProviderClient = new ChunkProviderClient(this);
		return this.chunkProviderClient;
	}

	public void setSpawnLocation() {
		this.setSpawnPoint(new ChunkCoordinates(8, 64, 8));
	}

	protected void tickBlocksAndAmbiance() {
		this.buildPlayerListAndCheckLight();
		Iterator<?> iterator1 = this.activeChunkSet.iterator();

		while(iterator1.hasNext()) {
			ChunkCoordIntPair chunkCoordIntPair2 = (ChunkCoordIntPair)iterator1.next();
			int i3 = chunkCoordIntPair2.chunkXPos * 16;
			int i4 = chunkCoordIntPair2.chunkZPos * 16;
			//Profiler.startSection("getChunk");
			Chunk chunk5 = this.getChunkFromChunkCoords(chunkCoordIntPair2.chunkXPos, chunkCoordIntPair2.chunkZPos);
			this.tickChunks(i3, i4, chunk5);
			//Profiler.endSection();
		}

	}

	public void scheduleBlockUpdate(int i1, int i2, int i3, int i4, int i5) {
	}

	public boolean tickUpdates(boolean z1) {
		return false;
	}

	public void doPreChunk(int i1, int i2, boolean z3) {
		if(z3) {
			this.chunkProviderClient.loadChunk(i1, i2);
		} else {
			this.chunkProviderClient.func_539_c(i1, i2);
		}

		if(!z3) {
			this.markBlocksDirty(i1 * 16, 0, i2 * 16, i1 * 16 + 15, 256, i2 * 16 + 15);
		}

	}

	public boolean spawnEntityInWorld(Entity entity1) {
		boolean z2 = super.spawnEntityInWorld(entity1);
		this.entityList.add(entity1);
		if(!z2) {
			this.entitySpawnQueue.add(entity1);
		}

		return z2;
	}

	public void setEntityDead(Entity entity1) {
		super.setEntityDead(entity1);
		this.entityList.remove(entity1);
	}

	protected void obtainEntitySkin(Entity entity1) {
		super.obtainEntitySkin(entity1);
		if(this.entitySpawnQueue.contains(entity1)) {
			this.entitySpawnQueue.remove(entity1);
		}

	}

	protected void releaseEntitySkin(Entity entity1) {
		super.releaseEntitySkin(entity1);
		if(this.entityList.contains(entity1)) {
			if(entity1.isEntityAlive()) {
				this.entitySpawnQueue.add(entity1);
			} else {
				this.entityList.remove(entity1);
			}
		}

	}

	public void addEntityToWorld(int i1, Entity entity2) {
		Entity entity3 = this.getEntityByID(i1);
		if(entity3 != null) {
			this.setEntityDead(entity3);
		}

		this.entityList.add(entity2);
		entity2.entityId = i1;
		if(!this.spawnEntityInWorld(entity2)) {
			this.entitySpawnQueue.add(entity2);
		}

		this.entityHashSet.addKey(i1, entity2);
	}

	public Entity getEntityByID(int i1) {
		return (Entity)this.entityHashSet.lookup(i1);
	}

	public Entity removeEntityFromWorld(int i1) {
		Entity entity2 = (Entity)this.entityHashSet.removeObject(i1);
		if(entity2 != null) {
			this.entityList.remove(entity2);
			this.setEntityDead(entity2);
		}

		return entity2;
	}

	public boolean setBlockAndMetadataAndInvalidate(int i1, int i2, int i3, int i4, int i5) {
		this.invalidateBlockReceiveRegion(i1, i2, i3, i1, i2, i3);
		return super.setBlockAndMetadataWithNotify(i1, i2, i3, i4, i5);
	}

	public void sendQuittingDisconnectingPacket() {
		this.sendQueue.quitWithPacket(new Packet255KickDisconnect("Quitting"));
	}

	protected void updateWeather() {
		if(!this.worldProvider.hasNoSky) {
			if(this.lastLightningBolt > 0) {
				--this.lastLightningBolt;
			}

			this.prevRainingStrength = this.rainingStrength;
			if(this.worldInfo.isRaining()) {
				this.rainingStrength = (float)((double)this.rainingStrength + 0.01D);
			} else {
				this.rainingStrength = (float)((double)this.rainingStrength - 0.01D);
			}

			if(this.rainingStrength < 0.0F) {
				this.rainingStrength = 0.0F;
			}

			if(this.rainingStrength > 1.0F) {
				this.rainingStrength = 1.0F;
			}

			this.prevThunderingStrength = this.thunderingStrength;
			if(this.worldInfo.isThundering()) {
				this.thunderingStrength = (float)((double)this.thunderingStrength + 0.01D);
			} else {
				this.thunderingStrength = (float)((double)this.thunderingStrength - 0.01D);
			}

			if(this.thunderingStrength < 0.0F) {
				this.thunderingStrength = 0.0F;
			}

			if(this.thunderingStrength > 1.0F) {
				this.thunderingStrength = 1.0F;
			}

			/*
			if(this.worldProvider instanceof WorldProviderDesertDimension) {
				this.prevSandstormingStrength = this.sandstormingStrength;
				if(this.worldInfo.isSandstorming()) {
					this.sandstormingStrength = (float)((double)this.sandstormingStrength + 0.01D);
				} else {
					this.sandstormingStrength = (float)((double)this.sandstormingStrength - 0.01D);
				}

				if(this.sandstormingStrength < 0.0F) {
					this.sandstormingStrength = 0.0F;
				}

				if(this.sandstormingStrength > 1.0F) {
					this.sandstormingStrength = 1.0F;
				}
			}
			*/
		}
	}
}
