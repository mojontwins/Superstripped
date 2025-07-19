package net.minecraft.src;

public class GenLayerBiomesAlpha extends GenLayerAlpha {
	private BiomeGenBase biome;
	
	public GenLayerBiomesAlpha(long j1, WorldType worldType) {
		super(j1);
		biome = worldType.getBiomesForWorldType()[0];
	}

	@Override
	public int[] getInts(int x, int y, int w, int h) {
		int[] dst = IntCache.getIntCache(w * h);
		
		for(int iY = 0; iY < h; ++iY) {
			for(int iX = 0; iX < w; ++iX) {
				dst[iX + iY * w] = biome.biomeID;
			}
		}
		
		return dst;
	}

}
