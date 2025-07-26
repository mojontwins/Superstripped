package net.minecraft.src;

public class PlayerCapabilities {
	public boolean disableDamage = false;
	public boolean isFlying = false;
	public boolean allowFlying = false;
	public boolean isCreativeMode = false;

	public void writeCapabilitiesToNBT(NBTTagCompound nBTTagCompound1) {
		NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
		nBTTagCompound2.setBoolean("invulnerable", this.disableDamage);
		nBTTagCompound2.setBoolean("flying", this.isFlying);
		nBTTagCompound2.setBoolean("mayfly", this.allowFlying);
		nBTTagCompound2.setBoolean("instabuild", this.isCreativeMode);
		nBTTagCompound1.setTag("abilities", nBTTagCompound2);
	}

	public void readCapabilitiesFromNBT(NBTTagCompound nBTTagCompound1) {
		if(nBTTagCompound1.hasKey("abilities")) {
			NBTTagCompound nBTTagCompound2 = nBTTagCompound1.getCompoundTag("abilities");
			this.disableDamage = nBTTagCompound2.getBoolean("invulnerable");
			this.isFlying = nBTTagCompound2.getBoolean("flying");
			this.allowFlying = nBTTagCompound2.getBoolean("mayfly");
			this.isCreativeMode = nBTTagCompound2.getBoolean("instabuild");
		}

	}
	
	public String toString() {
		return "Capabilities: disableDamage=" + this.disableDamage + 
				", isFlying=" + this.isFlying +
				", allowFlying=" + this.allowFlying + 
				", isCreativeMode=" + this.isCreativeMode;
	}
}
