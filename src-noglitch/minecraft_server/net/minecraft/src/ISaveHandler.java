package net.minecraft.src;

import java.io.File;
import java.util.List;

public interface ISaveHandler {
	WorldInfo loadWorldInfo();

	void checkSessionLock();

	IChunkLoader getChunkLoader(WorldProvider worldProvider1);

	void saveWorldInfoAndPlayer(WorldInfo worldInfo1, List<EntityPlayer> list2);

	void saveWorldInfo(WorldInfo worldInfo1);

	ISaveHandler getPlayerNBTManager();

	void s_func_22093_e();

	File getMapFileFromName(String string1);

	String getSaveDirectoryName();
	
	void writePlayerData(EntityPlayer entityPlayer1);

	void readPlayerData(EntityPlayer entityPlayer1);

	String[] s_func_52007_g();
}
