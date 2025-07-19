package net.minecraft.src;

import java.util.List;

public interface ISaveFormat {
	String getFormatName();

	ISaveHandler getSaveLoader(String string1, boolean z2);

	List<SaveFormatComparator> getSaveList();

	void flushCache();

	WorldInfo getWorldInfo(String string1);

	void deleteWorldDirectory(String string1);

	void renameWorld(String string1, String string2);

	boolean isOldMapFormat(String string1);

	boolean convertMapFormat(String string1, IProgressUpdate iProgressUpdate2);
}
