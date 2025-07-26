package net.minecraft.src;

public class ChunkLoader {
	public static AnvilConverterData load(NBTTagCompound nBTTagCompound0) {
		int i1 = nBTTagCompound0.getInteger("xPos");
		int i2 = nBTTagCompound0.getInteger("zPos");
		AnvilConverterData anvilConverterData3 = new AnvilConverterData(i1, i2);
		anvilConverterData3.blocks = nBTTagCompound0.getByteArray("Blocks");
		anvilConverterData3.data = new NibbleArrayReader(nBTTagCompound0.getByteArray("Data"), 7);
		anvilConverterData3.skyLight = new NibbleArrayReader(nBTTagCompound0.getByteArray("SkyLight"), 7);
		anvilConverterData3.blockLight = new NibbleArrayReader(nBTTagCompound0.getByteArray("BlockLight"), 7);
		anvilConverterData3.heightmap = nBTTagCompound0.getByteArray("HeightMap");
		anvilConverterData3.terrainPopulated = nBTTagCompound0.getBoolean("TerrainPopulated");
		anvilConverterData3.entities = nBTTagCompound0.getTagList("Entities");
		anvilConverterData3.tileEntities = nBTTagCompound0.getTagList("TileEntities");
		anvilConverterData3.tileTicks = nBTTagCompound0.getTagList("TileTicks");

		try {
			anvilConverterData3.lastUpdated = nBTTagCompound0.getLong("LastUpdate");
		} catch (ClassCastException classCastException5) {
			anvilConverterData3.lastUpdated = (long)nBTTagCompound0.getInteger("LastUpdate");
		}

		return anvilConverterData3;
	}

	public static void convertToAnvilFormat(AnvilConverterData anvilConverterData0, NBTTagCompound nBTTagCompound1, WorldChunkManager worldChunkManager2) {
		nBTTagCompound1.setInteger("xPos", anvilConverterData0.x);
		nBTTagCompound1.setInteger("zPos", anvilConverterData0.z);
		nBTTagCompound1.setLong("LastUpdate", anvilConverterData0.lastUpdated);
		int[] i3 = new int[anvilConverterData0.heightmap.length];

		for(int i4 = 0; i4 < anvilConverterData0.heightmap.length; ++i4) {
			i3[i4] = anvilConverterData0.heightmap[i4];
		}

		nBTTagCompound1.func_48183_a("HeightMap", i3);
		nBTTagCompound1.setBoolean("TerrainPopulated", anvilConverterData0.terrainPopulated);
		NBTTagList nBTTagList16 = new NBTTagList("Sections");

		int i7;
		for(int i5 = 0; i5 < 8; ++i5) {
			boolean z6 = true;

			for(i7 = 0; i7 < 16 && z6; ++i7) {
				for(int i8 = 0; i8 < 16 && z6; ++i8) {
					for(int i9 = 0; i9 < 16; ++i9) {
						int i10 = i7 << 11 | i9 << 7 | i8 + (i5 << 4);
						byte b11 = anvilConverterData0.blocks[i10];
						if(b11 != 0) {
							z6 = false;
							break;
						}
					}
				}
			}

			if(!z6) {
				byte[] b19 = new byte[4096];
				NibbleArray nibbleArray20 = new NibbleArray(b19.length, 4);
				NibbleArray nibbleArray21 = new NibbleArray(b19.length, 4);
				NibbleArray nibbleArray22 = new NibbleArray(b19.length, 4);

				for(int i23 = 0; i23 < 16; ++i23) {
					for(int i12 = 0; i12 < 16; ++i12) {
						for(int i13 = 0; i13 < 16; ++i13) {
							int i14 = i23 << 11 | i13 << 7 | i12 + (i5 << 4);
							byte b15 = anvilConverterData0.blocks[i14];
							b19[i12 << 8 | i13 << 4 | i23] = (byte)(b15 & 255);
							nibbleArray20.set(i23, i12, i13, anvilConverterData0.data.get(i23, i12 + (i5 << 4), i13));
							nibbleArray21.set(i23, i12, i13, anvilConverterData0.skyLight.get(i23, i12 + (i5 << 4), i13));
							nibbleArray22.set(i23, i12, i13, anvilConverterData0.blockLight.get(i23, i12 + (i5 << 4), i13));
						}
					}
				}

				NBTTagCompound nBTTagCompound24 = new NBTTagCompound();
				nBTTagCompound24.setByte("Y", (byte)(i5 & 255));
				nBTTagCompound24.setByteArray("Blocks", b19);
				nBTTagCompound24.setByteArray("Data", nibbleArray20.data);
				nBTTagCompound24.setByteArray("SkyLight", nibbleArray21.data);
				nBTTagCompound24.setByteArray("BlockLight", nibbleArray22.data);
				nBTTagList16.appendTag(nBTTagCompound24);
			}
		}

		nBTTagCompound1.setTag("Sections", nBTTagList16);
		byte[] b17 = new byte[256];

		for(int i18 = 0; i18 < 16; ++i18) {
			for(i7 = 0; i7 < 16; ++i7) {
				b17[i7 << 4 | i18] = (byte)(worldChunkManager2.getBiomeGenAt(anvilConverterData0.x << 4 | i18, anvilConverterData0.z << 4 | i7).biomeID & 255);
			}
		}

		nBTTagCompound1.setByteArray("Biomes", b17);
		nBTTagCompound1.setTag("Entities", anvilConverterData0.entities);
		nBTTagCompound1.setTag("TileEntities", anvilConverterData0.tileEntities);
		if(anvilConverterData0.tileTicks != null) {
			nBTTagCompound1.setTag("TileTicks", anvilConverterData0.tileTicks);
		}

	}
}
