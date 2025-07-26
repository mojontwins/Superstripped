package com.mojontwins.minecraft.worldedit;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandClear extends CommandWorldEdit {

	@Override
	public String getString() {
		return "clear";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(this.checkCorners(theWorld)) {
			int cleared = WorldEdit.clear(theWorld);
			this.theCommandSender.printMessage(theWorld, cleared + " blocks clear.");
			
			return cleared;
		}
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "Clears the active area.\nReturns: blocks cleared.";
	}

}
