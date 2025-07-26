package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;

public class CommandStop extends CommandBase {

	public CommandStop() {
	}

	@Override
	public String getString() {
		return "stop";
	}

	@Override
	public int getMinParams() {
		return 0;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer) {
		this.theCommandSender.printMessage(theWorld, "Failed by command stop!");
		return 0;
	}

	@Override
	public String getHelp() {
		return "Returns 0.";
	}

}
