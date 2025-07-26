package net.minecraft.src;

public interface IDyeableBlock {
	public int getMetadataFromDye(int dye);
	public int getDyeFromMetadata(int meta);
	public int[] getTints();
}
