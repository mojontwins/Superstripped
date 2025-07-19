package net.minecraft.src;

class VillageAgressor {
	public EntityLiving agressor;
	public int agressionTime;
	final Village villageObj;

	VillageAgressor(Village village1, EntityLiving entityLiving2, int i3) {
		this.villageObj = village1;
		this.agressor = entityLiving2;
		this.agressionTime = i3;
	}
}
