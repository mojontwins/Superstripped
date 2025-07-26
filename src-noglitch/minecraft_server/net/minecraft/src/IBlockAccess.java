package net.minecraft.src;

public interface IBlockAccess {
	int getBlockId(int i1, int i2, int i3);
	
	Block getBlock(int x, int y, int z);

	TileEntity getBlockTileEntity(int i1, int i2, int i3);

	int getLightBrightnessForSkyBlocks(int i1, int i2, int i3, int i4);

	float getBrightness(int i1, int i2, int i3, int i4);

	float getLightBrightness(int i1, int i2, int i3);

	int getBlockMetadata(int i1, int i2, int i3);

	Material getBlockMaterial(int i1, int i2, int i3);

	boolean isBlockOpaqueCube(int i1, int i2, int i3);

	boolean isBlockNormalCube(int i1, int i2, int i3);

	boolean isAirBlock(int i1, int i2, int i3);

	BiomeGenBase getBiomeGenForCoords(int i1, int i2);

	int getHeight();

	boolean getAreChunksEmpty();
	
	public boolean doesBlockHaveSolidTopSurface(int par1, int par2, int par3);
}
