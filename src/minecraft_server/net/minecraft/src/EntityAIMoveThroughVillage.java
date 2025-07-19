package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntityAIMoveThroughVillage extends EntityAIBase {
	private EntityCreature theEntity;
	private float field_48290_b;
	private PathEntity field_48291_c;
	private VillageDoorInfo doorInfo;
	private boolean field_48289_e;
	private List<VillageDoorInfo> doorList = new ArrayList<VillageDoorInfo>();

	public EntityAIMoveThroughVillage(EntityCreature entityCreature1, float f2, boolean z3) {
		this.theEntity = entityCreature1;
		this.field_48290_b = f2;
		this.field_48289_e = z3;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		this.func_48286_h();
		if(this.field_48289_e && this.theEntity.worldObj.isDaytime()) {
			return false;
		} else {
			Village village1 = this.theEntity.worldObj.villageCollectionObj.findNearestVillage(MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ), 0);
			if(village1 == null) {
				return false;
			} else {
				this.doorInfo = this.func_48284_a(village1);
				if(this.doorInfo == null) {
					return false;
				} else {
					boolean z2 = this.theEntity.getNavigator().getCanBreakDoors();
					this.theEntity.getNavigator().setBreakDoors(false);
					this.field_48291_c = this.theEntity.getNavigator().getPathToXYZ((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ);
					this.theEntity.getNavigator().setBreakDoors(z2);
					if(this.field_48291_c != null) {
						return true;
					} else {
						Vec3D vec3D3 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 10, 7, Vec3D.createVector((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ));
						if(vec3D3 == null) {
							return false;
						} else {
							this.theEntity.getNavigator().setBreakDoors(false);
							this.field_48291_c = this.theEntity.getNavigator().getPathToXYZ(vec3D3.xCoord, vec3D3.yCoord, vec3D3.zCoord);
							this.theEntity.getNavigator().setBreakDoors(z2);
							return this.field_48291_c != null;
						}
					}
				}
			}
		}
	}

	public boolean continueExecuting() {
		if(this.theEntity.getNavigator().noPath()) {
			return false;
		} else {
			float f1 = this.theEntity.width + 4.0F;
			return this.theEntity.getDistanceSq((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ) > (double)(f1 * f1);
		}
	}

	public void startExecuting() {
		this.theEntity.getNavigator().setPath(this.field_48291_c, this.field_48290_b);
	}

	public void resetTask() {
		if(this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq((double)this.doorInfo.posX, (double)this.doorInfo.posY, (double)this.doorInfo.posZ) < 16.0D) {
			this.doorList.add(this.doorInfo);
		}

	}

	private VillageDoorInfo func_48284_a(Village village1) {
		VillageDoorInfo villageDoorInfo2 = null;
		int i3 = Integer.MAX_VALUE;
		List<VillageDoorInfo> list4 = village1.getVillageDoorInfoList();
		Iterator<VillageDoorInfo> iterator5 = list4.iterator();

		while(iterator5.hasNext()) {
			VillageDoorInfo villageDoorInfo6 = (VillageDoorInfo)iterator5.next();
			int i7 = villageDoorInfo6.getDistanceSquared(MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ));
			if(i7 < i3 && !this.func_48285_a(villageDoorInfo6)) {
				villageDoorInfo2 = villageDoorInfo6;
				i3 = i7;
			}
		}

		return villageDoorInfo2;
	}

	private boolean func_48285_a(VillageDoorInfo villageDoorInfo1) {
		Iterator<VillageDoorInfo> iterator2 = this.doorList.iterator();

		VillageDoorInfo villageDoorInfo3;
		do {
			if(!iterator2.hasNext()) {
				return false;
			}

			villageDoorInfo3 = (VillageDoorInfo)iterator2.next();
		} while(villageDoorInfo1.posX != villageDoorInfo3.posX || villageDoorInfo1.posY != villageDoorInfo3.posY || villageDoorInfo1.posZ != villageDoorInfo3.posZ);

		return true;
	}

	private void func_48286_h() {
		if(this.doorList.size() > 15) {
			this.doorList.remove(0);
		}

	}
}
