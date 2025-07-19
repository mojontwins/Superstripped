package net.minecraft.src;

public class ItemArmor extends Item {
	private static final int[] maxDamageArray = new int[]{11, 16, 15, 13};
	public final int armorType;
	public final int damageReduceAmount;
	public final int renderIndex;
	private final EnumArmorMaterial material;

	public ItemArmor(int i1, EnumArmorMaterial enumArmorMaterial2, int i3, int i4) {
		super(i1);
		this.material = enumArmorMaterial2;
		this.armorType = i4;
		this.renderIndex = i3;
		this.damageReduceAmount = enumArmorMaterial2.getDamageReductionAmount(i4);
		this.setMaxDamage(enumArmorMaterial2.getDurability(i4));
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabCombat;
	}

	public int getItemEnchantability() {
		return this.material.getEnchantability();
	}

	static int[] getMaxDamageArray() {
		return maxDamageArray;
	}
}
