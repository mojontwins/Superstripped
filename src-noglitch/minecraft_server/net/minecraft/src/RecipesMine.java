package net.minecraft.src;

public class RecipesMine {
	public void addRecipes(CraftingManager cm) {
		cm.addRecipe(new ItemStack(Block.blockCoal, 1), new Object[] {"###", "###", "###", '#', Item.coal});
		cm.addRecipe(new ItemStack(Item.coal, 9), new Object[] {"#", '#', Block.blockCoal});
		
		cm.addShapelessRecipe(new ItemStack(Item.recipeBook, 1), new Object[]{Item.book, new ItemStack(Item.dyePowder, 1, 0)});
		
	}
}
	
