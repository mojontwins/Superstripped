package net.minecraft.src;

public abstract class WorldSavedData {
	public final String mapName;
	private boolean dirty;

	public WorldSavedData(String string1) {
		this.mapName = string1;
	}

	public abstract void readFromNBT(NBTTagCompound nBTTagCompound1);

	public abstract void writeToNBT(NBTTagCompound nBTTagCompound1);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean z1) {
		this.dirty = z1;
	}

	public boolean isDirty() {
		return this.dirty;
	}
}
