package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.World;

public interface ICommand {
	public String getString();
	
	public int getMinParams();
	
	public int execute(String [] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer thePlayer);
	
	public String getHelp();
	
	public CommandBase withCommandSender(ICommandSender commandSender);
}
