package net.minecraft.src;

public class MaterialLogic extends Material {
	public MaterialLogic() {
		super();
	}

	public boolean isSolid() {
		return false;
	}

	public boolean getCanBlockGrass() {
		return false;
	}

	public boolean blocksMovement() {
		return false;
	}
}
