package net.minecraft.src;

import java.util.Random;

public class ChunkProviderAlpha extends ChunkProviderGenerate {

	public NoiseGeneratorOctavesAlpha minLimitNoiseAlpha;
	public NoiseGeneratorOctavesAlpha maxLimitNoiseAlpha;
	public NoiseGeneratorOctavesAlpha mainNoiseAlpha;
	public NoiseGeneratorOctavesAlpha noiseGenSandOrGravel;
	public NoiseGeneratorOctavesAlpha noiseStone;
	public NoiseGeneratorOctavesAlpha scaleNoiseAlpha;
	public NoiseGeneratorOctavesAlpha depthNoiseAlpha;
	double[] depthArray;
	
	private double[] sandNoise = new double[256];
	private double[] gravelNoise = new double[256];
	
	public ChunkProviderAlpha(World world, long seed, boolean mapFeaturesEnabled) {
		super(world, seed, mapFeaturesEnabled);

		// Reset the randomizer so worlds are seed-accurate with vanilla
		this.rand = new Random(seed);
		
		this.minLimitNoiseAlpha = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.maxLimitNoiseAlpha = new NoiseGeneratorOctavesAlpha(this.rand, 16);
		this.mainNoiseAlpha = new NoiseGeneratorOctavesAlpha(this.rand, 8);
		this.noiseGenSandOrGravel = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.noiseStone = new NoiseGeneratorOctavesAlpha(this.rand, 4);
		this.scaleNoiseAlpha = new NoiseGeneratorOctavesAlpha(this.rand, 10);
		this.depthNoiseAlpha = new NoiseGeneratorOctavesAlpha(this.rand, 16);
	}
	
	public void replaceBlocksForBiome(int chunkX, int chunkZ, byte[] blocks, byte[] metadata, BiomeGenBase[] biomes) {
		int seaLevel = 64;
		double d5 = 8.0D / 256D;
		this.sandNoise = this.noiseGenSandOrGravel.generateNoiseOctaves(this.sandNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 0.0D, 16, 16, 1, d5, d5, 1.0D);
		this.gravelNoise = this.noiseGenSandOrGravel.generateNoiseOctaves(this.gravelNoise, (double)(chunkZ * 16), 109.0134D, (double)(chunkX * 16), 16, 1, 16, d5, 1.0D, d5);
		this.stoneNoise = this.noiseStone.generateNoiseOctaves(this.stoneNoise, (double)(chunkX * 16), (double)(chunkZ * 16), 0.0D, 16, 16, 1, d5 * 2.0D, d5 * 2.0D, d5 * 2.0D);

		BiomeGenBase biomeGen;

		for(int z = 0; z < 16; ++z) {
			for(int x = 0; x < 16; ++x) {		
				biomeGen = biomes[z | (x << 4)];

				int noiseIndex = z | (x << 4);
				biomeGen.replaceBlocksForBiome(this, this.worldObj, this.rand, 
						chunkX, chunkZ, x, z, 
						blocks, metadata, seaLevel, 
						this.sandNoise[noiseIndex], this.gravelNoise[noiseIndex], this.stoneNoise[noiseIndex]
				);
			}
		}

	}
	
	public double[] initializeNoiseField(double[] d1, int i2, int i3, int i4, int i5, int i6, int i7) {
		if(d1 == null) {
			d1 = new double[i5 * i6 * i7];
		}

		double d8 = 684.412D;
		double d10 = 684.412D;
		this.scaleArray = this.scaleNoiseAlpha.generateNoiseOctaves(this.scaleArray, (double)i2, (double)i3, (double)i4, i5, 1, i7, 1.0D, 0.0D, 1.0D);
		this.depthArray = this.depthNoiseAlpha.generateNoiseOctaves(this.depthArray, (double)i2, (double)i3, (double)i4, i5, 1, i7, 100.0D, 0.0D, 100.0D);
		this.mainArray = this.mainNoiseAlpha.generateNoiseOctaves(this.mainArray, (double)i2, (double)i3, (double)i4, i5, i6, i7, d8 / 80.0D, d10 / 160.0D, d8 / 80.0D);
		this.minLimitArray = this.minLimitNoiseAlpha.generateNoiseOctaves(this.minLimitArray, (double)i2, (double)i3, (double)i4, i5, i6, i7, d8, d10, d8);
		this.maxLimitArray = this.maxLimitNoiseAlpha.generateNoiseOctaves(this.maxLimitArray, (double)i2, (double)i3, (double)i4, i5, i6, i7, d8, d10, d8);
		int i12 = 0;
		int i13 = 0;

		for(int i14 = 0; i14 < i5; ++i14) {
			for(int i15 = 0; i15 < i7; ++i15) {
				double d16 = (this.scaleArray[i13] + 256.0D) / 512.0D;
				if(d16 > 1.0D) {
					d16 = 1.0D;
				}

				double d18 = 0.0D;
				double d20 = this.depthArray[i13] / 8000.0D;
				if(d20 < 0.0D) {
					d20 = -d20;
				}

				d20 = d20 * 3.0D - 3.0D;
				if(d20 < 0.0D) {
					d20 /= 2.0D;
					if(d20 < -1.0D) {
						d20 = -1.0D;
					}

					d20 /= 1.4D;
					d20 /= 2.0D;
					d16 = 0.0D;
				} else {
					if(d20 > 1.0D) {
						d20 = 1.0D;
					}

					d20 /= 6.0D;
				}

				d16 += 0.5D;
				d20 = d20 * (double)i6 / 16.0D;
				double d22 = (double)i6 / 2.0D + d20 * 4.0D;
				++i13;

				for(int i24 = 0; i24 < i6; ++i24) {
					double d25 = 0.0D;
					double d27 = ((double)i24 - d22) * 12.0D / d16;
					if(d27 < 0.0D) {
						d27 *= 4.0D;
					}

					double d29 = this.minLimitArray[i12] / 512.0D;
					double d31 = this.maxLimitArray[i12] / 512.0D;
					double d33 = (this.mainArray[i12] / 10.0D + 1.0D) / 2.0D;
					if(d33 < 0.0D) {
						d25 = d29;
					} else if(d33 > 1.0D) {
						d25 = d31;
					} else {
						d25 = d29 + (d31 - d29) * d33;
					}

					d25 -= d27;
					double d35;
					if(i24 > i6 - 4) {
						d35 = (double)((float)(i24 - (i6 - 4)) / 3.0F);
						d25 = d25 * (1.0D - d35) + -10.0D * d35;
					}

					if((double)i24 < d18) {
						d35 = (d18 - (double)i24) / 4.0D;
						if(d35 < 0.0D) {
							d35 = 0.0D;
						}

						if(d35 > 1.0D) {
							d35 = 1.0D;
						}

						d25 = d25 * (1.0D - d35) + -10.0D * d35;
					}

					d1[i12] = d25;
					++i12;
				}
			}
		}

		return d1;
	}
}
