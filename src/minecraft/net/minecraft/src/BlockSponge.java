package net.minecraft.src;

public class BlockSponge extends Block {
	protected BlockSponge(int i1) {
		super(i1, Material.sponge);
		this.blockIndexInTexture = 48;
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public void onBlockAdded(World world1, int i2, int i3, int i4) {
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
	}
}
