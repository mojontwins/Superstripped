package com.mojontwins.minecraft.worldedit;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandCopy extends CommandWorldEdit {

	@Override
	public String getString() {
		return "copy";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		if(this.checkCorners(theWorld)) {
			WorldEdit.copy(theWorld, thePlayer);
			this.theCommandSender.printMessage(theWorld, WorldEdit.clipboardSize() + " blocks copìed to the clipboard.");
			
			return WorldEdit.clipboardSize();
		} 
		
		return 0;
	}

	@Override
	public String getHelp() {
		return "Copies active area to clipboard\nReturns: blocks copied.";
	}

}
