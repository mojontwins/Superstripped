package net.minecraft.src;

public class TileEntityMobSpawner extends TileEntity {
	public int delay = -1;
	protected String mobID = "Pig";
	public double yaw;
	public double prevYaw = 0.0D;
	
	private int minArmorTier = 0;
	private int maxArmorTier = 0;

	public TileEntityMobSpawner() {
		this.delay = 20;
	}

	public String getMobID() {
		return this.mobID;
	}

	public void setMobID(String string1) {
		this.mobID = string1;
	}

	public boolean anyPlayerInRange() {
		return this.worldObj.getClosestPlayer((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D, 16.0D) != null;
	}

	public void updateEntity() {
		this.prevYaw = this.yaw;
		if(this.anyPlayerInRange()) {
			double xPos = (double)((float)this.xCoord + this.worldObj.rand.nextFloat());
			double yPos = (double)((float)this.yCoord + this.worldObj.rand.nextFloat());
			double zPos = (double)((float)this.zCoord + this.worldObj.rand.nextFloat());
			this.worldObj.spawnParticle("smoke", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);
			this.worldObj.spawnParticle("flame", xPos, yPos, zPos, 0.0D, 0.0D, 0.0D);

			for(this.yaw += (double)(1000.0F / ((float)this.delay + 200.0F)); this.yaw > 360.0D; this.prevYaw -= 360.0D) {
				this.yaw -= 360.0D;
			}

			if(!this.worldObj.isRemote) {
				if(this.delay == -1) {
					this.updateDelay();
				}

				if(this.delay > 0) {
					--this.delay;
					return;
				}

				byte attempts = 4;

				for(int i = 0; i < attempts; ++i) {
					EntityLiving theEntityLiving = (EntityLiving)((EntityLiving)EntityList.createEntityByName(this.mobID, this.worldObj));
					if(theEntityLiving == null) {
						return;
					}

					int existingEntities = this.worldObj.getEntitiesWithinAABB(theEntityLiving.getClass(), AxisAlignedBB.getBoundingBoxFromPool((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(8.0D, 4.0D, 8.0D)).size();
					if(existingEntities >= 6) {
						this.updateDelay();
						return;
					}

					double x = (double)this.xCoord + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0D;
					double y = (double)(this.yCoord + this.worldObj.rand.nextInt(3) - 1);
					double z = (double)this.zCoord + (this.worldObj.rand.nextDouble() - this.worldObj.rand.nextDouble()) * 4.0D;
					theEntityLiving.setLocationAndAngles(x, y, z, this.worldObj.rand.nextFloat() * 360.0F, 0.0F);
					
					if(theEntityLiving.getCanSpawnHere()) {		
						SpawnerAnimals.creatureSpecificInit(theEntityLiving, this.worldObj, (int)x, (int)y, (int)z);
						if(this.worldObj.checkIfAABBIsClear(theEntityLiving.boundingBox) && this.worldObj.getCollidingBoundingBoxes(theEntityLiving, theEntityLiving.boundingBox).size() == 0 && !this.worldObj.isAnyLiquid(theEntityLiving.boundingBox)) {
						
							this.worldObj.spawnEntityInWorld(theEntityLiving);
							this.worldObj.playAuxSFX(2004, this.xCoord, this.yCoord, this.zCoord, 0);
							theEntityLiving.spawnExplosionParticle();
							this.updateDelay();
							
							// Set armor!
							if(theEntityLiving instanceof EntityArmoredMob && this.maxArmorTier > this.minArmorTier && this.maxArmorTier > 0) {
								InventoryMob inventory = new InventoryMob (theEntityLiving);
								for (int k = 0; k < 4; k ++) {
									inventory.setArmorItemInSlot(3 - k, ItemArmor.getArmorPieceForTier(this.minArmorTier + this.worldObj.rand.nextInt (1 + this.maxArmorTier - this.minArmorTier), k));
								}
								((EntityArmoredMob) theEntityLiving).setInventory(inventory);
							}
						}
	
					}
				}
			}

			super.updateEntity();
		}
	}

	private void updateDelay() {
		this.delay = 200 + this.worldObj.rand.nextInt(600);
	}

	public void readFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readFromNBT(nBTTagCompound1);
		this.mobID = nBTTagCompound1.getString("EntityId");
		this.delay = nBTTagCompound1.getShort("Delay");
		this.setMinArmorTier(nBTTagCompound1.getByte("MinArmorTier"));
		this.setMaxArmorTier(nBTTagCompound1.getByte("MaxArmorTier"));
	}

	public void writeToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeToNBT(nBTTagCompound1);
		nBTTagCompound1.setString("EntityId", this.mobID);
		nBTTagCompound1.setShort("Delay", (short)this.delay);
		nBTTagCompound1.setByte("MinArmorTier", (byte)this.getMinArmorTier());
		nBTTagCompound1.setByte("MaxArmorTier", (byte)this.getMaxArmorTier());
	}

	public Packet getDescriptionPacket() {
		int i1 = EntityList.getIDFromString(this.mobID);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, i1);
	}

	public int getMinArmorTier() {
		return minArmorTier;
	}

	public void setMinArmorTier(int minArmorTier) {
		this.minArmorTier = minArmorTier;
	}

	public int getMaxArmorTier() {
		return maxArmorTier;
	}

	public void setMaxArmorTier(int maxArmorTier) {
		this.maxArmorTier = maxArmorTier;
	}
}
