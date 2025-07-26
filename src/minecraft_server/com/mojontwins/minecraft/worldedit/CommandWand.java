package com.mojontwins.minecraft.worldedit;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class CommandWand extends CommandWorldEdit {

	@Override
	public String getString() {
		return "wand";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		float f6 = 0.7F;
		double d7 = (double)(theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
		double d9 = (double)(theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
		double d11 = (double)(theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
		EntityItem entityItem13 = new EntityItem(theWorld, thePlayer.posX + d7, thePlayer.posY + d9, thePlayer.posZ + d11, new ItemStack(Item.magicWand));
		entityItem13.delayBeforeCanPickup = 10;
		theWorld.spawnEntityInWorld(entityItem13);
					
		return 1;
	}

	@Override
	public String getHelp() {
		return "Gives you the wand to set corners (right click, SHIFT+right click)";
	}

}
