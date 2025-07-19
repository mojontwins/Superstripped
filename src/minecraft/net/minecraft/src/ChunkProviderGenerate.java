package net.minecraft.src;

import java.util.List;
import java.util.Random;

import com.mojontwins.minecraft.feature.FeatureProvider;

public class ChunkProviderGenerate implements IChunkProvider {
	protected Random rand;
	protected NoiseGeneratorOctaves noiseGen1;
	protected NoiseGeneratorOctaves noiseGen2;
	protected NoiseGeneratorOctaves noiseGen3;
	protected NoiseGeneratorOctaves noiseGenStone;
	protected NoiseGeneratorOctaves noiseGen5;
	protected NoiseGeneratorOctaves noiseGen6;
	
	protected World worldObj;
	protected final boolean mapFeaturesEnabled;
	
	protected double[] noiseArray;
	protected double[] stoneNoise = new double[256];
	
	protected MapGenBase caveGenerator = new MapGenCaves();
	protected MapGenBase ravineGenerator = new MapGenRavine();
	
	// Multi-chunk features	
	public FeatureProvider featureProvider;
	
	protected BiomeGenBase[] biomesForGeneration;
	
	double[] noise3;
	double[] noise1;
	double[] noise2;
	double[] noise5;
	double[] noise6;
	float[] distanceArray;
	int[][] unusedArray = new int[32][32];
	protected boolean isOcean;

	public ChunkProviderGenerate(World world1, long j2, boolean z4) {
		this.worldObj = world1;
		this.mapFeaturesEnabled = z4;
		this.rand = new Random(j2);
		this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseGenStone = new NoiseGeneratorOctaves(this.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
		
		this.featureProvider = new FeatureProvider(worldObj, this);
	}

	public void generateTerrain(int chunkX, int chunkZ, byte[] blocks) {
		int quadrantSize = 4;
		int seaLevel = 64;
		int cellSize = quadrantSize + 1;
		int columnSize = 17;
		int biomeQuadrantSize = quadrantSize + 1;
		
		double scalingFactor = 0.25D;
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, biomeQuadrantSize + 5, biomeQuadrantSize + 5);
		this.noiseArray = this.initializeNoiseField(this.noiseArray, chunkX * quadrantSize, 0, chunkZ * quadrantSize, cellSize, columnSize, cellSize);
		
		this.isOcean = true;

		// Split in 4x16x4 sections
		for(int xSection = 0; xSection < quadrantSize; ++xSection) {
			for(int zSection = 0; zSection < quadrantSize; ++zSection) {
				
				for(int ySection = 0; ySection < 16; ++ySection) {

					double noiseA = this.noiseArray[((xSection + 0) * cellSize + zSection + 0) * columnSize + ySection + 0];
					double noiseB = this.noiseArray[((xSection + 0) * cellSize + zSection + 1) * columnSize + ySection + 0];
					double noiseC = this.noiseArray[((xSection + 1) * cellSize + zSection + 0) * columnSize + ySection + 0];
					double noiseD = this.noiseArray[((xSection + 1) * cellSize + zSection + 1) * columnSize + ySection + 0];
					double noiseAinc = (this.noiseArray[((xSection + 0) * cellSize + zSection + 0) * columnSize + ySection + 1] - noiseA) * 0.125D;
					double noiseBinc = (this.noiseArray[((xSection + 0) * cellSize + zSection + 1) * columnSize + ySection + 1] - noiseB) * 0.125D;
					double noiseCinc = (this.noiseArray[((xSection + 1) * cellSize + zSection + 0) * columnSize + ySection + 1] - noiseC) * 0.125D;
					double noiseDinc = (this.noiseArray[((xSection + 1) * cellSize + zSection + 1) * columnSize + ySection + 1] - noiseD) * 0.125D;

					for(int y = 0; y < 8; ++y) {						
						double curNoiseA = noiseA;
						double curNoiseB = noiseB;
						double curNoiseAinc = (noiseC - noiseA) * scalingFactor;
						double curNoiseBinc = (noiseD - noiseB) * scalingFactor;

						for(int x = 0; x < 4; ++x) {
							int indexInBlockArray = (x + (xSection << 2)) << 11 | (0 + (zSection << 2)) << 7 | (ySection << 3) + y;
							
							double density = curNoiseA;
							double densityIncrement = (curNoiseB - curNoiseA) * 0.25D;

							int yy = ySection * 8 + y;
							for(int z = 0; z < 4; ++z) {
								
								int blockID = 0;

								// Fill with water with a layer of ice if suitable								
								if(yy < seaLevel) {
									blockID = Block.waterStill.blockID;
								}

								// World density positive: fill with block.
								if(density > 0.0D) {
									blockID = Block.stone.blockID;
								}

								blocks[indexInBlockArray] = (byte)blockID;

								// Next Z
								indexInBlockArray += 128;
								density += densityIncrement;
								
								// Ocean detector
								if(yy == seaLevel - 1) this.isOcean &= (blockID != Block.stone.blockID);
							}

							curNoiseA += curNoiseAinc;
							curNoiseB += curNoiseBinc;
						}

						noiseA += noiseAinc;
						noiseB += noiseBinc;
						noiseC += noiseCinc;
						noiseD += noiseDinc;
					}
				}
			}
		}
	}

	public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		int seaLevel = 64;
		double d6 = 8.0D / 256D;
		this.stoneNoise = this.noiseGenStone.generateNoiseOctaves(this.stoneNoise, chunkX * 16, chunkZ * 16, 0, 16, 16, 1, d6 * 2.0D, d6 * 2.0D, d6 * 2.0D);

		BiomeGenBase biomeGen;
		
		for(int z = 0; z < 16; ++z) {
			for(int x = 0; x < 16; ++x) {
				biomeGen = biomes[x + z * 16];
				
				biomeGen.replaceBlocksForBiome(this, this.worldObj, this.rand, 
						chunkX, chunkZ, x, z, 
						blocks, metadata, seaLevel, 
						0D, 0D, this.stoneNoise[z + x * 16]
				);
			}
		}
	}
				
	public Chunk loadChunk(int i1, int i2) {
		return this.provideChunk(i1, i2);
	}
	
	protected byte[] createByteArray() {
		return new byte[32768];
	}

	public Chunk provideChunk(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);

		byte[] blockArray = this.createByteArray();
		byte[] metadataArray = this.createByteArray();
		this.generateTerrain(chunkX, chunkZ, blockArray);

		this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		
		this.replaceBlocksForBiome(chunkX, chunkZ, blockArray, metadataArray, this.biomesForGeneration);
		
		this.caveGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockArray);
		this.ravineGenerator.generate(this, this.worldObj, chunkX, chunkZ, blockArray);
		
		Chunk chunk = new Chunk(this.worldObj, blockArray, chunkX, chunkZ);

		if (this.mapFeaturesEnabled) {
			this.featureProvider.getNearestFeatures(chunkX, chunkZ, chunk);
		} 

		byte[] biomeArray = chunk.getBiomeArray();

		for(int i6 = 0; i6 < biomeArray.length; ++i6) {
			biomeArray[i6] = (byte)this.biomesForGeneration[i6].biomeID;
		}

		chunk.generateSkylightMap();
		return chunk;
	}

	public Chunk justGenerate(int chunkX, int chunkZ) {
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		byte[] blockArray = this.createByteArray();
		this.generateTerrain(chunkX, chunkZ, blockArray);
		
		Chunk chunk = new Chunk(this.worldObj, blockArray, chunkX, chunkZ);
		
		return chunk;
	}

	public double[] initializeNoiseField(double[] d1, int i2, int i3, int i4, int i5, int i6, int i7) {
		if(d1 == null) {
			d1 = new double[i5 * i6 * i7];
		}

		if(this.distanceArray == null) {
			this.distanceArray = new float[25];

			for(int i8 = -2; i8 <= 2; ++i8) {
				for(int i9 = -2; i9 <= 2; ++i9) {
					float f10 = 10.0F / MathHelper.sqrt_float((float)(i8 * i8 + i9 * i9) + 0.2F);
					this.distanceArray[i8 + 2 + (i9 + 2) * 5] = f10;
				}
			}
		}

		double d44 = 684.412D;
		double d45 = 684.412D;
		this.noise5 = this.noiseGen5.generateNoiseOctaves(this.noise5, i2, i4, i5, i7, 1.121D, 1.121D, 0.5D);
		this.noise6 = this.noiseGen6.generateNoiseOctaves(this.noise6, i2, i4, i5, i7, 200.0D, 200.0D, 0.5D);
		this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, i2, i3, i4, i5, i6, i7, d44 / 80.0D, d45 / 160.0D, d44 / 80.0D);
		this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, i2, i3, i4, i5, i6, i7, d44, d45, d44);
		this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, i2, i3, i4, i5, i6, i7, d44, d45, d44);
		int i12 = 0;
		int i13 = 0;

		for(int i14 = 0; i14 < i5; ++i14) {
			for(int i15 = 0; i15 < i7; ++i15) {
				float maxHeightScaled = 0.0F;
				float minHeightScaled = 0.0F;
				float totalDistance = 0.0F;
				byte b19 = 2;
				BiomeGenBase biomeGenBase20 = this.biomesForGeneration[i14 + 2 + (i15 + 2) * (i5 + 5)];

				for(int i21 = -b19; i21 <= b19; ++i21) {
					for(int i22 = -b19; i22 <= b19; ++i22) {
						BiomeGenBase biomeGenBase23 = this.biomesForGeneration[i14 + i21 + 2 + (i15 + i22 + 2) * (i5 + 5)];
						float distance = this.distanceArray[i21 + 2 + (i22 + 2) * 5] / (biomeGenBase23.minHeight + 2.0F);
						if(biomeGenBase23.minHeight > biomeGenBase20.minHeight) {
							distance /= 2.0F;
						}

						maxHeightScaled += biomeGenBase23.maxHeight * distance;
						minHeightScaled += biomeGenBase23.minHeight * distance;
						totalDistance += distance;
					}
				}

				float avgMaxHeight = maxHeightScaled / totalDistance;
				float avgMinHeight = minHeightScaled / totalDistance;
				
				avgMaxHeight = avgMaxHeight * 0.9F + 0.1F;
				avgMinHeight = (avgMinHeight * 4.0F - 1.0F) / 8.0F;
				
				double d46 = this.noise6[i13] / 8000.0D;
				if(d46 < 0.0D) {
					d46 = -d46 * 0.3D;
				}

				d46 = d46 * 3.0D - 2.0D;
				if(d46 < 0.0D) {
					d46 /= 2.0D;
					if(d46 < -1.0D) {
						d46 = -1.0D;
					}

					d46 /= 1.4D;
					d46 /= 2.0D;
				} else {
					if(d46 > 1.0D) {
						d46 = 1.0D;
					}

					d46 /= 8.0D;
				}

				++i13;

				for(int i47 = 0; i47 < i6; ++i47) {
					double d48 = (double)avgMinHeight;
					double d26 = (double)avgMaxHeight;
					d48 += d46 * 0.2D;
					d48 = d48 * (double)i6 / 16.0D;
					double d28 = (double)i6 / 2.0D + d48 * 4.0D;
					double d30 = 0.0D;
					double d32 = ((double)i47 - d28) * 12.0D * 128.0D / 128.0D / d26;
					if(d32 < 0.0D) {
						d32 *= 4.0D;
					}

					double d34 = this.noise1[i12] / 512.0D;
					double d36 = this.noise2[i12] / 512.0D;
					double d38 = (this.noise3[i12] / 10.0D + 1.0D) / 2.0D;
					if(d38 < 0.0D) {
						d30 = d34;
					} else if(d38 > 1.0D) {
						d30 = d36;
					} else {
						d30 = d34 + (d36 - d34) * d38;
					}

					d30 -= d32;
					if(i47 > i6 - 4) {
						double d40 = (double)((float)(i47 - (i6 - 4)) / 3.0F);
						d30 = d30 * (1.0D - d40) + -10.0D * d40;
					}

					d1[i12] = d30;
					++i12;
				}
			}
		}

		return d1;
	}

	public boolean chunkExists(int i1, int i2) {
		return true;
	}

	public void populate(IChunkProvider iChunkProvider1, int chunkX, int chunkZ) {
		BlockSand.fallInstantly = true;
		int x0 = chunkX * 16;
		int z0 = chunkZ * 16;
		
		BiomeGenBase biomeGenBase6 = this.worldObj.getBiomeGenForCoords(x0 + 16, z0 + 16);
		this.rand.setSeed(this.worldObj.getSeed());
		long j7 = this.rand.nextLong() / 2L * 2L + 1L;
		long j9 = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed((long)chunkX * j7 + (long)chunkZ * j9 ^ this.worldObj.getSeed());
		boolean z11 = false;
		boolean hadCustomFeat = false;		
		
		if(this.mapFeaturesEnabled) {	
			hadCustomFeat = this.featureProvider.populateFeatures(worldObj, rand, chunkX, chunkZ);		
		}
		
		int i12;
		int i13;
		int i14;
		if(!z11 && this.rand.nextInt(4) == 0) {
			i12 = x0 + this.rand.nextInt(16) + 8;
			i13 = this.rand.nextInt(128);
			i14 = z0 + this.rand.nextInt(16) + 8;
			(new WorldGenLakes(Block.waterStill.blockID)).generate(this.worldObj, this.rand, i12, i13, i14);
		}

		if(!z11 && this.rand.nextInt(8) == 0) {
			i12 = x0 + this.rand.nextInt(16) + 8;
			i13 = this.rand.nextInt(this.rand.nextInt(120) + 8);
			i14 = z0 + this.rand.nextInt(16) + 8;
			if(i13 < 63 || this.rand.nextInt(10) == 0) {
				(new WorldGenLakes(Block.lavaStill.blockID)).generate(this.worldObj, this.rand, i12, i13, i14);
			}
		}

		for(i12 = 0; i12 < 8; ++i12) {
			i13 = x0 + this.rand.nextInt(16) + 8;
			i14 = this.rand.nextInt(128);
			int i15 = z0 + this.rand.nextInt(16) + 8;
			if((new WorldGenDungeons()).generate(this.worldObj, this.rand, i13, i14, i15)) {
				;
			}
		}

		biomeGenBase6.decorate(this.worldObj, this.rand, x0, z0, hadCustomFeat);
		
		SpawnerAnimals.performWorldGenSpawning(this.worldObj, biomeGenBase6, x0 + 8, z0 + 8, 16, 16, this.rand);
		x0 += 8;
		z0 += 8;

		for(i12 = 0; i12 < 16; ++i12) {
			for(i13 = 0; i13 < 16; ++i13) {
				i14 = this.worldObj.getPrecipitationHeight(x0 + i12, z0 + i13);
				if(this.worldObj.isBlockHydratedDirectly(i12 + x0, i14 - 1, i13 + z0)) {
					this.worldObj.setBlockWithNotify(i12 + x0, i14 - 1, i13 + z0, Block.ice.blockID);
				}

				if(this.worldObj.canSnowAt(i12 + x0, i14, i13 + z0)) {
					this.worldObj.setBlockWithNotify(i12 + x0, i14, i13 + z0, Block.snow.blockID);
				}
			}
		}

		BlockSand.fallInstantly = false;
	}

	public boolean saveChunks(boolean z1, IProgressUpdate iProgressUpdate2) {
		return true;
	}

	public boolean unload100OldestChunks() {
		return false;
	}

	public boolean canSave() {
		return true;
	}

	public String makeString() {
		return "RandomLevelSource";
	}

	public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType1, int i2, int i3, int i4) {
		BiomeGenBase biomeGenBase5 = this.worldObj.getBiomeGenForCoords(i2, i4);
		return biomeGenBase5 == null ? null : biomeGenBase5.getSpawnableList(enumCreatureType1);
	}

	public ChunkPosition findClosestStructure(World world1, String string2, int i3, int i4, int i5) {
		return null;
	}
}
