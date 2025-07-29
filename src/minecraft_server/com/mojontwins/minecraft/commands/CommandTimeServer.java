package com.mojontwins.minecraft.commands;

import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.World;
import net.minecraft.src.WorldServer;

public class CommandTimeServer extends CommandBase {

	@Override
	public String getString() {
		return "time";
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int execute(String[] tokens, int idx, ChunkCoordinates coordinates, World theWorld, EntityPlayer player) {
		if("set".equals(tokens [1])) {
			int timeSet = -1;
			if ("night".equals(tokens [2])) {
				timeSet = 14000;
			} else if ("day".equals(tokens [2])) {
				timeSet = 1000;
			} else {
				timeSet = this.toIntWithDefault(tokens [2], -1);
			}
			if(timeSet >= 0) {
				
				for(int i = 0; i < CommandProcessor.serverConfigManager.getServer().worldMngr.length; i ++) {
					WorldServer worldServer = CommandProcessor.serverConfigManager.getServer().worldMngr[i];
					worldServer.advanceTime(timeSet);
				
				}
				
				this.theCommandSender.printMessage(theWorld, "Time set to " + timeSet);
				return timeSet;
			} else {
				this.theCommandSender.printMessage(theWorld, "Wrong time!");
				return -1;
			}
		} else {
			return (int) (theWorld.getWorldTime() % 24000L);
		}
	}

	@Override
	public String getHelp() {
		return "Sets the world time (0-23999)\n/time [set <n>|day|night]\nReturns: time set";
	}

}
