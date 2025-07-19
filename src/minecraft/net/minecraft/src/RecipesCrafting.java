package net.minecraft.src;

public class RecipesCrafting {
	public void addRecipes(CraftingManager craftingManager1) {
		craftingManager1.addRecipe(new ItemStack(Block.chest), new Object[]{"###", "# #", "###", '#', Block.planks});
		craftingManager1.addRecipe(new ItemStack(Block.stoneOvenIdle), new Object[]{"###", "# #", "###", '#', Block.cobblestone});
		craftingManager1.addRecipe(new ItemStack(Block.workbench), new Object[]{"##", "##", '#', Block.planks});
		
		craftingManager1.addRecipe(new ItemStack(Block.sandStone), new Object[]{"##", "##", '#', Block.sand});
		craftingManager1.addRecipe(new ItemStack(Block.sandStone, 4, 2), new Object[]{"##", "##", '#', Block.sandStone});
		craftingManager1.addRecipe(new ItemStack(Block.sandStone, 1, 1), new Object[]{"#", "#", '#', new ItemStack(Block.stairSingle, 1, 1)});
	}
}
