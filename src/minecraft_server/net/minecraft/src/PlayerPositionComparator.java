package net.minecraft.src;

import java.util.Comparator;

public class PlayerPositionComparator implements Comparator<Object> {
	private final ChunkCoordinates theChunkCoordinates;

	public PlayerPositionComparator(ChunkCoordinates var1) {
		this.theChunkCoordinates = var1;
	}

	public int comparePlayers(EntityPlayer var1, EntityPlayer var2) {
		double var3 = var1.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
		double var5 = var2.getDistanceSq((double)this.theChunkCoordinates.posX, (double)this.theChunkCoordinates.posY, (double)this.theChunkCoordinates.posZ);
		return var3 < var5 ? -1 : (var3 > var5 ? 1 : 0);
	}

	public int compare(Object var1, Object var2) {
		return this.comparePlayers((EntityPlayer)var1, (EntityPlayer)var2);
	}
}
