package com.mojontwins.minecraft.worldedit;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandPaste extends CommandWorldEdit {

	@Override
	public String getString() {
		return "paste";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		WorldEdit.paste(theWorld, thePlayer);
		this.theCommandSender.printMessage(theWorld, WorldEdit.clipboardSize() + " blocks pasted to the world.");
		
		return WorldEdit.clipboardSize();
	}

	@Override
	public String getHelp() {
		return "Pastes the content of the clipboard to the new position.\nReturns: blocks pasted.";
	}

}
