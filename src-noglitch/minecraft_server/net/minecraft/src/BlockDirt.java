package net.minecraft.src;

public class BlockDirt extends Block implements IGround {
	protected BlockDirt(int i1, int i2) {
		super(i1, i2, Material.ground);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}
	
	public boolean canGrowPlants() {
		// TODO Auto-generated method stub
		return true;
	}
}
