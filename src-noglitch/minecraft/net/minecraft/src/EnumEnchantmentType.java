package net.minecraft.src;

public enum EnumEnchantmentType {
	all,
	armor,
	armor_feet,
	armor_legs,
	armor_torso,
	armor_head,
	weapon,
	digger,
	bow;

	public boolean canEnchantItem(Item item1) {
		if(this == all) {
			return true;
		} else if(item1 instanceof ItemArmor) {
			if(this == armor) {
				return true;
			} else {
				ItemArmor itemArmor2 = (ItemArmor)item1;
				return itemArmor2.armorType == 0 ? this == armor_head : (itemArmor2.armorType == 2 ? this == armor_legs : (itemArmor2.armorType == 1 ? this == armor_torso : (itemArmor2.armorType == 3 ? this == armor_feet : false)));
			}
		} else {
			return item1 instanceof ItemSword ? this == weapon : (item1 instanceof ItemTool ? this == digger : (item1 instanceof ItemBow ? this == bow : false));
		}
	}
}
