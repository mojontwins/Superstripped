package net.minecraft.src;

public class ItemSapling extends ItemMetadata {
	public ItemSapling(int i1) {
		super(i1, Block.sapling, 0);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
}
