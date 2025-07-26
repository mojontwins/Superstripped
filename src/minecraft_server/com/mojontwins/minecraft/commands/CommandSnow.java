package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandSnow extends CommandBase {

	public CommandSnow() {
	}

	@Override
	public String getString() {
		return "snow";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		theWorld.getWorldInfo().setSnowingTime(0);
		return 0;
	}

	@Override
	public String getHelp() {
		return "Toggles snow on<->off\nReturns: 0";
	}

}
