package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandRain extends CommandBase {

	public CommandRain() {
	}

	@Override
	public String getString() {
		return "rain";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		theWorld.getWorldInfo().setRainTime(0);
		return 0;
	}

	@Override
	public String getHelp() {
		return "Toggles rain on<->off\nReturns: 0";
	}

}
