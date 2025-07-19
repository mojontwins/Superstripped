package net.minecraft.src;

public class EntityAmbientCreature extends EntityLiving implements IAnimals {
	public EntityAmbientCreature(World var1) {
		super(var1);
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}
}
