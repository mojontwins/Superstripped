package net.minecraft.src;

public class ItemLeaves extends ItemBlock {
	public ItemLeaves(int i1) {
		super(i1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int i1) {
		return i1 | 4;
	}

	public int getIconFromDamage(int i1) {
		return Block.leaves.getBlockTextureFromSideAndMetadata(0, i1);
	}

	public int getColorFromDamage(int i1, int i2) {
		return (i1 & 1) == 1 ? ColorizerFoliage.getFoliageColorPine() : ((i1 & 2) == 2 ? ColorizerFoliage.getFoliageColorBirch() : ColorizerFoliage.getFoliageColorBasic());
	}
}
