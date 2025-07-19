package net.minecraft.src;

public abstract class EntityAgeable extends EntityCreature {
	public EntityAgeable(World world1) {
		super(world1);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(12, new Integer(0));
	}

	public int getGrowingAge() {
		return this.dataWatcher.getWatchableObjectInt(12);
	}

	public void setGrowingAge(int i1) {
		this.dataWatcher.updateObject(12, i1);
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setInteger("Age", this.getGrowingAge());
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.setGrowingAge(nBTTagCompound1.getInteger("Age"));
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		int i1 = this.getGrowingAge();
		if(i1 < 0) {
			++i1;
			this.setGrowingAge(i1);
		} else if(i1 > 0) {
			--i1;
			this.setGrowingAge(i1);
		}

	}

	public boolean isChild() {
		return this.getGrowingAge() < 0;
	}
}
