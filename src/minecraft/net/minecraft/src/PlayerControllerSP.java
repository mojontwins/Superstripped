package net.minecraft.src;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;

public class PlayerControllerSP extends PlayerController {
	private int curBlockX = -1;
	private int curBlockY = -1;
	private int curBlockZ = -1;
	private float curBlockDamage = 0.0F;
	private float prevBlockDamage = 0.0F;
	private float blockDestroySoundCounter = 0.0F;
	private int blockHitWait = 0;

	public PlayerControllerSP(Minecraft minecraft1) {
		super(minecraft1);
	}

	public void flipPlayer(EntityPlayer entityPlayer1) {
		entityPlayer1.rotationYaw = -180.0F;
	}

	public boolean shouldDrawHUD() {
		return true;
	}

	// future equivalent : tryHarvestBlock
	public boolean onPlayerDestroyBlock(int i1, int i2, int i3, int i4) {
		int i5 = this.mc.theWorld.getBlockId(i1, i2, i3);
		int i6 = this.mc.theWorld.getBlockMetadata(i1, i2, i3);
		boolean z7 = super.onPlayerDestroyBlock(i1, i2, i3, i4);
		ItemStack itemStack8 = this.mc.thePlayer.getCurrentEquippedItem();
		boolean z9 = this.mc.thePlayer.canHarvestBlock(Block.blocksList[i5], i6);
		if(itemStack8 != null) {
			itemStack8.onDestroyBlock(this.mc.theWorld, i5, i1, i2, i3, this.mc.thePlayer);
			if(itemStack8.stackSize == 0) {
				itemStack8.onItemDestroyedByUse(this.mc.thePlayer);
				this.mc.thePlayer.destroyCurrentEquippedItem();
			}
		}

		if(z7 && z9) {
			Block.blocksList[i5].harvestBlock(this.mc.theWorld, this.mc.thePlayer, i1, i2, i3, i6);
		}

		return z7;
	}

	public void clickBlock(int x, int y, int z, int side) {
		if(this.mc.thePlayer.canPlayerEdit(x, y, z)) {
			this.mc.theWorld.onBlockHit(this.mc.thePlayer, x, y, z, side);
			int blockID = this.mc.theWorld.getBlockId(x, y, z);
			int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);
			if(blockID > 0 && this.curBlockDamage == 0.0F) {
				Block.blocksList[blockID].onBlockClicked(this.mc.theWorld, x, y, z, this.mc.thePlayer);
			}

			if(blockID > 0 && Block.blocksList[blockID].blockStrength(this.mc.thePlayer, metadata) >= 1.0F) {
				this.onPlayerDestroyBlock(x, y, z, side);
			}

		}
	}

	public void resetBlockRemoving() {
		this.curBlockDamage = 0.0F;
		this.blockHitWait = 0;
	}

	public void onPlayerDamageBlock(int x, int y, int z, int side) {
		if(this.blockHitWait > 0) {
			--this.blockHitWait;
		} else {
			if(x == this.curBlockX && y == this.curBlockY && z == this.curBlockZ) {
				int blockID = this.mc.theWorld.getBlockId(x, y, z);
				int metadata = this.mc.theWorld.getBlockMetadata(x, y, z);

				if(!this.mc.thePlayer.canPlayerEdit(x, y, z)) {
					return;
				}

				if(blockID == 0) {
					return;
				}

				Block block = Block.blocksList[blockID];
				this.curBlockDamage += block.blockStrength(this.mc.thePlayer, metadata);
				if(this.blockDestroySoundCounter % 4.0F == 0.0F && block != null) {
					this.mc.sndManager.playSound(block.stepSound.getStepSound(), (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getPitch() * 0.5F);
				}

				++this.blockDestroySoundCounter;
				if(this.curBlockDamage >= 1.0F) {
					this.onPlayerDestroyBlock(x, y, z, side);
					this.curBlockDamage = 0.0F;
					this.prevBlockDamage = 0.0F;
					this.blockDestroySoundCounter = 0.0F;
					this.blockHitWait = 5;
				}
			} else {
				this.curBlockDamage = 0.0F;
				this.prevBlockDamage = 0.0F;
				this.blockDestroySoundCounter = 0.0F;
				this.curBlockX = x;
				this.curBlockY = y;
				this.curBlockZ = z;
			}

		}
	}

	public void setPartialTime(float f1) {
		if(this.curBlockDamage <= 0.0F) {
			this.mc.ingameGUI.damageGuiPartialTime = 0.0F;
			this.mc.renderGlobal.damagePartialTime = 0.0F;
		} else {
			float f2 = this.prevBlockDamage + (this.curBlockDamage - this.prevBlockDamage) * f1;
			this.mc.ingameGUI.damageGuiPartialTime = f2;
			this.mc.renderGlobal.damagePartialTime = f2;
		}

	}

	public float getBlockReachDistance() {
		return 4.0F;
	}

	public void onWorldChange(World world1) {
		super.onWorldChange(world1);
	}

	public EntityPlayer createPlayer(World world1) {
		EntityPlayer entityPlayer2 = super.createPlayer(world1);
		return entityPlayer2;
	}

	public void updateController() {
		this.prevBlockDamage = this.curBlockDamage;
		this.mc.sndManager.playRandomMusicIfReady();
	}

	public boolean onPlayerRightClick(EntityPlayer entityPlayer1, World world2, ItemStack itemStack3, int i4, int i5, int i6, int i7, float xWithinFace, float yWithinFace, float zWithinFace) {
		int i8 = world2.getBlockId(i4, i5, i6);
		return i8 > 0 && 
				// Activation is overriden pressing LSHIFT.
				(Keyboard.getEventKey() != Keyboard.KEY_LSHIFT && Block.blocksList[i8].blockActivated(world2, i4, i5, i6, entityPlayer1, i7, xWithinFace, yWithinFace, zWithinFace)) ?
						true 
					: 
						(itemStack3 == null ? false : itemStack3.useItem(entityPlayer1, world2, i4, i5, i6, i7, xWithinFace, yWithinFace, zWithinFace, Keyboard.getEventKey() == Keyboard.KEY_LCONTROL));
	}

	public boolean func_35642_f() {
		return true;
	}
}
