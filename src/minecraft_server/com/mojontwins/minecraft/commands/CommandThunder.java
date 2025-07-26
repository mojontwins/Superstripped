package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandThunder extends CommandBase {

	public CommandThunder() {
	}

	@Override
	public String getString() {
		return "thunder";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		theWorld.getWorldInfo().setThunderTime(0);
		return 0;
	}

	@Override
	public String getHelp() {
		return "Toggles thunderstorm on<->off\nReturns: 0";
	}

}
