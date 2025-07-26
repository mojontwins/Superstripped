package net.minecraft.src;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AnvilChunkLoader implements IThreadedFileIO, IChunkLoader {
	private List<AnvilChunkLoaderPending> anvilChunkLoaderPending = new ArrayList<AnvilChunkLoaderPending>();
	private Set<ChunkCoordIntPair> chunkCoordIntPairList = new HashSet<ChunkCoordIntPair>();
	private Object field_48450_c = new Object();
	private final File chunkSaveLocation;

	public AnvilChunkLoader(File file1) {
		this.chunkSaveLocation = file1;
	}

	public Chunk loadChunk(World world1, int i2, int i3) throws IOException {
		NBTTagCompound nBTTagCompound4 = null;
		ChunkCoordIntPair chunkCoordIntPair5 = new ChunkCoordIntPair(i2, i3);
		synchronized(this.field_48450_c) {
			if(this.chunkCoordIntPairList.contains(chunkCoordIntPair5)) {
				for(int i7 = 0; i7 < this.anvilChunkLoaderPending.size(); ++i7) {
					if(((AnvilChunkLoaderPending)this.anvilChunkLoaderPending.get(i7)).field_48427_a.equals(chunkCoordIntPair5)) {
						nBTTagCompound4 = ((AnvilChunkLoaderPending)this.anvilChunkLoaderPending.get(i7)).field_48426_b;
						break;
					}
				}
			}
		}

		if(nBTTagCompound4 == null) {
			DataInputStream dataInputStream10 = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, i2, i3);
			if(dataInputStream10 == null) {
				return null;
			}

			nBTTagCompound4 = CompressedStreamTools.read((DataInput)dataInputStream10);
		}

		return this.func_48443_a(world1, i2, i3, nBTTagCompound4);
	}

	protected Chunk func_48443_a(World world1, int i2, int i3, NBTTagCompound nBTTagCompound4) {
		if(!nBTTagCompound4.hasKey("Level")) {
			System.out.println("Chunk file at " + i2 + "," + i3 + " is missing level data, skipping");
			return null;
		} else if(!nBTTagCompound4.getCompoundTag("Level").hasKey("Sections")) {
			System.out.println("Chunk file at " + i2 + "," + i3 + " is missing block data, skipping");
			return null;
		} else {
			Chunk chunk5 = this.func_48444_a(world1, nBTTagCompound4.getCompoundTag("Level"));
			if(!chunk5.isAtLocation(i2, i3)) {
				System.out.println("Chunk file at " + i2 + "," + i3 + " is in the wrong location; relocating. (Expected " + i2 + ", " + i3 + ", got " + chunk5.xPosition + ", " + chunk5.zPosition + ")");
				nBTTagCompound4.setInteger("xPos", i2);
				nBTTagCompound4.setInteger("zPos", i3);
				chunk5 = this.func_48444_a(world1, nBTTagCompound4.getCompoundTag("Level"));
			}

			chunk5.removeUnknownBlocks();
			return chunk5;
		}
	}

	public void saveChunk(World world1, Chunk chunk2) {
		world1.checkSessionLock();

		try {
			NBTTagCompound nBTTagCompound3 = new NBTTagCompound();
			NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
			nBTTagCompound3.setTag("Level", nBTTagCompound4);
			this.func_48445_a(chunk2, world1, nBTTagCompound4);
			this.func_48446_a(chunk2.getChunkCoordIntPair(), nBTTagCompound3);
		} catch (Exception exception5) {
			exception5.printStackTrace();
		}

	}

	protected void func_48446_a(ChunkCoordIntPair chunkCoordIntPair1, NBTTagCompound nBTTagCompound2) {
		synchronized(this.field_48450_c) {
			if(this.chunkCoordIntPairList.contains(chunkCoordIntPair1)) {
				for(int i4 = 0; i4 < this.anvilChunkLoaderPending.size(); ++i4) {
					if(((AnvilChunkLoaderPending)this.anvilChunkLoaderPending.get(i4)).field_48427_a.equals(chunkCoordIntPair1)) {
						this.anvilChunkLoaderPending.set(i4, new AnvilChunkLoaderPending(chunkCoordIntPair1, nBTTagCompound2));
						return;
					}
				}
			}

			this.anvilChunkLoaderPending.add(new AnvilChunkLoaderPending(chunkCoordIntPair1, nBTTagCompound2));
			this.chunkCoordIntPairList.add(chunkCoordIntPair1);
			ThreadedFileIOBase.threadedIOInstance.queueIO(this);
		}
	}

	public boolean writeNextIO() {
		AnvilChunkLoaderPending anvilChunkLoaderPending1 = null;
		synchronized(this.field_48450_c) {
			if(this.anvilChunkLoaderPending.size() <= 0) {
				return false;
			}

			anvilChunkLoaderPending1 = (AnvilChunkLoaderPending)this.anvilChunkLoaderPending.remove(0);
			this.chunkCoordIntPairList.remove(anvilChunkLoaderPending1.field_48427_a);
		}

		if(anvilChunkLoaderPending1 != null) {
			try {
				this.func_48447_a(anvilChunkLoaderPending1);
			} catch (Exception exception4) {
				exception4.printStackTrace();
			}
		}

		return true;
	}

	private void func_48447_a(AnvilChunkLoaderPending anvilChunkLoaderPending1) throws IOException {
		DataOutputStream dataOutputStream2 = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, anvilChunkLoaderPending1.field_48427_a.chunkXPos, anvilChunkLoaderPending1.field_48427_a.chunkZPos);
		CompressedStreamTools.write(anvilChunkLoaderPending1.field_48426_b, (DataOutput)dataOutputStream2);
		dataOutputStream2.close();
	}

	public void saveExtraChunkData(World world1, Chunk chunk2) {
	}

	public void chunkTick() {
	}

	public void saveExtraData() {
	}

	private void func_48445_a(Chunk chunk1, World world2, NBTTagCompound nBTTagCompound3) {
		world2.checkSessionLock();
		nBTTagCompound3.setInteger("xPos", chunk1.xPosition);
		nBTTagCompound3.setInteger("zPos", chunk1.zPosition);
		nBTTagCompound3.setLong("LastUpdate", world2.getWorldTime());
		nBTTagCompound3.func_48183_a("HeightMap", chunk1.heightMap);
		nBTTagCompound3.setBoolean("TerrainPopulated", chunk1.isTerrainPopulated);
		ExtendedBlockStorage[] extendedBlockStorage4 = chunk1.getBlockStorageArray();
		NBTTagList nBTTagList5 = new NBTTagList("Sections");
		ExtendedBlockStorage[] extendedBlockStorage6 = extendedBlockStorage4;
		int i7 = extendedBlockStorage4.length;

		NBTTagCompound nBTTagCompound10;
		for(int i8 = 0; i8 < i7; ++i8) {
			ExtendedBlockStorage extendedBlockStorage9 = extendedBlockStorage6[i8];
			if(extendedBlockStorage9 != null && extendedBlockStorage9.func_48700_f() != 0) {
				nBTTagCompound10 = new NBTTagCompound();
				nBTTagCompound10.setByte("Y", (byte)(extendedBlockStorage9.getYLocation() >> 4 & 255));
				nBTTagCompound10.setByteArray("Blocks", extendedBlockStorage9.func_48692_g());
				if(extendedBlockStorage9.getBlockMSBArray() != null) {
					nBTTagCompound10.setByteArray("Add", extendedBlockStorage9.getBlockMSBArray().data);
				}

				nBTTagCompound10.setByteArray("Data", extendedBlockStorage9.func_48697_j().data);
				nBTTagCompound10.setByteArray("SkyLight", extendedBlockStorage9.getSkylightArray().data);
				nBTTagCompound10.setByteArray("BlockLight", extendedBlockStorage9.getBlocklightArray().data);
				nBTTagList5.appendTag(nBTTagCompound10);
			}
		}

		nBTTagCompound3.setTag("Sections", nBTTagList5);
		nBTTagCompound3.setByteArray("Biomes", chunk1.getBiomeArray());
		chunk1.hasEntities = false;

		{
			NBTTagList nBTTagList15 = new NBTTagList();
			Iterator<Entity> iterator17;
			for(i7 = 0; i7 < chunk1.entityLists.length; ++i7) {
				iterator17 = chunk1.entityLists[i7].iterator();
	
				while(iterator17.hasNext()) {
					Entity entity19 = (Entity)iterator17.next();
					chunk1.hasEntities = true;
					nBTTagCompound10 = new NBTTagCompound();
					if(entity19.addEntityID(nBTTagCompound10)) {
						nBTTagList15.appendTag(nBTTagCompound10);
					}
				}
			}
	
			nBTTagCompound3.setTag("Entities", nBTTagList15);
		}
		
		{
			NBTTagList nBTTagList16 = new NBTTagList();
			Iterator<TileEntity> iterator17 = chunk1.chunkTileEntityMap.values().iterator();
	
			while(iterator17.hasNext()) {
				TileEntity tileEntity20 = (TileEntity)iterator17.next();
				nBTTagCompound10 = new NBTTagCompound();
				tileEntity20.writeToNBT(nBTTagCompound10);
				nBTTagList16.appendTag(nBTTagCompound10);
			}
	
			nBTTagCompound3.setTag("TileEntities", nBTTagList16);
		}
		
		List<NextTickListEntry> list18 = world2.getPendingBlockUpdates(chunk1, false);
		if(list18 != null) {
			long j21 = world2.getWorldTime();
			NBTTagList nBTTagList11 = new NBTTagList();
			Iterator<NextTickListEntry> iterator12 = list18.iterator();

			while(iterator12.hasNext()) {
				NextTickListEntry nextTickListEntry13 = (NextTickListEntry)iterator12.next();
				NBTTagCompound nBTTagCompound14 = new NBTTagCompound();
				nBTTagCompound14.setInteger("i", nextTickListEntry13.blockID);
				nBTTagCompound14.setInteger("x", nextTickListEntry13.xCoord);
				nBTTagCompound14.setInteger("y", nextTickListEntry13.yCoord);
				nBTTagCompound14.setInteger("z", nextTickListEntry13.zCoord);
				nBTTagCompound14.setInteger("t", (int)(nextTickListEntry13.scheduledTime - j21));
				nBTTagList11.appendTag(nBTTagCompound14);
			}

			nBTTagCompound3.setTag("TileTicks", nBTTagList11);
		}

	}

	private Chunk func_48444_a(World world1, NBTTagCompound nBTTagCompound2) {
		int i3 = nBTTagCompound2.getInteger("xPos");
		int i4 = nBTTagCompound2.getInteger("zPos");
		Chunk chunk5 = new Chunk(world1, i3, i4);
		chunk5.heightMap = nBTTagCompound2.func_48182_l("HeightMap");
		chunk5.isTerrainPopulated = nBTTagCompound2.getBoolean("TerrainPopulated");
		NBTTagList nBTTagList6 = nBTTagCompound2.getTagList("Sections");
		byte b7 = 16;
		ExtendedBlockStorage[] extendedBlockStorage8 = new ExtendedBlockStorage[b7];

		for(int i9 = 0; i9 < nBTTagList6.tagCount(); ++i9) {
			NBTTagCompound nBTTagCompound10 = (NBTTagCompound)nBTTagList6.tagAt(i9);
			byte b11 = nBTTagCompound10.getByte("Y");
			ExtendedBlockStorage extendedBlockStorage12 = new ExtendedBlockStorage(b11 << 4);
			extendedBlockStorage12.setBlockLSBArray(nBTTagCompound10.getByteArray("Blocks"));
			if(nBTTagCompound10.hasKey("Add")) {
				extendedBlockStorage12.setBlockMSBArray(new NibbleArray(nBTTagCompound10.getByteArray("Add"), 4));
			}

			extendedBlockStorage12.setBlockMetadataArray(new NibbleArray(nBTTagCompound10.getByteArray("Data"), 4));
			extendedBlockStorage12.setSkylightArray(new NibbleArray(nBTTagCompound10.getByteArray("SkyLight"), 4));
			extendedBlockStorage12.setBlocklightArray(new NibbleArray(nBTTagCompound10.getByteArray("BlockLight"), 4));
			extendedBlockStorage12.func_48708_d();
			extendedBlockStorage8[b11] = extendedBlockStorage12;
		}

		chunk5.setStorageArrays(extendedBlockStorage8);
		if(nBTTagCompound2.hasKey("Biomes")) {
			chunk5.setBiomeArray(nBTTagCompound2.getByteArray("Biomes"));
		}

		NBTTagList nBTTagList14 = nBTTagCompound2.getTagList("Entities");
		if(nBTTagList14 != null) {
			for(int i15 = 0; i15 < nBTTagList14.tagCount(); ++i15) {
				NBTTagCompound nBTTagCompound17 = (NBTTagCompound)nBTTagList14.tagAt(i15);
				Entity entity19 = EntityList.createEntityFromNBT(nBTTagCompound17, world1);
				chunk5.hasEntities = true;
				if(entity19 != null) {
					chunk5.addEntity(entity19);
				}
			}
		}

		NBTTagList nBTTagList16 = nBTTagCompound2.getTagList("TileEntities");
		if(nBTTagList16 != null) {
			for(int i18 = 0; i18 < nBTTagList16.tagCount(); ++i18) {
				NBTTagCompound nBTTagCompound21 = (NBTTagCompound)nBTTagList16.tagAt(i18);
				TileEntity tileEntity13 = TileEntity.createAndLoadEntity(nBTTagCompound21);
				if(tileEntity13 != null) {
					chunk5.addTileEntity(tileEntity13);
				}
			}
		}

		if(nBTTagCompound2.hasKey("TileTicks")) {
			NBTTagList nBTTagList20 = nBTTagCompound2.getTagList("TileTicks");
			if(nBTTagList20 != null) {
				for(int i22 = 0; i22 < nBTTagList20.tagCount(); ++i22) {
					NBTTagCompound nBTTagCompound23 = (NBTTagCompound)nBTTagList20.tagAt(i22);
					world1.scheduleBlockUpdateFromLoad(nBTTagCompound23.getInteger("x"), nBTTagCompound23.getInteger("y"), nBTTagCompound23.getInteger("z"), nBTTagCompound23.getInteger("i"), nBTTagCompound23.getInteger("t"));
				}
			}
		}

		return chunk5;
	}
}
