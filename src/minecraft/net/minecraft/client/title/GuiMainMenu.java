package net.minecraft.client.title;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiSelectWorldAlpha;
import net.minecraft.client.gui.GuiTexturePacks;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.locale.StringTranslate;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.src.Block;
import net.minecraft.src.GameRules;
import net.minecraft.src.MathHelper;
import net.minecraft.src.Version;

public class GuiMainMenu extends GuiScreen {
	private static final Random rand = new Random();
	private String splashText = "missingno";
	private GuiButton multiplayerButton;
	
	String[] logoBlockLayers = new String[]{
			
			" *   * * *   * *** *** *** *** *** ***", 
			" ** ** * **  * *   *   * * * * *    * ", 
			" * * * * * * * **  *   **  *** **   * ", 
			" *   * * *  ** *   *   * * * * *    * ", 
			" *   * * *   * *** *** * * * * *    * "
	};
	
	private LogoEffectRandomizer[][] logoEffects;

	public GuiMainMenu() {
		try {
			ArrayList<String> arrayList1 = new ArrayList<String>();
			BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(GuiMainMenu.class.getResourceAsStream("/title/splashes.txt"), Charset.forName("UTF-8")));
			String string3 = "";

			while((string3 = bufferedReader2.readLine()) != null) {
				string3 = string3.trim();
				if(string3.length() > 0) {
					arrayList1.add(string3);
				}
			}

			do {
				this.splashText = (String)arrayList1.get(rand.nextInt(arrayList1.size()));
			} while(this.splashText.hashCode() == 125780783);
		} catch (Exception exception4) {
		}

	}

	public void updateScreen() {
		if(this.logoEffects != null) {
			for(int i1 = 0; i1 < this.logoEffects.length; ++i1) {
				for(int i2 = 0; i2 < this.logoEffects[i1].length; ++i2) {
					this.logoEffects[i1][i2].updateLogoEffects();
				}
			}
		}

	}

	public boolean doesGuiPauseGame() {
		return false;
	}

	protected void keyTyped(char c1, int i2) {
	}

	public void initGui() {
		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(new Date());
		if(calendar1.get(2) + 1 == 11 && calendar1.get(5) == 9) {
			this.splashText = "Happy birthday, ez!";
		} else if(calendar1.get(2) + 1 == 6 && calendar1.get(5) == 1) {
			this.splashText = "Trans rights are Human rights!";
		} else if(calendar1.get(2) + 1 == 12 && calendar1.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if(calendar1.get(2) + 1 == 1 && calendar1.get(5) == 1) {
			this.splashText = "Happy new year!";
		}

		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		int i4 = this.height / 4 + 48;
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, i4, stringTranslate2.translateKey("menu.singleplayer")));
		this.controlList.add(this.multiplayerButton = new GuiButton(2, this.width / 2 - 100, i4 + 24, stringTranslate2.translateKey("menu.multiplayer")));
		
		this.controlList.add(new GuiButton(3, this.width / 2 - 100, i4 + 48, stringTranslate2.translateKey("menu.mods")));
		
		if(this.mc.hideQuitButton) {
			this.controlList.add(new GuiButton(0, this.width / 2 - 100, i4 + 72, stringTranslate2.translateKey("menu.options")));
		} else {
			this.controlList.add(new GuiButton(0, this.width / 2 - 100, i4 + 72 + 12, 98, 20, stringTranslate2.translateKey("menu.options")));
			this.controlList.add(new GuiButton(4, this.width / 2 + 2, i4 + 72 + 12, 98, 20, stringTranslate2.translateKey("menu.quit")));
		}

		this.controlList.add(new GuiButtonLanguage(5, this.width / 2 - 124, i4 + 72 + 12));
		if(this.mc.session == null) {
			this.multiplayerButton.enabled = false;
		}

	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if(guiButton1.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings));
		}

		if(guiButton1.id == 1) {
			if(GameRules.oldSelectWorldScreen) {
				this.mc.displayGuiScreen(new GuiSelectWorldAlpha(this));
			} else {
				this.mc.displayGuiScreen(new GuiSelectWorld(this));
			}
		}

		if(guiButton1.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if(guiButton1.id == 3) {
			this.mc.displayGuiScreen(new GuiTexturePacks(this));
		}

		if(guiButton1.id == 4) {
			this.mc.shutdown();
		}

	}

	public void drawScreen(int x, int y, float renderPartialTick) {
		this.drawDefaultBackground();
		this.fontRenderer.drawStringWithShadow("Minecraft " + Version.getVersion(), 2, 2, 0xCCCCCC);
		
		Tessellator tessellator4 = Tessellator.instance;
		
		this.drawLogo(renderPartialTick);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	
		tessellator4.setColorOpaque_I(0xFFFFFF);
		GL11.glPushMatrix();
		GL11.glTranslatef((float)(this.width / 2 + 90), 70.0F, 0.0F);
		GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		float f8 = 1.8F - MathHelper.abs(MathHelper.sin((float)(System.currentTimeMillis() % 1000L) / 1000.0F * (float)Math.PI * 2.0F) * 0.1F);
		f8 = f8 * 100.0F / (float)(this.fontRenderer.getStringWidth(this.splashText) + 32);
		GL11.glScalef(f8, f8, f8);
		this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8, 16776960);
		GL11.glPopMatrix();
			
		String string9 = "Copyright Mojang AB. Do not distribute!";
		this.drawString(this.fontRenderer, string9, this.width - this.fontRenderer.getStringWidth(string9) - 2, this.height - 10, 0xFFFFFF);
		super.drawScreen(x, y, renderPartialTick);
	}
	
	private void drawLogo(float renderPartialTick) {
		int i3;
		if(this.logoEffects == null) {
			this.logoEffects = new LogoEffectRandomizer[this.logoBlockLayers[0].length()][this.logoBlockLayers.length];

			for(int i2 = 0; i2 < this.logoEffects.length; ++i2) {
				for(i3 = 0; i3 < this.logoEffects[i2].length; ++i3) {
					this.logoEffects[i2][i3] = new LogoEffectRandomizer(this, i2, i3);
				}
			}
		}

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		ScaledResolution scaledResolution14 = new ScaledResolution(mc.displayWidth, this.mc.displayHeight);
		i3 = 120 * scaledResolution14.scaleFactor;
		GLU.gluPerspective(70.0F, (float)this.mc.displayWidth / (float)i3, 0.05F, 100.0F);
		GL11.glViewport(0, this.mc.displayHeight - i3, this.mc.displayWidth, i3);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glDepthMask(true);

		for(int i4 = 0; i4 < 3; ++i4) {
			GL11.glPushMatrix();
			GL11.glTranslatef(0.4F, 0.6F, -12.0F); // Thanks Birevan! I wanted my logo bigger
			if(i4 == 0) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				GL11.glTranslatef(0.0F, -0.4F, 0.0F);
				GL11.glScalef(0.98F, 1.0F, 1.0F);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}

			if(i4 == 1) {
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
			}

			if(i4 == 2) {
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
			}

			GL11.glScalef(1.0F, -1.0F, 1.0F);
			GL11.glRotatef(15.0F, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(0.89F, 1.0F, 0.4F);
			GL11.glTranslatef((float)(-this.logoBlockLayers[0].length()) * 0.5F, (float)(-this.logoBlockLayers.length) * 0.5F, 0.0F);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			if(i4 == 0) {
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/title/black.png"));
			}

			RenderBlocks renderBlocks5 = new RenderBlocks();

			for(int i6 = 0; i6 < this.logoBlockLayers.length; ++i6) {
				for(int i7 = 0; i7 < this.logoBlockLayers[i6].length(); ++i7) {
					char c8 = this.logoBlockLayers[i6].charAt(i7);
					if(c8 != 32) {
						GL11.glPushMatrix();
						LogoEffectRandomizer logoEffectRandomizer9 = this.logoEffects[i7][i6];
						float f10 = (float)(logoEffectRandomizer9.prevHeight + (logoEffectRandomizer9.height - logoEffectRandomizer9.prevHeight) * (double)renderPartialTick);
						float f11 = 1.0F;
						float f12 = 1.0F;
						float f13 = 0.0F;
						if(i4 == 0) {
							f11 = f10 * 0.04F + 1.0F;
							f12 = 1.0F / f11;
							f10 = 0.0F;
						}

						GL11.glTranslatef((float)i7, (float)i6, f10);
						GL11.glScalef(f11, f11, f11);
						GL11.glRotatef(f13, 0.0F, 1.0F, 0.0F);
						GL11.glEnable(GL11.GL_DEPTH_TEST);
						renderBlocks5.renderBlockAsItem(i4 == 0 ? Block.fire : Block.stone, f12);
						GL11.glPopMatrix();
					}
				}
			}

			GL11.glPopMatrix();
		}

		GL11.glDisable(GL11.GL_BLEND);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public static Random getRandom() {
		return rand;
	}

}
