package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;

public class EntityRenderer {
	public static boolean anaglyphEnable = false;
	public static int anaglyphField;
	private Minecraft mc;
	private float farPlaneDistance = 0.0F;
	public ItemRenderer itemRenderer;
	private int rendererUpdateCount;
	private Entity pointedEntity = null;
	private MouseFilter mouseFilterXAxis = new MouseFilter();
	private MouseFilter mouseFilterYAxis = new MouseFilter();
	private float thirdPersonDistance = 4.0F;
	private float thirdPersonDistanceTemp = 4.0F;
	private float debugCamYaw = 0.0F;
	private float prevDebugCamYaw = 0.0F;
	private float debugCamPitch = 0.0F;
	private float prevDebugCamPitch = 0.0F;
	private float smoothCamYaw;
	private float smoothCamPitch;
	private float smoothCamFilterX;
	private float smoothCamFilterY;
	private float smoothCamPartialTicks;
	private float debugCamFOV = 0.0F;
	private float prevDebugCamFOV = 0.0F;
	private float camRoll = 0.0F;
	private float prevCamRoll = 0.0F;
	public int lightmapTexture;
	private float fovModifierHand;
	private float fovModifierHandPrev;
	private float fovMultiplierTemp;
	private boolean cloudFog = false;
	private double cameraZoom = 1.0D;
	private double cameraYaw = 0.0D;
	private double cameraPitch = 0.0D;
	private long prevFrameTime = System.currentTimeMillis();
	private long renderEndNanoTime = 0L;
	private boolean lightmapUpdateNeeded = false;
	float torchFlickerX = 0.0F;
	float torchFlickerDX = 0.0F;
	float torchFlickerY = 0.0F;
	float torchFlickerDY = 0.0F;
	private Random random = new Random();
	private int rainSoundCounter = 0;
	float[] rainXCoords;
	float[] rainYCoords;
	volatile int field_1394_b = 0;
	volatile int field_1393_c = 0;
	FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
	float fogColorRed;
	float fogColorGreen;
	float fogColorBlue;
	private float fogColor2;
	private float fogColor1;
	public int debugViewDirection;

	public EntityRenderer(Minecraft minecraft1) {
		this.mc = minecraft1;
		this.itemRenderer = new ItemRenderer(minecraft1);
		this.lightmapTexture = minecraft1.renderEngine.allocateAndSetupTexture(new BufferedImage(16, 16, 1));
	}

	public void updateRenderer() {
		this.updateFovModifierHand();
		this.updateTorchFlicker();
		this.fogColor2 = this.fogColor1;
		this.thirdPersonDistanceTemp = this.thirdPersonDistance;
		this.prevDebugCamYaw = this.debugCamYaw;
		this.prevDebugCamPitch = this.debugCamPitch;
		this.prevDebugCamFOV = this.debugCamFOV;
		this.prevCamRoll = this.camRoll;
		float f1;
		float f2;
		if(GameSettingsValues.smoothCamera) {
			f1 = GameSettingsValues.mouseSensitivity * 0.6F + 0.2F;
			f2 = f1 * f1 * f1 * 8.0F;
			this.smoothCamFilterX = this.mouseFilterXAxis.func_22386_a(this.smoothCamYaw, 0.05F * f2);
			this.smoothCamFilterY = this.mouseFilterYAxis.func_22386_a(this.smoothCamPitch, 0.05F * f2);
			this.smoothCamPartialTicks = 0.0F;
			this.smoothCamYaw = 0.0F;
			this.smoothCamPitch = 0.0F;
		}

		if(this.mc.renderViewEntity == null) {
			this.mc.renderViewEntity = this.mc.thePlayer;
		}

		f1 = this.mc.theWorld.getLightBrightness(MathHelper.floor_double(this.mc.renderViewEntity.posX), MathHelper.floor_double(this.mc.renderViewEntity.posY), MathHelper.floor_double(this.mc.renderViewEntity.posZ));
		f2 = (float)(3 - GameSettingsValues.renderDistance) / 3.0F;
		float f3 = f1 * (1.0F - f2) + f2;
		this.fogColor1 += (f3 - this.fogColor1) * 0.1F;
		++this.rendererUpdateCount;
		this.itemRenderer.updateEquippedItem();
		this.addRainParticles();
	}

	public void getMouseOver(float f1) {
		if(this.mc.renderViewEntity != null) {
			if(this.mc.theWorld != null) {
				double d2 = (double)this.mc.playerController.getBlockReachDistance();
				this.mc.objectMouseOver = this.mc.renderViewEntity.rayTrace(d2, f1);
				double d4 = d2;
				Vec3D vec3D6 = this.mc.renderViewEntity.getCurrentNodeVec3d(f1);
				if(this.mc.playerController.extendedReach()) {
					d2 = 6.0D;
					d4 = 6.0D;
				} else {
					if(d2 > 3.0D) {
						d4 = 3.0D;
					}

					d2 = d4;
				}

				if(this.mc.objectMouseOver != null) {
					d4 = this.mc.objectMouseOver.hitVec.distanceTo(vec3D6);
				}

				Vec3D vec3D7 = this.mc.renderViewEntity.getLook(f1);
				Vec3D vec3D8 = vec3D6.addVector(vec3D7.xCoord * d2, vec3D7.yCoord * d2, vec3D7.zCoord * d2);
				this.pointedEntity = null;
				float f9 = 1.0F;
				List<Entity> list10 = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.renderViewEntity, this.mc.renderViewEntity.boundingBox.addCoord(vec3D7.xCoord * d2, vec3D7.yCoord * d2, vec3D7.zCoord * d2).expand((double)f9, (double)f9, (double)f9));
				double d11 = d4;

				for(int i13 = 0; i13 < list10.size(); ++i13) {
					Entity entity14 = (Entity)list10.get(i13);
					if(entity14.canBeCollidedWith()) {
						float f15 = entity14.getCollisionBorderSize();
						AxisAlignedBB axisAlignedBB16 = entity14.boundingBox.expand((double)f15, (double)f15, (double)f15);
						MovingObjectPosition movingObjectPosition17 = axisAlignedBB16.calculateIntercept(vec3D6, vec3D8);
						if(axisAlignedBB16.isVecInside(vec3D6)) {
							if(0.0D < d11 || d11 == 0.0D) {
								this.pointedEntity = entity14;
								d11 = 0.0D;
							}
						} else if(movingObjectPosition17 != null) {
							double d18 = vec3D6.distanceTo(movingObjectPosition17.hitVec);
							if(d18 < d11 || d11 == 0.0D) {
								this.pointedEntity = entity14;
								d11 = d18;
							}
						}
					}
				}

				if(this.pointedEntity != null && (d11 < d4 || this.mc.objectMouseOver == null)) {
					this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity);
				}

			}
		}
	}

	private void updateFovModifierHand() {
		EntityPlayerSP entityPlayerSP1 = (EntityPlayerSP)this.mc.renderViewEntity;
		this.fovMultiplierTemp = entityPlayerSP1.getFOVMultiplier();
		this.fovModifierHandPrev = this.fovModifierHand;
		this.fovModifierHand += (this.fovMultiplierTemp - this.fovModifierHand) * 0.5F;
	}

	private float getFOVModifier(float f1, boolean z2) {
		if(this.debugViewDirection > 0) {
			return 90.0F;
		} else {
			EntityPlayer entityPlayer3 = (EntityPlayer)this.mc.renderViewEntity;
			float f4 = 70.0F;
			if(z2) {
				f4 += GameSettingsValues.fovSetting * 40.0F;
				f4 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * f1;
			}

			if(entityPlayer3.getHealth() <= 0) {
				float f5 = (float)entityPlayer3.deathTime + f1;
				f4 /= (1.0F - 500.0F / (f5 + 500.0F)) * 2.0F + 1.0F;
			}

			int i6 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, entityPlayer3, f1);
			if(i6 != 0 && Block.blocksList[i6].blockMaterial == Material.water) {
				/*if(this.mc.thePlayer.divingHelmetOn()) {
					f4 = f4 * 50.0F / 70.0F;
				} else */{
					f4 = f4 * 60.0F / 70.0F;
				}
			}

			return f4 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * f1;
		}
	}

	private void hurtCameraEffect(float f1) {
		EntityLiving entityLiving2 = this.mc.renderViewEntity;
		float f3 = (float)entityLiving2.hurtTime - f1;
		float f4;
		if(entityLiving2.getHealth() <= 0) {
			f4 = (float)entityLiving2.deathTime + f1;
			GL11.glRotatef(40.0F - 8000.0F / (f4 + 200.0F), 0.0F, 0.0F, 1.0F);
		}

		if(f3 >= 0.0F) {
			f3 /= (float)entityLiving2.maxHurtTime;
			f3 = MathHelper.sin(f3 * f3 * f3 * f3 * (float)Math.PI);
			f4 = entityLiving2.attackedAtYaw;
			GL11.glRotatef(-f4, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-f3 * 14.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(f4, 0.0F, 1.0F, 0.0F);
		}
	}

	private void setupViewBobbing(float f1) {
		if(this.mc.renderViewEntity instanceof EntityPlayer) {
			EntityPlayer entityPlayer2 = (EntityPlayer)this.mc.renderViewEntity;
			float f3 = entityPlayer2.distanceWalkedModified - entityPlayer2.prevDistanceWalkedModified;
			float f4 = -(entityPlayer2.distanceWalkedModified + f3 * f1);
			float f5 = entityPlayer2.prevCameraYaw + (entityPlayer2.cameraYaw - entityPlayer2.prevCameraYaw) * f1;
			float f6 = entityPlayer2.prevCameraPitch + (entityPlayer2.cameraPitch - entityPlayer2.prevCameraPitch) * f1;
			GL11.glTranslatef(MathHelper.sin(f4 * (float)Math.PI) * f5 * 0.5F, -Math.abs(MathHelper.cos(f4 * (float)Math.PI) * f5), 0.0F);
			GL11.glRotatef(MathHelper.sin(f4 * (float)Math.PI) * f5 * 3.0F, 0.0F, 0.0F, 1.0F);
			GL11.glRotatef(Math.abs(MathHelper.cos(f4 * (float)Math.PI - 0.2F) * f5) * 5.0F, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(f6, 1.0F, 0.0F, 0.0F);
		}
	}

	private void orientCamera(float f1) {
		EntityLiving entityLiving2 = this.mc.renderViewEntity;
		float f3 = entityLiving2.yOffset - 1.62F;
		double d4 = entityLiving2.prevPosX + (entityLiving2.posX - entityLiving2.prevPosX) * (double)f1;
		double d6 = entityLiving2.prevPosY + (entityLiving2.posY - entityLiving2.prevPosY) * (double)f1 - (double)f3;
		double d8 = entityLiving2.prevPosZ + (entityLiving2.posZ - entityLiving2.prevPosZ) * (double)f1;
		GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * f1, 0.0F, 0.0F, 1.0F);
		if(entityLiving2.isPlayerSleeping()) {
			f3 = (float)((double)f3 + 1.0D);
			GL11.glTranslatef(0.0F, 0.3F, 0.0F);
			if(!GameSettingsValues.debugCamEnable) {
				/*
				int i10 = this.mc.theWorld.getBlockId(MathHelper.floor_double(entityLiving2.posX), MathHelper.floor_double(entityLiving2.posY), MathHelper.floor_double(entityLiving2.posZ));
				
				if(i10 == Block.bed.blockID) {
					int i11 = this.mc.theWorld.getBlockMetadata(MathHelper.floor_double(entityLiving2.posX), MathHelper.floor_double(entityLiving2.posY), MathHelper.floor_double(entityLiving2.posZ));
					int i12 = i11 & 3;
					GL11.glRotatef((float)(i12 * 90), 0.0F, 1.0F, 0.0F);
				}
				*/

				GL11.glRotatef(entityLiving2.prevRotationYaw + (entityLiving2.rotationYaw - entityLiving2.prevRotationYaw) * f1 + 180.0F, 0.0F, -1.0F, 0.0F);
				GL11.glRotatef(entityLiving2.prevRotationPitch + (entityLiving2.rotationPitch - entityLiving2.prevRotationPitch) * f1, -1.0F, 0.0F, 0.0F);
			}
		} else if(GameSettingsValues.thirdPersonView > 0) {
			double d27 = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * f1);
			float f13;
			float f28;
			if(GameSettingsValues.debugCamEnable) {
				f28 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * f1;
				f13 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * f1;
				GL11.glTranslatef(0.0F, 0.0F, (float)(-d27));
				GL11.glRotatef(f13, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(f28, 0.0F, 1.0F, 0.0F);
			} else {
				f28 = entityLiving2.rotationYaw;
				f13 = entityLiving2.rotationPitch;
				if(GameSettingsValues.thirdPersonView == 2) {
					f13 += 180.0F;
				}

				double d14 = (double)(-MathHelper.sin(f28 / 180.0F * (float)Math.PI) * MathHelper.cos(f13 / 180.0F * (float)Math.PI)) * d27;
				double d16 = (double)(MathHelper.cos(f28 / 180.0F * (float)Math.PI) * MathHelper.cos(f13 / 180.0F * (float)Math.PI)) * d27;
				double d18 = (double)(-MathHelper.sin(f13 / 180.0F * (float)Math.PI)) * d27;

				for(int i20 = 0; i20 < 8; ++i20) {
					float f21 = (float)((i20 & 1) * 2 - 1);
					float f22 = (float)((i20 >> 1 & 1) * 2 - 1);
					float f23 = (float)((i20 >> 2 & 1) * 2 - 1);
					f21 *= 0.1F;
					f22 *= 0.1F;
					f23 *= 0.1F;
					MovingObjectPosition movingObjectPosition24 = this.mc.theWorld.rayTraceBlocks(Vec3D.createVector(d4 + (double)f21, d6 + (double)f22, d8 + (double)f23), Vec3D.createVector(d4 - d14 + (double)f21 + (double)f23, d6 - d18 + (double)f22, d8 - d16 + (double)f23));
					if(movingObjectPosition24 != null) {
						double d25 = movingObjectPosition24.hitVec.distanceTo(Vec3D.createVector(d4, d6, d8));
						if(d25 < d27) {
							d27 = d25;
						}
					}
				}

				if(GameSettingsValues.thirdPersonView == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GL11.glRotatef(entityLiving2.rotationPitch - f13, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(entityLiving2.rotationYaw - f28, 0.0F, 1.0F, 0.0F);
				GL11.glTranslatef(0.0F, 0.0F, (float)(-d27));
				GL11.glRotatef(f28 - entityLiving2.rotationYaw, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(f13 - entityLiving2.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GL11.glTranslatef(0.0F, 0.0F, -0.1F);
		}

		if(!GameSettingsValues.debugCamEnable) {
			GL11.glRotatef(entityLiving2.prevRotationPitch + (entityLiving2.rotationPitch - entityLiving2.prevRotationPitch) * f1, 1.0F, 0.0F, 0.0F);
			GL11.glRotatef(entityLiving2.prevRotationYaw + (entityLiving2.rotationYaw - entityLiving2.prevRotationYaw) * f1 + 180.0F, 0.0F, 1.0F, 0.0F);
		}

		GL11.glTranslatef(0.0F, f3, 0.0F);
		d4 = entityLiving2.prevPosX + (entityLiving2.posX - entityLiving2.prevPosX) * (double)f1;
		d6 = entityLiving2.prevPosY + (entityLiving2.posY - entityLiving2.prevPosY) * (double)f1 - (double)f3;
		d8 = entityLiving2.prevPosZ + (entityLiving2.posZ - entityLiving2.prevPosZ) * (double)f1;
		this.cloudFog = this.mc.renderGlobal.func_27307_a(d4, d6, d8, f1);
	}

	private void setupCameraTransform(float f1, int i2) {
		this.farPlaneDistance = (float)(256 >> GameSettingsValues.renderDistance);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		float f3 = 0.07F;
		if(GameSettingsValues.anaglyph) {
			GL11.glTranslatef((float)(-(i2 * 2 - 1)) * f3, 0.0F, 0.0F);
		}

		if(this.cameraZoom != 1.0D) {
			GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
			GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
		}

		GLU.gluPerspective(this.getFOVModifier(f1, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
		float f4;
		if(this.mc.playerController.func_35643_e()) {
			f4 = 0.6666667F;
			GL11.glScalef(1.0F, f4, 1.0F);
		}

		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		if(GameSettingsValues.anaglyph) {
			GL11.glTranslatef((float)(i2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		this.hurtCameraEffect(f1);
		if(GameSettingsValues.viewBobbing) {
			this.setupViewBobbing(f1);
		}

		f4 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * f1;
		if(f4 > 0.0F) {
			byte b5 = 20;

			float f6 = 5.0F / (f4 * f4 + 5.0F) - f4 * 0.04F;
			f6 *= f6;
			GL11.glRotatef(((float)this.rendererUpdateCount + f1) * (float)b5, 0.0F, 1.0F, 1.0F);
			GL11.glScalef(1.0F / f6, 1.0F, 1.0F);
			GL11.glRotatef(-((float)this.rendererUpdateCount + f1) * (float)b5, 0.0F, 1.0F, 1.0F);
		}

		this.orientCamera(f1);
		if(this.debugViewDirection > 0) {
			int i7 = this.debugViewDirection - 1;
			if(i7 == 1) {
				GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
			}

			if(i7 == 2) {
				GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
			}

			if(i7 == 3) {
				GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
			}

			if(i7 == 4) {
				GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
			}

			if(i7 == 5) {
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
			}
		}

	}

	private void renderHand(float f1, int i2) {
		if(this.debugViewDirection <= 0) {
			GL11.glMatrixMode(GL11.GL_PROJECTION);
			GL11.glLoadIdentity();
			float f3 = 0.07F;
			if(GameSettingsValues.anaglyph) {
				GL11.glTranslatef((float)(-(i2 * 2 - 1)) * f3, 0.0F, 0.0F);
			}

			if(this.cameraZoom != 1.0D) {
				GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
				GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0D);
			}

			GLU.gluPerspective(this.getFOVModifier(f1, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
			if(this.mc.playerController.func_35643_e()) {
				float f4 = 0.6666667F;
				GL11.glScalef(1.0F, f4, 1.0F);
			}

			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glLoadIdentity();
			if(GameSettingsValues.anaglyph) {
				GL11.glTranslatef((float)(i2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
			}

			GL11.glPushMatrix();
			this.hurtCameraEffect(f1);
			if(GameSettingsValues.viewBobbing) {
				this.setupViewBobbing(f1);
			}

			if(GameSettingsValues.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping() && !GameSettingsValues.hideGUI && !this.mc.playerController.func_35643_e()) {
				this.enableLightmap((double)f1);
				this.itemRenderer.renderItemInFirstPerson(f1);
				this.disableLightmap((double)f1);
			}

			GL11.glPopMatrix();
			if(GameSettingsValues.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping()) {
				this.itemRenderer.renderOverlays(f1);
				this.hurtCameraEffect(f1);
			}

			if(GameSettingsValues.viewBobbing) {
				this.setupViewBobbing(f1);
			}

		}
	}

	public void disableLightmap(double d1) {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void enableLightmap(double d1) {
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glMatrixMode(GL11.GL_TEXTURE);
		GL11.glLoadIdentity();
		float f3 = 0.00390625F;
		GL11.glScalef(f3, f3, f3);
		GL11.glTranslatef(8.0F, 8.0F, 8.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		this.mc.renderEngine.bindTexture(this.lightmapTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	private void updateTorchFlicker() {
		this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.torchFlickerDY = (float)((double)this.torchFlickerDY + (Math.random() - Math.random()) * Math.random() * Math.random());
		this.torchFlickerDX = (float)((double)this.torchFlickerDX * 0.9D);
		this.torchFlickerDY = (float)((double)this.torchFlickerDY * 0.9D);
		this.torchFlickerX += (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
		this.torchFlickerY += (this.torchFlickerDY - this.torchFlickerY) * 1.0F;
		this.lightmapUpdateNeeded = true;
	}
	
	private void updateLightmap() {
		if(this.mc.theWorld != null) {
			this.mc.renderEngine.createTextureFromBytes(
					this.mc.theWorld.worldProvider.updateLightmap(
							this.torchFlickerX,
							GameSettingsValues.gammaSetting,
							this.mc.thePlayer
					),
					16, 16, this.lightmapTexture);
			this.lightmapUpdateNeeded = false;
		}
	}

	public void updateCameraAndRender(float f1) {
		//Profiler.startSection("lightTex");
		if(this.lightmapUpdateNeeded) {
			this.updateLightmap();
		}

		//Profiler.endSection();
		if(!Display.isActive()) {
			if(System.currentTimeMillis() - this.prevFrameTime > 500L) {
				this.mc.displayInGameMenu();
			}
		} else {
			this.prevFrameTime = System.currentTimeMillis();
		}

		//Profiler.startSection("mouse");
		if(this.mc.inGameHasFocus) {
			this.mc.mouseHelper.mouseXYChange();
			float f2 = GameSettingsValues.mouseSensitivity * 0.6F + 0.2F;
			float f3 = f2 * f2 * f2 * 8.0F;
			float f4 = (float)this.mc.mouseHelper.deltaX * f3;
			float f5 = (float)this.mc.mouseHelper.deltaY * f3;
			byte b6 = 1;
			if(GameSettingsValues.invertMouse) {
				b6 = -1;
			}

			if(GameSettingsValues.smoothCamera) {
				this.smoothCamYaw += f4;
				this.smoothCamPitch += f5;
				float f7 = f1 - this.smoothCamPartialTicks;
				this.smoothCamPartialTicks = f1;
				f4 = this.smoothCamFilterX * f7;
				f5 = this.smoothCamFilterY * f7;
				this.mc.thePlayer.setAngles(f4, f5 * (float)b6);
			} else {
				this.mc.thePlayer.setAngles(f4, f5 * (float)b6);
			}
		}

		//Profiler.endSection();
		if(!this.mc.skipRenderWorld) {
			anaglyphEnable = GameSettingsValues.anaglyph;
			ScaledResolution scaledResolution13 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
			int i14 = scaledResolution13.getScaledWidth();
			int i15 = scaledResolution13.getScaledHeight();
			int i16 = Mouse.getX() * i14 / this.mc.displayWidth;
			int i17 = i15 - Mouse.getY() * i15 / this.mc.displayHeight - 1;
			short s18 = 200;
			if(GameSettingsValues.limitFramerate == 1) {
				s18 = 120;
			}

			if(GameSettingsValues.limitFramerate == 2) {
				s18 = 40;
			}

			long j8;
			if(this.mc.theWorld != null) {
				//Profiler.startSection("level");
				if(GameSettingsValues.limitFramerate == 0) {
					this.renderWorld(f1, 0L);
				} else {
					this.renderWorld(f1, this.renderEndNanoTime + (long)(1000000000 / s18));
				}

				//Profiler.endStartSection("sleep");
				if(GameSettingsValues.limitFramerate == 2) {
					j8 = (this.renderEndNanoTime + (long)(1000000000 / s18) - System.nanoTime()) / 1000000L;
					if(j8 > 0L && j8 < 500L) {
						try {
							Thread.sleep(j8);
						} catch (InterruptedException interruptedException12) {
							interruptedException12.printStackTrace();
						}
					}
				}

				this.renderEndNanoTime = System.nanoTime();
				//Profiler.endStartSection("gui");
				if(!GameSettingsValues.hideGUI || this.mc.currentScreen != null) {
					this.mc.ingameGUI.renderGameOverlay(f1, this.mc.currentScreen != null, i16, i17);
				}

				//Profiler.endSection();
			} else {
				GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glLoadIdentity();
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				this.setupOverlayRendering();
				j8 = (this.renderEndNanoTime + (long)(1000000000 / s18) - System.nanoTime()) / 1000000L;
				if(j8 < 0L) {
					j8 += 10L;
				}

				if(j8 > 0L && j8 < 500L) {
					try {
						Thread.sleep(j8);
					} catch (InterruptedException interruptedException11) {
						interruptedException11.printStackTrace();
					}
				}

				this.renderEndNanoTime = System.nanoTime();
			}

			if(this.mc.currentScreen != null) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				this.mc.currentScreen.drawScreen(i16, i17, f1);
				if(this.mc.currentScreen != null && this.mc.currentScreen.guiParticles != null) {
					this.mc.currentScreen.guiParticles.draw(f1);
				}
			}

		}
	}

	public void renderWorld(float f1, long j2) {
		//Profiler.startSection("lightTex");
		if(this.lightmapUpdateNeeded) {
			this.updateLightmap();
		}

		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		if(this.mc.renderViewEntity == null) {
			this.mc.renderViewEntity = this.mc.thePlayer;
		}

		//Profiler.endStartSection("pick");
		this.getMouseOver(f1);
		EntityLiving entityLiving4 = this.mc.renderViewEntity;
		RenderGlobal renderGlobal5 = this.mc.renderGlobal;
		EffectRenderer effectRenderer6 = this.mc.effectRenderer;
		double d7 = entityLiving4.lastTickPosX + (entityLiving4.posX - entityLiving4.lastTickPosX) * (double)f1;
		double d9 = entityLiving4.lastTickPosY + (entityLiving4.posY - entityLiving4.lastTickPosY) * (double)f1;
		double d11 = entityLiving4.lastTickPosZ + (entityLiving4.posZ - entityLiving4.lastTickPosZ) * (double)f1;
		int i16;

		for(int i18 = 0; i18 < 2; ++i18) {
			if(GameSettingsValues.anaglyph) {
				anaglyphField = i18;
				if(anaglyphField == 0) {
					GL11.glColorMask(false, true, true, false);
				} else {
					GL11.glColorMask(true, false, false, false);
				}
			}

			//Profiler.endStartSection("clear");
			GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
			this.updateFogColor(f1);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL11.GL_CULL_FACE);
			//Profiler.endStartSection("camera");
			this.setupCameraTransform(f1, i18);
			ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, GameSettingsValues.thirdPersonView == 2);
			//Profiler.endStartSection("frustrum");
			ClippingHelperImpl.getInstance();
			if(GameSettingsValues.renderDistance < 2) {
				this.setupFog(-1, f1);
				//Profiler.endStartSection("sky");
				renderGlobal5.renderSky(f1);
			}

			GL11.glEnable(GL11.GL_FOG);
			this.setupFog(1, f1);
			if(GameSettingsValues.ambientOcclusion) {
				GL11.glShadeModel(GL11.GL_SMOOTH);
			}

			//Profiler.endStartSection("culling");
			Frustrum frustrum19 = new Frustrum();
			frustrum19.setPosition(d7, d9, d11);
			this.mc.renderGlobal.clipRenderersByFrustum(frustrum19, f1);
			if(i18 == 0) {
				//Profiler.endStartSection("updatechunks");

				while(!this.mc.renderGlobal.updateRenderers(entityLiving4, false) && j2 != 0L) {
					long j20 = j2 - System.nanoTime();
					if(j20 < 0L || j20 > 1000000000L) {
						break;
					}
				}
			}

			this.setupFog(0, f1);
			GL11.glEnable(GL11.GL_FOG);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			RenderHelper.disableStandardItemLighting();
			//Profiler.endStartSection("terrain");
			renderGlobal5.sortAndRender(entityLiving4, 0, (double)f1);
			GL11.glShadeModel(GL11.GL_FLAT);
			EntityPlayer entityPlayer21;
			if(this.debugViewDirection == 0) {
				RenderHelper.enableStandardItemLighting();
				//Profiler.endStartSection("entities");
				renderGlobal5.renderEntities(entityLiving4.getCurrentNodeVec3d(f1), frustrum19, f1);
				this.enableLightmap((double)f1);
				//Profiler.endStartSection("litParticles");
				effectRenderer6.func_1187_b(entityLiving4, f1);
				RenderHelper.disableStandardItemLighting();
				this.setupFog(0, f1);
				//Profiler.endStartSection("particles");
				effectRenderer6.renderParticles(entityLiving4, f1);
				this.disableLightmap((double)f1);
				if(this.mc.objectMouseOver != null && entityLiving4.isInsideOfMaterial(Material.water) && entityLiving4 instanceof EntityPlayer && !GameSettingsValues.hideGUI) {
					entityPlayer21 = (EntityPlayer)entityLiving4;
					GL11.glDisable(GL11.GL_ALPHA_TEST);
					//Profiler.endStartSection("outline");
					renderGlobal5.drawBlockBreaking(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
					renderGlobal5.drawSelectionBox(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
					GL11.glEnable(GL11.GL_ALPHA_TEST);
				}
			}

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glDepthMask(true);
			this.setupFog(0, f1);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/terrain.png"));
			if(GameSettingsValues.fancyGraphics) {
				//Profiler.endStartSection("water");
				if(GameSettingsValues.ambientOcclusion) {
					GL11.glShadeModel(GL11.GL_SMOOTH);
				}

				GL11.glColorMask(false, false, false, false);
				i16 = renderGlobal5.sortAndRender(entityLiving4, 1, (double)f1);
				if(GameSettingsValues.anaglyph) {
					if(anaglyphField == 0) {
						GL11.glColorMask(false, true, true, true);
					} else {
						GL11.glColorMask(true, false, false, true);
					}
				} else {
					GL11.glColorMask(true, true, true, true);
				}

				if(i16 > 0) {
					renderGlobal5.renderAllRenderLists(1, (double)f1);
				}

				GL11.glShadeModel(GL11.GL_FLAT);
			} else {
				//Profiler.endStartSection("water");
				renderGlobal5.sortAndRender(entityLiving4, 1, (double)f1);
			}

			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			if(this.cameraZoom == 1.0D && entityLiving4 instanceof EntityPlayer && !GameSettingsValues.hideGUI && this.mc.objectMouseOver != null && !entityLiving4.isInsideOfMaterial(Material.water)) {
				entityPlayer21 = (EntityPlayer)entityLiving4;
				GL11.glDisable(GL11.GL_ALPHA_TEST);
				//Profiler.endStartSection("outline");
				renderGlobal5.drawBlockBreaking(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
				renderGlobal5.drawSelectionBox(entityPlayer21, this.mc.objectMouseOver, 0, entityPlayer21.inventory.getCurrentItem(), f1);
				GL11.glEnable(GL11.GL_ALPHA_TEST);
			}

			//Profiler.endStartSection("weather");
			this.renderRainSnow(f1);
			GL11.glDisable(GL11.GL_FOG);
			if(this.pointedEntity != null) {
				;
			}

			if(GameSettingsValues.shouldRenderClouds() && !this.mc.theWorld.worldProvider.isCaveWorld) {
				//Profiler.endStartSection("clouds");
				GL11.glPushMatrix();
				this.setupFog(0, f1);
				GL11.glEnable(GL11.GL_FOG);
				renderGlobal5.renderClouds(f1);
				GL11.glDisable(GL11.GL_FOG);
				this.setupFog(1, f1);
				GL11.glPopMatrix();
			}

			//Profiler.endStartSection("hand");
			if(this.cameraZoom == 1.0D) {
				GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
				this.renderHand(f1, i18);
			}

			if(!GameSettingsValues.anaglyph) {
				//Profiler.endSection();
				return;
			}
		}

		GL11.glColorMask(true, true, true, false);
		//Profiler.endSection();
	}

	private void addRainParticles() {
		float f1 = this.mc.theWorld.getRainStrength(1.0F);
		if(!GameSettingsValues.fancyGraphics) {
			f1 /= 2.0F;
		}

		if(f1 != 0.0F) {
			this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
			EntityLiving entityLiving2 = this.mc.renderViewEntity;
			World world3 = this.mc.theWorld;
			int i4 = MathHelper.floor_double(entityLiving2.posX);
			int i5 = MathHelper.floor_double(entityLiving2.posY);
			int i6 = MathHelper.floor_double(entityLiving2.posZ);
			byte b7 = 10;
			double d8 = 0.0D;
			double d10 = 0.0D;
			double d12 = 0.0D;
			int i14 = 0;
			int i15 = (int)(100.0F * f1 * f1);
			if(GameSettingsValues.particleSetting == 1) {
				i15 >>= 1;
			} else if(GameSettingsValues.particleSetting == 2) {
				i15 = 0;
			}

			for(int i16 = 0; i16 < i15; ++i16) {
				int i17 = i4 + this.random.nextInt(b7) - this.random.nextInt(b7);
				int i18 = i6 + this.random.nextInt(b7) - this.random.nextInt(b7);
				int i19 = world3.getPrecipitationHeight(i17, i18);
				int i20 = world3.getBlockId(i17, i19 - 1, i18);
				BiomeGenBase biomeGenBase21 = world3.getBiomeGenForCoords(i17, i18);
				if(i19 <= i5 + b7 && i19 >= i5 - b7 && biomeGenBase21.canSpawnLightningBolt() && biomeGenBase21.getFloatTemperature() > 0.2F) {
					float f22 = this.random.nextFloat();
					float f23 = this.random.nextFloat();
					if(i20 > 0) {
						if(Block.blocksList[i20].blockMaterial == Material.lava) {
							this.mc.effectRenderer.addEffect(new EntitySmokeFX(world3, (double)((float)i17 + f22), (double)((float)i19 + 0.1F) - Block.blocksList[i20].minY, (double)((float)i18 + f23), 0.0D, 0.0D, 0.0D));
						} else {
							++i14;
							if(this.random.nextInt(i14) == 0) {
								d8 = (double)((float)i17 + f22);
								d10 = (double)((float)i19 + 0.1F) - Block.blocksList[i20].minY;
								d12 = (double)((float)i18 + f23);
							}

							this.mc.effectRenderer.addEffect(new EntityRainFX(world3, (double)((float)i17 + f22), (double)((float)i19 + 0.1F) - Block.blocksList[i20].minY, (double)((float)i18 + f23)));
						}
					}
				}
			}

			if(i14 > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
				this.rainSoundCounter = 0;
				if(d10 > entityLiving2.posY + 1.0D && world3.getPrecipitationHeight(MathHelper.floor_double(entityLiving2.posX), MathHelper.floor_double(entityLiving2.posZ)) > MathHelper.floor_double(entityLiving2.posY)) {
					this.mc.theWorld.playSoundEffect(d8, d10, d12, "ambient.weather.rain", 0.1F, 0.5F);
				} else {
					this.mc.theWorld.playSoundEffect(d8, d10, d12, "ambient.weather.rain", 0.2F, 1.0F);
				}
			}

		}
	}

	protected void renderRainSnow(float f1) {
		float f2 = this.mc.theWorld.getRainStrength(f1);
		if(f2 > 0.0F) {
			this.enableLightmap((double)f1);
			if(this.rainXCoords == null) {
				this.rainXCoords = new float[1024];
				this.rainYCoords = new float[1024];

				for(int i3 = 0; i3 < 32; ++i3) {
					for(int i4 = 0; i4 < 32; ++i4) {
						float f5 = (float)(i4 - 16);
						float f6 = (float)(i3 - 16);
						float f7 = MathHelper.sqrt_float(f5 * f5 + f6 * f6);
						this.rainXCoords[i3 << 5 | i4] = -f6 / f7;
						this.rainYCoords[i3 << 5 | i4] = f5 / f7;
					}
				}
			}

			EntityLiving entityLiving41 = this.mc.renderViewEntity;
			World world42 = this.mc.theWorld;
			int i43 = MathHelper.floor_double(entityLiving41.posX);
			int i44 = MathHelper.floor_double(entityLiving41.posY);
			int i45 = MathHelper.floor_double(entityLiving41.posZ);
			Tessellator tessellator8 = Tessellator.instance;
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.01F);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/environment/snow.png"));
			double d9 = entityLiving41.lastTickPosX + (entityLiving41.posX - entityLiving41.lastTickPosX) * (double)f1;
			double d11 = entityLiving41.lastTickPosY + (entityLiving41.posY - entityLiving41.lastTickPosY) * (double)f1;
			double d13 = entityLiving41.lastTickPosZ + (entityLiving41.posZ - entityLiving41.lastTickPosZ) * (double)f1;
			int i15 = MathHelper.floor_double(d11);
			byte b16 = 5;
			if(GameSettingsValues.fancyGraphics) {
				b16 = 10;
			}

			byte b18 = -1;
			float f19 = (float)this.rendererUpdateCount + f1;
			if(GameSettingsValues.fancyGraphics) {
				b16 = 10;
			}

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			for(int i20 = i45 - b16; i20 <= i45 + b16; ++i20) {
				for(int i21 = i43 - b16; i21 <= i43 + b16; ++i21) {
					int i22 = (i20 - i45 + 16) * 32 + i21 - i43 + 16;
					float f23 = this.rainXCoords[i22] * 0.5F;
					float f24 = this.rainYCoords[i22] * 0.5F;
					BiomeGenBase biomeGenBase25 = world42.getBiomeGenForCoords(i21, i20);
					if(biomeGenBase25.canSpawnLightningBolt() || biomeGenBase25.getEnableSnow()) {
						int i26 = world42.getPrecipitationHeight(i21, i20);
						int i27 = i44 - b16;
						int i28 = i44 + b16;
						if(i27 < i26) {
							i27 = i26;
						}

						if(i28 < i26) {
							i28 = i26;
						}

						float f29 = 1.0F;
						int i30 = i26;
						if(i26 < i15) {
							i30 = i15;
						}

						if(i27 != i28) {
							this.random.setSeed((long)(i21 * i21 * 3121 + i21 * 45238971 ^ i20 * i20 * 418711 + i20 * 13761));
							float f31 = biomeGenBase25.getFloatTemperature();
							float f32;
							double d35;
							
							// in this implementation, getTemperatureAtHeight is not yet implemented. So if the biome main T is > 0.15F it will rain rather than snow.
							
							if(world42.getWorldChunkManager().getTemperatureAtHeight(f31, i26) >= 0.15F) {
								if(b18 != 0) {
									if(b18 >= 0) {
										tessellator8.draw();
									}

									b18 = 0;
									GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/environment/rain.png"));
									tessellator8.startDrawingQuads();
								}

								f32 = ((float)(this.rendererUpdateCount + i21 * i21 * 3121 + i21 * 45238971 + i20 * i20 * 418711 + i20 * 13761 & 31) + f1) / 32.0F * (3.0F + this.random.nextFloat());
								double d33 = (double)((float)i21 + 0.5F) - entityLiving41.posX;
								d35 = (double)((float)i20 + 0.5F) - entityLiving41.posZ;
								float f37 = MathHelper.sqrt_double(d33 * d33 + d35 * d35) / (float)b16;
								float f38 = 1.0F;
								tessellator8.setBrightness(world42.getLightBrightnessForSkyBlocks(i21, i30, i20, 0));
								tessellator8.setColorRGBA_F(f38, f38, f38, ((1.0F - f37 * f37) * 0.5F + 0.5F) * f2);
								tessellator8.setTranslation(-d9 * 1.0D, -d11 * 1.0D, -d13 * 1.0D);
								tessellator8.addVertexWithUV((double)((float)i21 - f23) + 0.5D, (double)i27, (double)((float)i20 - f24) + 0.5D, (double)(0.0F * f29), (double)((float)i27 * f29 / 4.0F + f32 * f29));
								tessellator8.addVertexWithUV((double)((float)i21 + f23) + 0.5D, (double)i27, (double)((float)i20 + f24) + 0.5D, (double)(1.0F * f29), (double)((float)i27 * f29 / 4.0F + f32 * f29));
								tessellator8.addVertexWithUV((double)((float)i21 + f23) + 0.5D, (double)i28, (double)((float)i20 + f24) + 0.5D, (double)(1.0F * f29), (double)((float)i28 * f29 / 4.0F + f32 * f29));
								tessellator8.addVertexWithUV((double)((float)i21 - f23) + 0.5D, (double)i28, (double)((float)i20 - f24) + 0.5D, (double)(0.0F * f29), (double)((float)i28 * f29 / 4.0F + f32 * f29));
								tessellator8.setTranslation(0.0D, 0.0D, 0.0D);
							} else {
								if(b18 != 1) {
									if(b18 >= 0) {
										tessellator8.draw();
									}

									b18 = 1;
									GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine.getTexture("/environment/snow.png"));
									tessellator8.startDrawingQuads();
								}

								f32 = ((float)(this.rendererUpdateCount & 511) + f1) / 512.0F;
								float f46 = this.random.nextFloat() + f19 * 0.01F * (float)this.random.nextGaussian();
								float f34 = this.random.nextFloat() + f19 * (float)this.random.nextGaussian() * 0.001F;
								d35 = (double)((float)i21 + 0.5F) - entityLiving41.posX;
								double d47 = (double)((float)i20 + 0.5F) - entityLiving41.posZ;
								float f39 = MathHelper.sqrt_double(d35 * d35 + d47 * d47) / (float)b16;
								float f40 = 1.0F;
								tessellator8.setBrightness((world42.getLightBrightnessForSkyBlocks(i21, i30, i20, 0) * 3 + 15728880) / 4);
								tessellator8.setColorRGBA_F(f40, f40, f40, ((1.0F - f39 * f39) * 0.3F + 0.5F) * f2);
								tessellator8.setTranslation(-d9 * 1.0D, -d11 * 1.0D, -d13 * 1.0D);
								tessellator8.addVertexWithUV((double)((float)i21 - f23) + 0.5D, (double)i27, (double)((float)i20 - f24) + 0.5D, (double)(0.0F * f29 + f46), (double)((float)i27 * f29 / 4.0F + f32 * f29 + f34));
								tessellator8.addVertexWithUV((double)((float)i21 + f23) + 0.5D, (double)i27, (double)((float)i20 + f24) + 0.5D, (double)(1.0F * f29 + f46), (double)((float)i27 * f29 / 4.0F + f32 * f29 + f34));
								tessellator8.addVertexWithUV((double)((float)i21 + f23) + 0.5D, (double)i28, (double)((float)i20 + f24) + 0.5D, (double)(1.0F * f29 + f46), (double)((float)i28 * f29 / 4.0F + f32 * f29 + f34));
								tessellator8.addVertexWithUV((double)((float)i21 - f23) + 0.5D, (double)i28, (double)((float)i20 - f24) + 0.5D, (double)(0.0F * f29 + f46), (double)((float)i28 * f29 / 4.0F + f32 * f29 + f34));
								tessellator8.setTranslation(0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}

			if(b18 >= 0) {
				tessellator8.draw();
			}

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
			this.disableLightmap((double)f1);
		}
	}

	public void setupOverlayRendering() {
		ScaledResolution scaledResolution1 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, scaledResolution1.scaledWidthD, scaledResolution1.scaledHeightD, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}

	private void updateFogColor(float f1) {
		World world2 = this.mc.theWorld;
		EntityLiving entityLiving3 = this.mc.renderViewEntity;
		float f4 = 1.0F / (float)(4 - GameSettingsValues.renderDistance);
		f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
		Vec3D vec3D5 = world2.getSkyColor(this.mc.renderViewEntity, f1);
		float f6 = (float)vec3D5.xCoord;
		float f7 = (float)vec3D5.yCoord;
		float f8 = (float)vec3D5.zCoord;

		Vec3D vec3D9 = world2.getFogColor(entityLiving3, f1);
		this.fogColorRed = (float)vec3D9.xCoord;
		this.fogColorGreen = (float)vec3D9.yCoord;
		this.fogColorBlue = (float)vec3D9.zCoord;

		float f11;
		if(GameSettingsValues.renderDistance < 2 && !this.mc.theWorld.worldProvider.hasNoSky) {
			
			Vec3D vec3D10 = MathHelper.sin(world2.getCelestialAngleRadians(f1)) > 0.0F ? Vec3D.createVector(-1.0D, 0.0D, 0.0D) : Vec3D.createVector(1.0D, 0.0D, 0.0D);
			f11 = (float)entityLiving3.getLook(f1).dotProduct(vec3D10);
			if(f11 < 0.0F) {
				f11 = 0.0F;
			}

			if(f11 > 0.0F) {
				if(GameRules.hasSunriseSunset) {
					float[] f12 = world2.worldProvider.calcSunriseSunsetColors(world2.getCelestialAngle(f1), f1);
					if(f12 != null) {
						f11 *= f12[3];
						this.fogColorRed = this.fogColorRed * (1.0F - f11) + f12[0] * f11;
						this.fogColorGreen = this.fogColorGreen * (1.0F - f11) + f12[1] * f11;
						this.fogColorBlue = this.fogColorBlue * (1.0F - f11) + f12[2] * f11;
					}
				}
			}
		}

		this.fogColorRed += (f6 - this.fogColorRed) * f4;
		this.fogColorGreen += (f7 - this.fogColorGreen) * f4;
		this.fogColorBlue += (f8 - this.fogColorBlue) * f4;
		
		float f19 = world2.getRainStrength(f1);
		float f20;
		if(f19 > 0.0F) {
			f11 = 1.0F - f19 * 0.5F;
			f20 = 1.0F - f19 * 0.4F;
			this.fogColorRed *= f11;
			this.fogColorGreen *= f11;
			this.fogColorBlue *= f20;
		}

		f11 = world2.getWeightedThunderStrength(f1);
		if(f11 > 0.0F) {
			f20 = 1.0F - f11 * 0.5F;
			this.fogColorRed *= f20;
			this.fogColorGreen *= f20;
			this.fogColorBlue *= f20;
		}

		int i21 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, entityLiving3, f1);
		if(this.cloudFog) {
			Vec3D vec3D13 = world2.drawClouds(f1);
			this.fogColorRed = (float)vec3D13.xCoord;
			this.fogColorGreen = (float)vec3D13.yCoord;
			this.fogColorBlue = (float)vec3D13.zCoord;
		} else if(i21 != 0 && Block.blocksList[i21].blockMaterial == Material.water) {
			if (!GameRules.colouredWater) {
				this.fogColorRed = 0.02F;
				this.fogColorGreen = 0.02F;
				this.fogColorBlue = 0.2F;
			} else {
				Vec3D vec3D = ((BlockFluid)Block.waterStill).colorMultiplierAsVec3D(this.mc.theWorld, (int) entityLiving3.posX, (int) entityLiving3.posY, (int) entityLiving3.posZ);
				this.fogColorRed = (float)vec3D.xCoord * 0.05F;
				this.fogColorGreen = (float)vec3D.yCoord * 0.05F;
				this.fogColorBlue = (float)vec3D.zCoord * 0.05F;
			}
		} else if(i21 != 0 && Block.blocksList[i21].blockMaterial == Material.lava) {
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}

		float f22 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * f1;
		this.fogColorRed *= f22;
		this.fogColorGreen *= f22;
		this.fogColorBlue *= f22;
		double d14 = (entityLiving3.lastTickPosY + (entityLiving3.posY - entityLiving3.lastTickPosY) * (double)f1) * world2.worldProvider.getVoidFogYFactor();
		
		// TODO: Blindness
		/*
		if(entityLiving3.isPotionActive(Potion.blindness)) {
			int i16 = entityLiving3.getActivePotionEffect(Potion.blindness).getDuration();
			if(i16 < 20) {
				d14 *= (double)(1.0F - (float)i16 / 20.0F);
			} else {
				d14 = 0.0D;
			}
		}*/

		if(d14 < 1.0D) {
			if(d14 < 0.0D) {
				d14 = 0.0D;
			}

			d14 *= d14;
			this.fogColorRed = (float)((double)this.fogColorRed * d14);
			this.fogColorGreen = (float)((double)this.fogColorGreen * d14);
			this.fogColorBlue = (float)((double)this.fogColorBlue * d14);
		}
		
		// Night vision
		/*
		if(((EntityPlayer)entityLiving3).divingHelmetOn()) {
			float nVB = 0.5F;
			
			float fNV = 1.0F / this.fogColorRed;
			if(fNV > 1.0F / this.fogColorGreen) fNV = 1.0F / this.fogColorGreen;
			if(fNV > 1.0F / this.fogColorBlue) fNV = 1.0F / this.fogColorBlue;
			
			this.fogColorRed   = this.fogColorRed   * (1.0F - nVB) + this.fogColorRed   * fNV * nVB;
			this.fogColorGreen = this.fogColorGreen * (1.0F - nVB) + this.fogColorGreen * fNV * nVB;
			this.fogColorBlue  = this.fogColorBlue  * (1.0F - nVB) + this.fogColorBlue  * fNV * nVB;
		}
		*/

		if(GameSettingsValues.anaglyph) {
			float f23 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float f17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float f18 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = f23;
			this.fogColorGreen = f17;
			this.fogColorBlue = f18;
		}

		GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	private void setupFog(int i1, float f2) {
		EntityLiving entityLiving3 = this.mc.renderViewEntity;
		boolean z4 = false;
		if(entityLiving3 instanceof EntityPlayer) {
			z4 = ((EntityPlayer)entityLiving3).capabilities.isCreativeMode;
		}

		if(i1 == 999) {
			GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
			GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
			GL11.glFogf(GL11.GL_FOG_START, 0.0F);
			GL11.glFogf(GL11.GL_FOG_END, 8.0F);
			if(GLContext.getCapabilities().GL_NV_fog_distance) {
				GL11.glFogi(34138, 34139);
			}

			GL11.glFogf(GL11.GL_FOG_START, 0.0F);
		} else {
			GL11.glFog(GL11.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
			GL11.glNormal3f(0.0F, -1.0F, 0.0F);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			int i5 = ActiveRenderInfo.getBlockIdAtEntityViewpoint(this.mc.theWorld, entityLiving3, f2);
			float f6;
			
			// TODO: Blindness
			/*
			if(entityLiving3.isPotionActive(Potion.blindness)) {
				f6 = 5.0F;
				int i7 = entityLiving3.getActivePotionEffect(Potion.blindness).getDuration();
				if(i7 < 20) {
					f6 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float)i7 / 20.0F);
				}

				GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
				if(i1 < 0) {
					GL11.glFogf(GL11.GL_FOG_START, 0.0F);
					GL11.glFogf(GL11.GL_FOG_END, f6 * 0.8F);
				} else {
					GL11.glFogf(GL11.GL_FOG_START, f6 * 0.25F);
					GL11.glFogf(GL11.GL_FOG_END, f6);
				}

				if(GLContext.getCapabilities().GL_NV_fog_distance) {
					GL11.glFogi(34138, 34139);
				}
			} else*/ {
				float f8;
				float f9;
				float f12;
				if(this.cloudFog) {
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
					GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
					f6 = 1.0F;
					f12 = 1.0F;
					f8 = 1.0F;
					if(GameSettingsValues.anaglyph) {
						f9 = (f6 * 30.0F + f12 * 59.0F + f8 * 11.0F) / 100.0F;
					}
				} else if(i5 > 0 && Block.blocksList[i5].blockMaterial == Material.water /* && !((EntityPlayer)entityLiving3).divingHelmetOn()*/) {
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
					
					//if(!entityLiving3.isPotionActive(Potion.waterBreathing)) {
						GL11.glFogf(GL11.GL_FOG_DENSITY, 0.1F);
					//} else {
					//	GL11.glFogf(GL11.GL_FOG_DENSITY, 0.05F);
					//}

					f6 = 0.4F;
					f12 = 0.4F;
					f8 = 0.9F;
					if(GameSettingsValues.anaglyph) {
						f9 = (f6 * 30.0F + f12 * 59.0F + f8 * 11.0F) / 100.0F;
					}
				} else if(i5 > 0 && Block.blocksList[i5].blockMaterial == Material.lava) {
					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_EXP);
					GL11.glFogf(GL11.GL_FOG_DENSITY, 2.0F);
					f6 = 0.4F;
					f12 = 0.3F;
					f8 = 0.3F;
					if(GameSettingsValues.anaglyph) {
						f9 = (f6 * 30.0F + f12 * 59.0F + f8 * 11.0F) / 100.0F;
					}
				} else {
					f6 = this.farPlaneDistance;
					if(this.mc.theWorld.worldProvider.getWorldHasNoSky() && !z4) {
						double d13 = (double)((entityLiving3.getBrightnessForRender(f2) & 15728640) >> 20) / 16.0D + (entityLiving3.lastTickPosY + (entityLiving3.posY - entityLiving3.lastTickPosY) * (double)f2 + 4.0D) / 32.0D;
						if(d13 < 1.0D) {
							if(d13 < 0.0D) {
								d13 = 0.0D;
							}

							d13 *= d13;
							f9 = 100.0F * (float)d13;
							if(f9 < 5.0F) {
								f9 = 5.0F;
							}

							if(f6 > f9) {
								f6 = f9;
							}
						}
					}
					

					GL11.glFogi(GL11.GL_FOG_MODE, GL11.GL_LINEAR);
					if(i1 < 0) {
						GL11.glFogf(GL11.GL_FOG_START, 0.0F);
						GL11.glFogf(GL11.GL_FOG_END, f6 * 0.8F);
					} else {
						GL11.glFogf(GL11.GL_FOG_START, f6 * 0.25F);
						GL11.glFogf(GL11.GL_FOG_END, f6);
					}

					if(GLContext.getCapabilities().GL_NV_fog_distance) {
						GL11.glFogi(34138, 34139);
					}

					if(this.mc.theWorld.worldProvider.func_48218_b((int)entityLiving3.posX, (int)entityLiving3.posZ)) {
						GL11.glFogf(GL11.GL_FOG_START, f6 * 0.05F);
						GL11.glFogf(GL11.GL_FOG_END, Math.min(f6, 192.0F) * 0.5F);
					}
				}
			}

			GL11.glEnable(GL11.GL_COLOR_MATERIAL);
			GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT);
		}
	}

	private FloatBuffer setFogColorBuffer(float f1, float f2, float f3, float f4) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(f1).put(f2).put(f3).put(f4);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}
}
