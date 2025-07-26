package net.minecraft.src;

import java.util.Random;

public abstract class WorldProvider {
	public World worldObj;
	public WorldType terrainType;
	public WorldChunkManager worldChunkMgr;
	public boolean isHellWorld = false;
	public boolean hasNoSky = false;
	public boolean isCaveWorld = false;
	public float[] lightBrightnessTable = new float[16];
	public int worldType = 0;
	private float[] colorsSunriseSunset = new float[4];

	public final void registerWorld(World world1) {
		this.worldObj = world1;
		this.terrainType = world1.getWorldInfo().getTerrainType();
		this.registerWorldChunkManager();
		this.generateLightBrightnessTable();
	}

	protected void generateLightBrightnessTable() {
		float f1 = 0.0F;

		for(int i2 = 0; i2 <= 15; ++i2) {
			float f3 = 1.0F - (float)i2 / 15.0F;
			this.lightBrightnessTable[i2] = (1.0F - f3) / (f3 * 3.0F + 1.0F) * (1.0F - f1) + f1;
		}

	}

	protected void registerWorldChunkManager() {
		this.worldChunkMgr = this.worldObj.getWorldInfo().getTerrainType().getChunkManager(this.worldObj);
	}

	public IChunkProvider getChunkProvider() {
		return this.worldObj.getWorldInfo().getTerrainType().getChunkGenerator(this.worldObj);
	}

	public boolean canCoordinateBeSpawn(int i1, int i2) {
		int i3 = this.worldObj.getFirstUncoveredBlock(i1, i2);
		return i3 == Block.grass.blockID;
	}

	public float calculateCelestialAngle(long j1, float f3) {
		int i4 = (int)(j1 % 24000L);
		float f5 = ((float)i4 + f3) / 24000.0F - 0.25F;
		if(f5 < 0.0F) {
			++f5;
		}

		if(f5 > 1.0F) {
			--f5;
		}

		float f6 = f5;
		f5 = 1.0F - (float)((Math.cos((double)f5 * Math.PI) + 1.0D) / 2.0D);
		f5 = f6 + (f5 - f6) / 3.0F;
		return f5;
	}

	public int getMoonPhase(long j1, float f3) {
		return (int)(j1 / 24000L) % 8;
	}

	public boolean canSleepHere() {
		return true;
	}

	public float[] calcSunriseSunsetColors(float f1, float f2) {
		float f3 = 0.4F;
		float f4 = MathHelper.cos(f1 * (float)Math.PI * 2.0F) - 0.0F;
		float f5 = -0.0F;
		if(f4 >= f5 - f3 && f4 <= f5 + f3) {
			float f6 = (f4 - f5) / f3 * 0.5F + 0.5F;
			float f7 = 1.0F - (1.0F - MathHelper.sin(f6 * (float)Math.PI)) * 0.99F;
			f7 *= f7;
			this.colorsSunriseSunset[0] = f6 * 0.3F + 0.7F;
			this.colorsSunriseSunset[1] = f6 * f6 * 0.7F + 0.2F;
			this.colorsSunriseSunset[2] = f6 * f6 * 0.0F + 0.2F;
			this.colorsSunriseSunset[3] = f7;
			return this.colorsSunriseSunset;
		} else {
			return null;
		}
	}

	public Vec3D getFogColor(Entity entityPlayer, float f1, float f2) {
		if (!GameRules.colouredFog) {
			float var3 = MathHelper.cos(f1 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
			if(var3 < 0.0F) {
				var3 = 0.0F;
			}

			if(var3 > 1.0F) {
				var3 = 1.0F;
			}

			float var4 = 0.7529412F;
			float var5 = 0.84705883F;
			float var6 = 1.0F;
			var4 *= var3 * 0.94F + 0.06F;
			var5 *= var3 * 0.94F + 0.06F;
			var6 *= var3 * 0.91F + 0.09F;
			return Vec3D.createVector((double)var4, (double)var5, (double)var6);
		} else {
			
			int posX = (int) entityPlayer.posX;
			int posZ = (int) entityPlayer.posZ;
	
			float f3 = MathHelper.cos(f1 * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
			if (f3 < 0.0F) {
				f3 = 0.0F;
			}
	
			if (f3 > 1.0F) {
				f3 = 1.0F;
			}
	
			int i6 = 0;
			int i7 = 0;
			int i8 = 0;
			float f4, f5, f6;
	
			// TODO:: Add option for colored fog
	
			// Trying to get it right, mark 2
			for (int i9 = -8; i9 < 8; i9++) {
				for (int i10 = -8; i10 < 8; i10++) {
					// 256 iterations of:
					int i11 = this.worldObj.getBiomeGenForCoords(posX + i10, posZ + i9).getBiomeFogColor();
					i6 += (i11 & 16711680) >> 16;
					i7 += (i11 & 65280) >> 8;
					i8 += i11 & 255;
				}
			}
	
			f4 = (i6 >> 8) / 256F;
			f5 = (i7 >> 8) / 256F;
			f6 = (i8 >> 8) / 256F;
	
			f4 *= f3 * 0.94F + 0.06F;
			f5 *= f3 * 0.94F + 0.06F;
			f6 *= f3 * 0.91F + 0.09F;
			return Vec3D.createVector((double) f4, (double) f5, (double) f6);
		}
	}
	
	public Vec3D getSkyColor(Entity entity1, float f2) { 

		float f3 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f2);
		float f4 = MathHelper.cos(f3 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(f4 < 0.0F) {
			f4 = 0.0F;
		}

		if(f4 > 1.0F) {
			f4 = 1.0F;
		}

		int i5 = MathHelper.floor_double(entity1.posX);
		int i6 = MathHelper.floor_double(entity1.posZ);
		BiomeGenBase biomeGenBase7 = this.worldObj.getBiomeGenForCoords(i5, i6);
		float f8 = biomeGenBase7.getFloatTemperature();
		int i9 = biomeGenBase7.getSkyColorByTemp(f8);
		float f10 = (float)(i9 >> 16 & 255) / 255.0F;
		float f11 = (float)(i9 >> 8 & 255) / 255.0F;
		float f12 = (float)(i9 & 255) / 255.0F;
		f10 *= f4;
		f11 *= f4;
		f12 *= f4;
				
		float f13 = this.worldObj.getRainStrength(f2);
		float f14;
		float f15;
		if(f13 > 0.0F) {
			f14 = (f10 * 0.3F + f11 * 0.59F + f12 * 0.11F) * 0.6F;
			f15 = 1.0F - f13 * 0.75F;
			f10 = f10 * f15 + f14 * (1.0F - f15);
			f11 = f11 * f15 + f14 * (1.0F - f15);
			f12 = f12 * f15 + f14 * (1.0F - f15);
		}

		f14 = this.worldObj.getWeightedThunderStrength(f2);
		if(f14 > 0.0F) {
			f15 = (f10 * 0.3F + f11 * 0.59F + f12 * 0.11F) * 0.2F;
			float f16 = 1.0F - f14 * 0.75F;
			f10 = f10 * f16 + f15 * (1.0F - f16);
			f11 = f11 * f16 + f15 * (1.0F - f16);
			f12 = f12 * f16 + f15 * (1.0F - f16);
		}

		if(this.worldObj.lightningFlash > 0) {
			f15 = (float)this.worldObj.lightningFlash - f2;
			if(f15 > 1.0F) {
				f15 = 1.0F;
			}

			f15 *= 0.45F;
			f10 = f10 * (1.0F - f15) + 0.8F * f15;
			f11 = f11 * (1.0F - f15) + 0.8F * f15;
			f12 = f12 * (1.0F - f15) + 1.0F * f15;
		}

		return Vec3D.createVector((double)f10, (double)f11, (double)f12);
	}
	
	public Vec3D getSkyColorBottom(Entity entity1, float f2) {
		float f3 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f2);
		float f4 = MathHelper.cos(f3 * (float)Math.PI * 2.0F) * 2.0F + 0.5F;
		if(f4 < 0.0F) {
			f4 = 0.0F;
		}

		if(f4 > 1.0F) {
			f4 = 1.0F;
		}
		
		Vec3D vec3D = ((BlockFluid)Block.waterStill).colorMultiplierAsVec3D(this.worldObj, (int) entity1.posX, (int) entity1.posY, (int) entity1.posZ);
		float f10 = (float)vec3D.xCoord;
		float f11 = (float)vec3D.yCoord;
		float f12 = (float)vec3D.zCoord;
		
		f10 *= f4;
		f11 *= f4;
		f12 *= f4;
		float f13 = this.worldObj.getRainStrength(f2);
		float f14;
		float f15;
		if(f13 > 0.0F) {
			f14 = (f10 * 0.3F + f11 * 0.59F + f12 * 0.11F) * 0.6F;
			f15 = 1.0F - f13 * 0.75F;
			f10 = f10 * f15 + f14 * (1.0F - f15);
			f11 = f11 * f15 + f14 * (1.0F - f15);
			f12 = f12 * f15 + f14 * (1.0F - f15);
		}

		f14 = this.worldObj.getWeightedThunderStrength(f2);
		if(f14 > 0.0F) {
			f15 = (f10 * 0.3F + f11 * 0.59F + f12 * 0.11F) * 0.2F;
			float f16 = 1.0F - f14 * 0.75F;
			f10 = f10 * f16 + f15 * (1.0F - f16);
			f11 = f11 * f16 + f15 * (1.0F - f16);
			f12 = f12 * f16 + f15 * (1.0F - f16);
		}

		if(this.worldObj.lightningFlash > 0) {
			f15 = (float)this.worldObj.lightningFlash - f2;
			if(f15 > 1.0F) {
				f15 = 1.0F;
			}

			f15 *= 0.45F;
			f10 = f10 * (1.0F - f15) + 0.8F * f15;
			f11 = f11 * (1.0F - f15) + 0.8F * f15;
			f12 = f12 * (1.0F - f15) + 1.0F * f15;
		}

		return Vec3D.createVector((double)f10, (double)f11, (double)f12);
	}	
	
	public float getStarBrightness(float f1) {
		float f2 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f1);
		float f3 = 1.0F - (MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.75F);
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		return f3 * f3 * 0.5F;
	}

	public boolean canRespawnHere() {
		return true;
	}

	public static WorldProvider getProviderForTerrainType(WorldType terrainType) {
		if(terrainType == WorldType.ALPHA || terrainType == WorldType.ALPHA_SNOW) return new WorldProviderSurfaceClassic();			
		return new WorldProviderSurface();
	}
	
	public static WorldProvider getProviderForDimension(int i0) {
			switch(i0) {
		case 0: return GameRules.getWorldProviderForSurface();
		default: return null;
		}
	}

	public float getCloudHeight() {
		return this.terrainType != WorldType.AMPLIFIED ? 128.0F : 250F;
	}

	public boolean isSkyColored() {
		return true;
	}

	public ChunkCoordinates getEntrancePortalLocation() {
		return null;
	}

	public int getAverageGroundLevel() {
		return this.terrainType == WorldType.FLAT ? 
				4 
			: 
				64;
	}

	public boolean getWorldHasNoSky() {
		return this.terrainType != WorldType.FLAT && !this.hasNoSky;
	}

	public double getVoidFogYFactor() {
		return this.terrainType == WorldType.FLAT ? 1.0D : 8.0D / 256D;
	}

	public boolean func_48218_b(int i1, int i2) {
		return false;
	}
	
	public int calculateSkylightSubtracted(float f1) {
		//float f2 = this.getCelestialAngle(f1);
		float f2 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f1);
		float f3 = 1.0F - (MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.5F);
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		f3 = 1.0F - f3;
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getRainStrength(f1) * 5.0F) / 16.0D));
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getWeightedThunderStrength(f1) * 5.0F) / 16.0D));
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getSandstormingStrength(f1) * 7.0F) / 16.0D));
		f3 = 1.0F - f3;
		return (int)(f3 * 11.0F);
	}
	
	public float getSunBrightness(float f1) {
		//float f2 = this.getCelestialAngle(f1);
		float f2 = this.calculateCelestialAngle(this.worldObj.worldInfo.getWorldTime(), f1);
		float f3 = 1.0F - (MathHelper.cos(f2 * (float)Math.PI * 2.0F) * 2.0F + 0.2F);
		if(f3 < 0.0F) {
			f3 = 0.0F;
		}

		if(f3 > 1.0F) {
			f3 = 1.0F;
		}

		f3 = 1.0F - f3;
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getRainStrength(f1) * 5.0F) / 16.0D));
		f3 = (float)((double)f3 * (1.0D - (double)(this.worldObj.getWeightedThunderStrength(f1) * 5.0F) / 16.0D));
		return f3 * 0.8F + 0.2F;
	}
	
	public String getSaveFolder() {
		return null;
	}
	
	public String getWelcomeMessage() {
		return null;
	}

	public String getDepartMessage() {
		return null;
	}
	
	public int getSeaLevel() {
		return 64;
	}

	public boolean noCelestials() {
		return false;
	}

	public boolean canCreatePortalToTheNether() {
		return this.terrainType.canCreatePortalToTheNether;
	}

	public int getMaxNumberOfCreature(EnumCreatureType enumCreatureType) {
		return enumCreatureType.getMaxNumberOfCreature();
	}

	public int getRainTimeToStop(Random rand) {
		return rand.nextInt(12000) + 12000;
	}

	public int getRainTimeToStart(Random rand) {
		return rand.nextInt(168000) + 12000;
	}
	
	public int[] updateLightmap(float torchFlicker, float gamma, EntityPlayer thePlayer) {
		int[] lightmapColors = new int[256];
		World world1 = this.worldObj;
		
		if(world1 != null) {
			float sb = world1.getSunBrightness(1.0F);
			
			for(int idx = 0; idx < 256; ++idx) {
				
				float sunBrightness = sb * 0.95F + 0.05F;
				float lightCoarse = this.lightBrightnessTable[idx / 16] * sunBrightness;
				float lightFine = this.lightBrightnessTable[idx % 16] * (torchFlicker * 0.1F + 1.5F);
				if(world1.lightningFlash > 0) {
					lightCoarse = this.lightBrightnessTable[idx / 16];
				}

				float lightCoarseNorm = lightCoarse * (sb * 0.65F + 0.35F);
				float lightCoarseNormG = lightFine * ((lightFine * 0.6F + 0.4F) * 0.6F + 0.4F);
				float lightCoarseNormB = lightFine * (lightFine * lightFine * 0.6F + 0.4F);
				
				// This is what makes everything yellowish!
				float r = lightCoarseNorm + lightFine;
				float g = lightCoarseNorm + lightCoarseNormG;
				float b = lightCoarse + lightCoarseNormB;
				
				r = r * 0.96F + 0.03F;
				g = g * 0.96F + 0.03F;
				b = b * 0.96F + 0.03F;

				if(this.worldType == 1) {
					r = 0.22F + lightFine * 0.75F;
					g = 0.28F + lightCoarseNormG * 0.75F;
					b = 0.25F + lightCoarseNormB * 0.75F;
				}

				// Set up night vision with code somewhat stolen from r1.5.2!
				/*
				if(thePlayer.divingHelmetOn() && thePlayer.isInsideOfMaterial(Material.water)) {
					float nVB = 0.5F;
					
					float fNV = 1.0F / r;
					if(fNV > 1.0F / g) fNV = 1.0F / g;
					if(fNV > 1.0F / b) fNV = 1.0F / b;
					
					r = r * (1.0F - nVB) + r * fNV * nVB;
					g = g * (1.0F - nVB) + g * fNV * nVB;
					b = b * (1.0F - nVB) + b * fNV * nVB;
				}
				*/

				if(r > 1.0F) {
					r = 1.0F;
				}

				if(g > 1.0F) {
					g = 1.0F;
				}

				if(b > 1.0F) {
					b = 1.0F;
				}

				float f16 = 1.0F - r;
				float f17 = 1.0F - g;
				float f18 = 1.0F - b;
				f16 = 1.0F - f16 * f16 * f16 * f16;
				f17 = 1.0F - f17 * f17 * f17 * f17;
				f18 = 1.0F - f18 * f18 * f18 * f18;
				r = r * (1.0F - gamma) + f16 * gamma;
				g = g * (1.0F - gamma) + f17 * gamma;
				b = b * (1.0F - gamma) + f18 * gamma;
				r = r * 0.96F + 0.03F;
				g = g * 0.96F + 0.03F;
				b = b * 0.96F + 0.03F;
				if(r > 1.0F) {
					r = 1.0F;
				}

				if(g > 1.0F) {
					g = 1.0F;
				}

				if(b > 1.0F) {
					b = 1.0F;
				}

				if(r < 0.0F) {
					r = 0.0F;
				}

				if(g < 0.0F) {
					g = 0.0F;
				}

				if(b < 0.0F) {
					b = 0.0F;
				}

				short alpha = 255;
				int rI = (int)(r * 255.0F);
				int gI = (int)(g * 255.0F);
				int bI = (int)(b * 255.0F);
				lightmapColors[idx] = alpha << 24 | rI << 16 | gI << 8 | bI;
			}
		}
		
		return lightmapColors;
	}
}
