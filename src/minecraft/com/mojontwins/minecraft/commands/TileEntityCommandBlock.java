package com.mojontwins.minecraft.commands;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.Packet;
import net.minecraft.src.Packet91UpdateCommandBlock;
import net.minecraft.src.TileEntity;

public class TileEntityCommandBlock extends TileEntity {
	// This tile entity only serves as a means to contain a command
	public String command = "";
	public boolean blocked;
	public boolean looper; 		// Set and block will re-schedule itself on success
	public String commandResults = "";
	
	public void writeToNBT(NBTTagCompound nBTTagCompound) {
		super.writeToNBT(nBTTagCompound);
		nBTTagCompound.setString("Command", this.command);
		nBTTagCompound.setBoolean("Looper", this.looper);
	}
	
	public void readFromNBT(NBTTagCompound nBTTagCompound) {
		super.readFromNBT(nBTTagCompound);
		this.command = nBTTagCompound.getString("Command");
		this.looper = nBTTagCompound.getBoolean("Looper");
	}
	
	public Packet getDescriptionPacket() {
		return new Packet91UpdateCommandBlock(this.xCoord, this.yCoord, this.zCoord, this.command);
	}
}
