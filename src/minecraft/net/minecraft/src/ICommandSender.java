package net.minecraft.src;

public interface ICommandSender {
	public void printMessage(World world, String message);

	BlockPos getMouseOverCoordinates();
}
