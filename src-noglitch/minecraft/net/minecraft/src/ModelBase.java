package net.minecraft.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ModelBase {
	public float onGround;
	public boolean isRiding = false;
	public List<ModelRenderer> boxList = new ArrayList<ModelRenderer>();
	public boolean isChild = true;
	private Map<String, TextureOffset> modelTextureMap = new HashMap<String, TextureOffset>();
	public int textureWidth = 64;
	public int textureHeight = 32;

	public void render(Entity entity1, float f2, float f3, float f4, float f5, float f6, float f7) {
	}

	public void setRotationAngles(float f1, float f2, float f3, float f4, float f5, float f6) {
	}

	public void setLivingAnimations(EntityLiving entityLiving1, float f2, float f3, float f4) {
	}

	protected void setTextureOffset(String string1, int i2, int i3) {
		this.modelTextureMap.put(string1, new TextureOffset(i2, i3));
	}

	public TextureOffset getTextureOffset(String string1) {
		return (TextureOffset)this.modelTextureMap.get(string1);
	}
}
