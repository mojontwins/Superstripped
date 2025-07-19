package net.minecraft.src;

public interface IDyeableEntity {
	public boolean admitsDyeing();
	public int getDyeColor();
	public void setDyeColor(int dyeColor);
}
