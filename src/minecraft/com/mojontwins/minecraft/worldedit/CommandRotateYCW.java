package com.mojontwins.minecraft.worldedit;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandRotateYCW extends CommandWorldEdit {

	@Override
	public String getString() {
		return "rotatey_cw";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		WorldEdit.rotate_cw();
		return 0;
	}

	@Override
	public String getHelp() {
		return "Rotates the clipboard clockwise around the Y axis";
	}

}
