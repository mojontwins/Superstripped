package net.minecraft.src;

import java.io.IOException;
import java.io.InputStream;

import net.minecraft.client.Minecraft;

public abstract class TexturePackBase {
	public String texturePackFileName;
	public String firstDescriptionLine;
	public String secondDescriptionLine;
	public String texturePackID;

	public void func_6482_a() {
	}

	public void closeTexturePackFile() {
	}

	public void func_6485_a(Minecraft minecraft1) throws IOException {
	}

	public void unbindThumbnailTexture(Minecraft minecraft1) {
	}

	public void bindThumbnailTexture(Minecraft minecraft1) {
	}

	public InputStream getResourceAsStream(String string1) {
		return TexturePackBase.class.getResourceAsStream(string1);
	}
}
