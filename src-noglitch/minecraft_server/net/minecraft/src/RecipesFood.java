package net.minecraft.src;

public class RecipesFood {
	public void addRecipes(CraftingManager craftingManager1) {
		craftingManager1.addShapelessRecipe(new ItemStack(Item.bowlSoup), new Object[]{Block.mushroomBrown, Block.mushroomRed, Item.bowlEmpty});
		
		craftingManager1.addRecipe(new ItemStack(Item.cookie, 8), new Object[]{"#X#", 'X', new ItemStack(Item.dyePowder, 1, 3), '#', Item.wheat});
		
	}
}
