package net.minecraft.src;

public class ChestItemRenderHelper {
	public static ChestItemRenderHelper instance = new ChestItemRenderHelper();
	private TileEntityChest tileEntityChest = new TileEntityChest();

	public void remder(Block block1, int i2, float f3) {
		TileEntityRenderer.instance.renderTileEntityAt(this.tileEntityChest, 0.0D, 0.0D, 0.0D, 0.0F);
	}
}
