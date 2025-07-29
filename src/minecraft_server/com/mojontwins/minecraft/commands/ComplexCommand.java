package com.mojontwins.minecraft.commands;

import net.minecraft.src.BlockPos;

public class ComplexCommand {
	public String command;
	public BlockPos mousePos;
	
	public ComplexCommand(String command, BlockPos mousePos) {
		this.command = command;
		this.mousePos = mousePos;
	}

}
