package net.minecraft.src;

public class FoodStats {
	private int foodLevel = 20;
	private float foodSaturationLevel = 5.0F;
	private float foodExhaustionLevel;
	private int foodTimer = 0;
	private int prevFoodLevel = 20;

	public void addStats(int i1, float f2) {
		this.foodLevel = Math.min(i1 + this.foodLevel, 20);
		this.foodSaturationLevel = Math.min(this.foodSaturationLevel + (float)i1 * f2 * 2.0F, (float)this.foodLevel);
	}

	public void addStats(ItemFood itemFood1) {
		this.addStats(itemFood1.getHealAmount(), itemFood1.getSaturationModifier());
	}

	public void onUpdate(EntityPlayer entityPlayer1) {
		if(GameRules.enableHunger) {
			int i2 = entityPlayer1.worldObj.difficultySetting;
			this.prevFoodLevel = this.foodLevel;
			if(this.foodExhaustionLevel > 4.0F) {
				this.foodExhaustionLevel -= 4.0F;
				if(this.foodSaturationLevel > 0.0F) {
					this.foodSaturationLevel = Math.max(this.foodSaturationLevel - 1.0F, 0.0F);
				} else if(i2 > 0) {
					this.foodLevel = Math.max(this.foodLevel - 1, 0);
				}
			}
	
			if(this.foodLevel >= 18 && entityPlayer1.shouldHeal()) {
				++this.foodTimer;
				if(this.foodTimer >= 80) {
					entityPlayer1.heal(1);
					this.foodTimer = 0;
				}
			} else if(this.foodLevel <= 0) {
				++this.foodTimer;
				if(this.foodTimer >= 80) {
					if(entityPlayer1.getHealth() > 10 || i2 >= 3 || entityPlayer1.getHealth() > 1 && i2 >= 2) {
						entityPlayer1.attackEntityFrom(DamageSource.starve, 1);
					}
	
					this.foodTimer = 0;
				}
			} else {
				this.foodTimer = 0;
			}
		}
	}

	public void readNBT(NBTTagCompound nBTTagCompound1) {
		if(nBTTagCompound1.hasKey("foodLevel")) {
			this.foodLevel = nBTTagCompound1.getInteger("foodLevel");
			this.foodTimer = nBTTagCompound1.getInteger("foodTickTimer");
			this.foodSaturationLevel = nBTTagCompound1.getFloat("foodSaturationLevel");
			this.foodExhaustionLevel = nBTTagCompound1.getFloat("foodExhaustionLevel");
		}

	}

	public void writeNBT(NBTTagCompound nBTTagCompound1) {
		nBTTagCompound1.setInteger("foodLevel", this.foodLevel);
		nBTTagCompound1.setInteger("foodTickTimer", this.foodTimer);
		nBTTagCompound1.setFloat("foodSaturationLevel", this.foodSaturationLevel);
		nBTTagCompound1.setFloat("foodExhaustionLevel", this.foodExhaustionLevel);
	}

	public int getFoodLevel() {
		return this.foodLevel;
	}

	public int getPrevFoodLevel() {
		return this.prevFoodLevel;
	}

	public boolean needFood() {
		return this.foodLevel < 20;
	}

	public void addExhaustion(float f1) {
		this.foodExhaustionLevel = Math.min(this.foodExhaustionLevel + f1, 40.0F);
	}

	public float getSaturationLevel() {
		return this.foodSaturationLevel;
	}

	public void setFoodLevel(int i1) {
		this.foodLevel = i1;
	}

	public void setFoodSaturationLevel(float f1) {
		this.foodSaturationLevel = f1;
	}
}
