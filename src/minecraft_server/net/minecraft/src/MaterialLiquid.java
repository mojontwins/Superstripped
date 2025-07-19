package net.minecraft.src;

public class MaterialLiquid extends Material {
	public MaterialLiquid() {
		super();
		this.setGroundCover();
		this.setNoPushMobility();
	}

	public boolean isLiquid() {
		return true;
	}

	public boolean blocksMovement() {
		return false;
	}

	public boolean isSolid() {
		return false;
	}
}
