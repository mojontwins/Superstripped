package net.minecraft.src;

public class EntityAIDefendVillage extends EntityAITarget {
	EntityIronGolem irongolem;
	EntityLiving villageAgressorTarget;

	public EntityAIDefendVillage(EntityIronGolem entityIronGolem1) {
		super(entityIronGolem1, 16.0F, false, true);
		this.irongolem = entityIronGolem1;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		Village village1 = this.irongolem.getVillage();
		if(village1 == null) {
			return false;
		} else {
			this.villageAgressorTarget = village1.findNearestVillageAggressor(this.irongolem);
			return this.func_48376_a(this.villageAgressorTarget, false);
		}
	}

	public void startExecuting() {
		this.irongolem.setAttackTarget(this.villageAgressorTarget);
		super.startExecuting();
	}
}
