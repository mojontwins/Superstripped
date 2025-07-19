package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class EntityClientPlayerMP extends EntityPlayerSP {
	public NetClientHandler sendQueue;
	private int inventoryUpdateTickCounter = 0;
	private double oldPosX;
	private double oldMinY;
	private double oldPosY;
	private double oldPosZ;
	private float oldRotationYaw;
	private float oldRotationPitch;
	private boolean wasOnGround = false;
	private boolean shouldStopSneaking = false;
	private boolean wasSneaking = false;
	private int timeSinceMoved = 0;
	private boolean hasSetHealth = false;

	public EntityClientPlayerMP(Minecraft minecraft1, World world2, Session session3, NetClientHandler netClientHandler4) {
		super(minecraft1, world2, session3, 0);
		this.sendQueue = netClientHandler4;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		return false;
	}

	public void heal(int i1) {
	}

	public void onUpdate() {
		if(this.worldObj.blockExists(MathHelper.floor_double(this.posX), 0, MathHelper.floor_double(this.posZ))) {
			super.onUpdate();
			this.sendMotionUpdates();
		}
	}

	public void sendMotionUpdates() {
		if(this.inventoryUpdateTickCounter++ == 20) {
			this.inventoryUpdateTickCounter = 0;
		}

		boolean z1 = this.isSprinting();
		if(z1 != this.wasSneaking) {
			if(z1) {
				this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 4));
			} else {
				this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 5));
			}

			this.wasSneaking = z1;
		}

		boolean z2 = this.isSneaking();
		if(z2 != this.shouldStopSneaking) {
			if(z2) {
				this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 1));
			} else {
				this.sendQueue.addToSendQueue(new Packet19EntityAction(this, 2));
			}

			this.shouldStopSneaking = z2;
		}

		double d3 = this.posX - this.oldPosX;
		double d5 = this.boundingBox.minY - this.oldMinY;
		double d7 = this.posY - this.oldPosY;
		double d9 = this.posZ - this.oldPosZ;
		double d11 = (double)(this.rotationYaw - this.oldRotationYaw);
		double d13 = (double)(this.rotationPitch - this.oldRotationPitch);
		boolean z15 = d5 != 0.0D || d7 != 0.0D || d3 != 0.0D || d9 != 0.0D;
		boolean z16 = d11 != 0.0D || d13 != 0.0D;
		if(this.ridingEntity != null) {
			if(z16) {
				this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.motionX, -999.0D, -999.0D, this.motionZ, this.onGround));
			} else {
				this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.motionX, -999.0D, -999.0D, this.motionZ, this.rotationYaw, this.rotationPitch, this.onGround));
			}

			z15 = false;
		} else if(z15 && z16) {
			this.sendQueue.addToSendQueue(new Packet13PlayerLookMove(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround));
			this.timeSinceMoved = 0;
		} else if(z15) {
			this.sendQueue.addToSendQueue(new Packet11PlayerPosition(this.posX, this.boundingBox.minY, this.posY, this.posZ, this.onGround));
			this.timeSinceMoved = 0;
		} else if(z16) {
			this.sendQueue.addToSendQueue(new Packet12PlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
			this.timeSinceMoved = 0;
		} else {
			this.sendQueue.addToSendQueue(new Packet10Flying(this.onGround));
			if(this.wasOnGround == this.onGround && this.timeSinceMoved <= 200) {
				++this.timeSinceMoved;
			} else {
				this.timeSinceMoved = 0;
			}
		}

		this.wasOnGround = this.onGround;
		if(z15) {
			this.oldPosX = this.posX;
			this.oldMinY = this.boundingBox.minY;
			this.oldPosY = this.posY;
			this.oldPosZ = this.posZ;
		}

		if(z16) {
			this.oldRotationYaw = this.rotationYaw;
			this.oldRotationPitch = this.rotationPitch;
		}

	}

	public EntityItem dropOneItem() {
		this.sendQueue.addToSendQueue(new Packet14BlockDig(4, 0, 0, 0, 0));
		return null;
	}

	protected void joinEntityItemWithWorld(EntityItem entityItem1) {
	}

	public void sendChatMessage(String string1) {
		if(this.mc.ingameGUI.getMessageList().size() == 0 || !((String)this.mc.ingameGUI.getMessageList().get(this.mc.ingameGUI.getMessageList().size() - 1)).equals(string1)) {
			this.mc.ingameGUI.getMessageList().add(string1);
		}

		this.sendQueue.addToSendQueue(new Packet3Chat(string1));
	}

	public void swingItem() {
		super.swingItem();
		this.sendQueue.addToSendQueue(new Packet18Animation(this, 1));
	}

	public void respawnPlayer() {
		this.sendQueue.addToSendQueue(new Packet9Respawn(this.dimension, (byte)this.worldObj.difficultySetting, this.worldObj.getWorldInfo().getTerrainType(), this.worldObj.getHeight(), 0));
	}

	protected void damageEntity(DamageSource damageSource1, int i2) {
		this.setEntityHealth(this.getHealth() - i2);
	}

	public void closeScreen() {
		this.sendQueue.addToSendQueue(new Packet101CloseWindow(this.craftingInventory.windowId));
		this.inventory.setItemStack((ItemStack)null);
		super.closeScreen();
	}

	public void setHealth(int i1) {
		if(this.hasSetHealth) {
			super.setHealth(i1);
		} else {
			this.setEntityHealth(i1);
			this.hasSetHealth = true;
		}

	}

	public void func_50009_aI() {
		this.sendQueue.addToSendQueue(new Packet202PlayerAbilities(this.capabilities));
	}
}
