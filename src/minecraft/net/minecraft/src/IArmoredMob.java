package net.minecraft.src;

public interface IArmoredMob {
	public void setArmor(int type, ItemStack itemStack);
	
	public ItemStack getArmor(int type);
}
