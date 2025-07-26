package net.minecraft.src;

public class EntityArmoredMob extends EntityMob {
	// Added a proper inventory to manage actual armor and stuff
	public InventoryMob inventory;
	protected int carryoverDamage = 0;
		
	public EntityArmoredMob(World var1) {
		super(var1);
		this.inventory = new InventoryMob(this, this.getSecondaryInventorySize()); 
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound var1) {
		super.readEntityFromNBT(var1);
		NBTTagCompound var2 = var1.getCompoundTag("Inventory");
		this.inventory.readFromNBT(var2);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound var1) {
		super.writeEntityToNBT(var1);
		var1.setTag("Inventory", this.inventory.writeToNBT(new NBTTagCompound()));
	}
	
	@Override
	public IInventory getIInventory() {
		return this.inventory;
	}
	
	public void setInventory(IInventory inventory) {
		this.inventory = (InventoryMob) inventory;
	}
	
	@Override
	protected void damageEntity(DamageSource damageSource, int var1) {
		int var2 = 25 - this.inventory.getTotalArmorValue();
		int var3 = var1 * var2 + this.carryoverDamage;
		int strength = var3 / 25;
		this.carryoverDamage = var3 % 25;

		super.damageEntity(damageSource, strength);
	}
	
	public int getSecondaryInventorySize() {
		return 9;
	}

	@Override
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}
}
