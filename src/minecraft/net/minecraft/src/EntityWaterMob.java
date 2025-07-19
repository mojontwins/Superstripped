package net.minecraft.src;

public abstract class EntityWaterMob extends EntityCreature implements IAnimals {
	public EntityWaterMob(World world1) {
		super(world1);
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
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
