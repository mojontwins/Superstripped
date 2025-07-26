package net.minecraft.src;

public class EntityAIWatchClosest2 extends EntityAIWatchClosest {
	public EntityAIWatchClosest2(EntityLiving entityLiving1, Class<?> class2, float f3) {
		super(entityLiving1, class2, f3);
		this.setMutexBits(3);
	}

	public EntityAIWatchClosest2(EntityLiving entityLiving1, Class<?> class2, float f3, float f4) {
		super(entityLiving1, class2, f3, f4);
		this.setMutexBits(3);
	}
}
