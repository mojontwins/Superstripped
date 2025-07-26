package net.minecraft.src;

public class ItemBattleAxe extends ItemAxe {

	public ItemBattleAxe(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1, 4, enumToolMaterial2);
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}
	
	/*
	 * Add knock back when hitting an entity.
	 */
	public float getExtraKnockbackVsEntity(Entity entity) {
		return 0.7F;
	}

	/*
	 * Default swinging speed = 6, less is faster. 
	 */
	public int getSwingSpeed() {
		return 10;
	}
}
