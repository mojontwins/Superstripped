package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public interface IServerConfigManager {

	public EntityPlayerMP getPlayerEntity(String string);

	public MinecraftServer getServer();

}
