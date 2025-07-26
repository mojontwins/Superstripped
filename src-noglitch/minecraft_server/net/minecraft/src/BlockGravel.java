package net.minecraft.src;

import java.util.Random;

public class BlockGravel extends BlockSand {
	public BlockGravel(int i1, int i2) {
		super(i1, i2);
		
		this.displayOnCreativeTab = CreativeTabs.tabBlock;
	}

	public int idDropped(int i1, Random random2, int i3) {
		return random2.nextInt(10 - i3 * 3) == 0 ? Item.flint.shiftedIndex : this.blockID;
	}
}
