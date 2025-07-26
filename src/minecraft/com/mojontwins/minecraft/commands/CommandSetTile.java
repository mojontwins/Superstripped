package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class CommandSetTile extends CommandBase {

	public CommandSetTile() {
	}

	@Override
	public String getString() {
		return "setTile";
	}

	@Override
	public int getMinParams() {
		return 4;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int x = this.parseTildeExpression(tokens[1], coordinates.posX);
		int y = this.parseTildeExpression(tokens[2], coordinates.posY);
		int z = this.parseTildeExpression(tokens[3], coordinates.posZ);
		ItemStack itemStack = this.parseItemOrBlock(tokens [4]);
		
		int blockID = itemStack.itemID; 
		int metadata = itemStack.itemDamage;
		
		try {
			theWorld.setBlockAndMetadataWithNotify(x, y, z, blockID, metadata);
		} catch (Exception e) {
			return 0;
		}
		
		return 1;
	}

	@Override
	public String getHelp() {
		return "Adds a block:meta\n/setTile <x> <y> <z> <blockID>[:<metadata>]\nReturns: 1 on success.";
	}

}
