package net.minecraft.src;

import org.lwjgl.opengl.GL11;

public class RenderBiped extends RenderLiving {
	protected ModelBiped modelBipedMain;
	protected float field_40296_d;
	
	protected ModelBiped modelArmorChestplate;
	protected ModelBiped modelArmor;

	public RenderBiped(ModelBiped modelBiped1, float f2) {
		this(modelBiped1, f2, 1.0F);
		this.modelBipedMain = modelBiped1;
		this.modelArmorChestplate = new ModelBiped(1.0F);
		this.modelArmor = new ModelBiped(0.5F);
		this.modelArmorChestplate.aimedBow = this.modelBipedMain.aimedBow;
	}

	public RenderBiped(ModelBiped modelBiped1, float f2, float f3) {
		super(modelBiped1, f2);
		this.modelBipedMain = modelBiped1;
		this.field_40296_d = f3;
	}

	protected void renderEquippedItems(EntityLiving entityLiving1, float f2) {
		super.renderEquippedItems(entityLiving1, f2);
		ItemStack itemStack3 = entityLiving1.getHeldItem();
		if(itemStack3 != null) {
			GL11.glPushMatrix();
			this.modelBipedMain.bipedRightArm.postRender(0.0625F);
			GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
			float f4;
			if(
					itemStack3.itemID < 4096 && Block.blocksList[itemStack3.itemID] != null && 
					RenderBlocks.renderItemIn3d(Block.blocksList[itemStack3.itemID].getRenderType())
				) {
				f4 = 0.5F;
				GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
				f4 *= 0.75F;
				GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
			} else if(itemStack3.itemID == Item.bow.shiftedIndex) {
				f4 = 0.625F;
				GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
				GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else if(Item.itemsList[itemStack3.itemID].isFull3D()) {
				f4 = 0.625F;
				GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
				GL11.glScalef(f4, -f4, f4);
				GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
			} else {
				f4 = 0.375F;
				GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
				GL11.glScalef(f4, f4, f4);
				GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
			}

			this.renderManager.itemRenderer.renderItem(entityLiving1, itemStack3, 0);
			if(itemStack3.getItem().requiresMultipleRenderPasses()) {
				this.renderManager.itemRenderer.renderItem(entityLiving1, itemStack3, 1);
			}

			GL11.glPopMatrix();
		}

	}
	
	protected int shouldRenderPass(EntityLiving entityLiving1, int var2, float f3) {
		if(var2 < 4) {
			
			IInventory inventory = entityLiving1.getIInventory();
			if (inventory != null && inventory instanceof InventoryMob) {
				ItemStack var3 = ((InventoryMob)inventory).getArmorItemInSlot(3 - var2);
	
				if(var3 != null) { 
					Item var4 = var3.getItem(); 
					if(var4 instanceof ItemArmor) {
						ItemArmor var5 = (ItemArmor)var4;
						this.loadTexture(RenderPlayer.getArmorTexture(var3, var5.renderIndex, var2));
						ModelBiped var6 = var2 == 2 ? this.modelArmor : this.modelArmorChestplate;
						var6.bipedHead.showModel = var2 == 0;
						var6.bipedHeadwear.showModel = var2 == 0;
						var6.bipedBody.showModel = var2 == 1 || var2 == 2;
						var6.bipedRightArm.showModel = var2 == 1;
						var6.bipedLeftArm.showModel = var2 == 1;
						var6.bipedRightLeg.showModel = var2 == 2 || var2 == 3;
						var6.bipedLeftLeg.showModel = var2 == 2 || var2 == 3;
						this.setRenderPassModel(var6);
						return 1;
					}
				}
			}
		}
		
		return -1;
	}
}
