package net.minecraft.src;

import com.mojang.nbt.NBTTagCompound;

public class TileEntityRecordPlayer extends TileEntity {
	public int record;

	public void readFromNBT(NBTTagCompound compoundTag) {
		super.readFromNBT(compoundTag);
		this.record = compoundTag.getInteger("Record");
	}

	public void writeToNBT(NBTTagCompound compoundTag) {
		super.writeToNBT(compoundTag);
		if(this.record > 0) {
			compoundTag.setInteger("Record", this.record);
		}

	}
}
