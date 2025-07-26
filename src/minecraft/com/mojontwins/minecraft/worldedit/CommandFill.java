package com.mojontwins.minecraft.worldedit;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandFill extends CommandWorldEdit {

	@Override
	public String getString() {
		return "fill";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		int blockID = 0;
		int meta = 0;
		
		try {
			blockID = Integer.parseInt(tokens[1]);
			if(idx > 2) meta = Integer.parseInt(tokens[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(this.checkCorners(theWorld)) {
			int cleared = WorldEdit.fill(theWorld, blockID, meta);
			this.theCommandSender.printMessage(theWorld, cleared + " blocks filled.");
			
			return cleared;
		} 		
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "Fills the active area with blockID and meta\n/fill <blockID> [<meta>]\nReturns: blocks filled.";
	}

}
