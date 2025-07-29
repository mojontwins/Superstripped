package net.minecraft.src;

import com.mojang.nbt.NBTTagCompound;

public abstract class EntityWaterMob extends EntityCreature implements IAnimals {
	public EntityWaterMob(World world1) {
		super(world1);
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.checkIfAABBIsClear(this.boundingBox);
	}

	public int getTalkInterval() {
		return 120;
	}

	protected boolean canDespawn() {
		return true;
	}

	protected int getExperiencePoints(EntityPlayer entityPlayer1) {
		return 1 + this.worldObj.rand.nextInt(3);
	}
}
