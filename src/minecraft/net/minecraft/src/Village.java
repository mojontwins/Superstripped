package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Village {
	private final World worldObj;
	private final List<VillageDoorInfo> villageDoorInfoList = new ArrayList<VillageDoorInfo>();
	private final ChunkCoordinates centerHelper = new ChunkCoordinates(0, 0, 0);
	private final ChunkCoordinates center = new ChunkCoordinates(0, 0, 0);
	private int villageRadius = 0;
	private int lastAddDoorTimestamp = 0;
	private int tickCounter = 0;
	private int numVillagers = 0;
	private List<VillageAgressor> villageAgressors = new ArrayList<VillageAgressor>();
	private int numIronGolems = 0;

	public Village(World world1) {
		this.worldObj = world1;
	}

	public void tick(int i1) {
		this.tickCounter = i1;
		this.removeDeadAndOutOfRangeDoors();
		this.removeDeadAndOldAgressors();
		if(i1 % 20 == 0) {
			this.updateNumVillagers();
		}

		if(i1 % 30 == 0) {
			this.updateNumIronGolems();
		}

		int i2 = this.numVillagers / 16;
		if(this.numIronGolems < i2 && this.villageDoorInfoList.size() > 20 && this.worldObj.rand.nextInt(7000) == 0) {
			Vec3D vec3D3 = this.tryGetIronGolemSpawningLocation(MathHelper.floor_float((float)this.center.posX), MathHelper.floor_float((float)this.center.posY), MathHelper.floor_float((float)this.center.posZ), 2, 4, 2);
			if(vec3D3 != null) {
				EntityIronGolem entityIronGolem4 = new EntityIronGolem(this.worldObj);
				entityIronGolem4.setPosition(vec3D3.xCoord, vec3D3.yCoord, vec3D3.zCoord);
				this.worldObj.spawnEntityInWorld(entityIronGolem4);
				++this.numIronGolems;
			}
		}

	}

	private Vec3D tryGetIronGolemSpawningLocation(int i1, int i2, int i3, int i4, int i5, int i6) {
		for(int i7 = 0; i7 < 10; ++i7) {
			int i8 = i1 + this.worldObj.rand.nextInt(16) - 8;
			int i9 = i2 + this.worldObj.rand.nextInt(6) - 3;
			int i10 = i3 + this.worldObj.rand.nextInt(16) - 8;
			if(this.isInRange(i8, i9, i10) && this.isValidIronGolemSpawningLocation(i8, i9, i10, i4, i5, i6)) {
				return Vec3D.createVector((double)i8, (double)i9, (double)i10);
			}
		}

		return null;
	}

	private boolean isValidIronGolemSpawningLocation(int i1, int i2, int i3, int i4, int i5, int i6) {
		if(!this.worldObj.isBlockNormalCube(i1, i2 - 1, i3)) {
			return false;
		} else {
			int i7 = i1 - i4 / 2;
			int i8 = i3 - i6 / 2;

			for(int i9 = i7; i9 < i7 + i4; ++i9) {
				for(int i10 = i2; i10 < i2 + i5; ++i10) {
					for(int i11 = i8; i11 < i8 + i6; ++i11) {
						if(this.worldObj.isBlockNormalCube(i9, i10, i11)) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	private void updateNumIronGolems() {
		List<Entity> list1 = this.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, AxisAlignedBB.getBoundingBoxFromPool((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
		this.numIronGolems = list1.size();
	}

	private void updateNumVillagers() {
		List<Entity> list1 = this.worldObj.getEntitiesWithinAABB(EntityVillager.class, AxisAlignedBB.getBoundingBoxFromPool((double)(this.center.posX - this.villageRadius), (double)(this.center.posY - 4), (double)(this.center.posZ - this.villageRadius), (double)(this.center.posX + this.villageRadius), (double)(this.center.posY + 4), (double)(this.center.posZ + this.villageRadius)));
		this.numVillagers = list1.size();
	}

	public ChunkCoordinates getCenter() {
		return this.center;
	}

	public int getVillageRadius() {
		return this.villageRadius;
	}

	public int getNumVillageDoors() {
		return this.villageDoorInfoList.size();
	}

	public int getTicksSinceLastDoorAdding() {
		return this.tickCounter - this.lastAddDoorTimestamp;
	}

	public int getNumVillagers() {
		return this.numVillagers;
	}

	public boolean isInRange(int i1, int i2, int i3) {
		return this.center.getDistanceSquared(i1, i2, i3) < (float)(this.villageRadius * this.villageRadius);
	}

	public List<VillageDoorInfo> getVillageDoorInfoList() {
		return this.villageDoorInfoList;
	}

	public VillageDoorInfo findNearestDoor(int i1, int i2, int i3) {
		VillageDoorInfo villageDoorInfo4 = null;
		int i5 = Integer.MAX_VALUE;
		Iterator<VillageDoorInfo> iterator6 = this.villageDoorInfoList.iterator();

		while(iterator6.hasNext()) {
			VillageDoorInfo villageDoorInfo7 = (VillageDoorInfo)iterator6.next();
			int i8 = villageDoorInfo7.getDistanceSquared(i1, i2, i3);
			if(i8 < i5) {
				villageDoorInfo4 = villageDoorInfo7;
				i5 = i8;
			}
		}

		return villageDoorInfo4;
	}

	public VillageDoorInfo findNearestDoorUnrestricted(int i1, int i2, int i3) {
		VillageDoorInfo villageDoorInfo4 = null;
		int i5 = Integer.MAX_VALUE;
		Iterator<VillageDoorInfo> iterator6 = this.villageDoorInfoList.iterator();

		while(iterator6.hasNext()) {
			VillageDoorInfo villageDoorInfo7 = (VillageDoorInfo)iterator6.next();
			int i8 = villageDoorInfo7.getDistanceSquared(i1, i2, i3);
			if(i8 > 256) {
				i8 *= 1000;
			} else {
				i8 = villageDoorInfo7.getDoorOpeningRestrictionCounter();
			}

			if(i8 < i5) {
				villageDoorInfo4 = villageDoorInfo7;
				i5 = i8;
			}
		}

		return villageDoorInfo4;
	}

	public VillageDoorInfo getVillageDoorAt(int i1, int i2, int i3) {
		if(this.center.getDistanceSquared(i1, i2, i3) > (float)(this.villageRadius * this.villageRadius)) {
			return null;
		} else {
			Iterator<VillageDoorInfo> iterator4 = this.villageDoorInfoList.iterator();

			VillageDoorInfo villageDoorInfo5;
			do {
				if(!iterator4.hasNext()) {
					return null;
				}

				villageDoorInfo5 = (VillageDoorInfo)iterator4.next();
			} while(villageDoorInfo5.posX != i1 || villageDoorInfo5.posZ != i3 || Math.abs(villageDoorInfo5.posY - i2) > 1);

			return villageDoorInfo5;
		}
	}

	public void addVillageDoorInfo(VillageDoorInfo villageDoorInfo1) {
		this.villageDoorInfoList.add(villageDoorInfo1);
		this.centerHelper.posX += villageDoorInfo1.posX;
		this.centerHelper.posY += villageDoorInfo1.posY;
		this.centerHelper.posZ += villageDoorInfo1.posZ;
		this.updateVillageRadiusAndCenter();
		this.lastAddDoorTimestamp = villageDoorInfo1.lastActivityTimestamp;
	}

	public boolean isAnnihilated() {
		return this.villageDoorInfoList.isEmpty();
	}

	public void addOrRenewAgressor(EntityLiving entityLiving1) {
		Iterator<VillageAgressor> iterator2 = this.villageAgressors.iterator();

		VillageAgressor villageAgressor3;
		do {
			if(!iterator2.hasNext()) {
				this.villageAgressors.add(new VillageAgressor(this, entityLiving1, this.tickCounter));
				return;
			}

			villageAgressor3 = (VillageAgressor)iterator2.next();
		} while(villageAgressor3.agressor != entityLiving1);

		villageAgressor3.agressionTime = this.tickCounter;
	}

	public EntityLiving findNearestVillageAggressor(EntityLiving entityLiving1) {
		double d2 = Double.MAX_VALUE;
		VillageAgressor villageAgressor4 = null;

		for(int i5 = 0; i5 < this.villageAgressors.size(); ++i5) {
			VillageAgressor villageAgressor6 = (VillageAgressor)this.villageAgressors.get(i5);
			double d7 = villageAgressor6.agressor.getDistanceSqToEntity(entityLiving1);
			if(d7 <= d2) {
				villageAgressor4 = villageAgressor6;
				d2 = d7;
			}
		}

		return villageAgressor4 != null ? villageAgressor4.agressor : null;
	}

	private void removeDeadAndOldAgressors() {
		Iterator<VillageAgressor> iterator1 = this.villageAgressors.iterator();

		while(true) {
			VillageAgressor villageAgressor2;
			do {
				if(!iterator1.hasNext()) {
					return;
				}

				villageAgressor2 = (VillageAgressor)iterator1.next();
			} while(villageAgressor2.agressor.isEntityAlive() && Math.abs(this.tickCounter - villageAgressor2.agressionTime) <= 300);

			iterator1.remove();
		}
	}

	private void removeDeadAndOutOfRangeDoors() {
		boolean z1 = false;
		boolean z2 = this.worldObj.rand.nextInt(50) == 0;
		Iterator<VillageDoorInfo> iterator3 = this.villageDoorInfoList.iterator();

		while(true) {
			VillageDoorInfo villageDoorInfo4;
			do {
				if(!iterator3.hasNext()) {
					if(z1) {
						this.updateVillageRadiusAndCenter();
					}

					return;
				}

				villageDoorInfo4 = (VillageDoorInfo)iterator3.next();
				if(z2) {
					villageDoorInfo4.resetDoorOpeningRestrictionCounter();
				}
			} while(this.isBlockDoor(villageDoorInfo4.posX, villageDoorInfo4.posY, villageDoorInfo4.posZ) && Math.abs(this.tickCounter - villageDoorInfo4.lastActivityTimestamp) <= 1200);

			this.centerHelper.posX -= villageDoorInfo4.posX;
			this.centerHelper.posY -= villageDoorInfo4.posY;
			this.centerHelper.posZ -= villageDoorInfo4.posZ;
			z1 = true;
			villageDoorInfo4.isDetachedFromVillageFlag = true;
			iterator3.remove();
		}
	}

	private boolean isBlockDoor(int i1, int i2, int i3) {
		int i4 = this.worldObj.getBlockId(i1, i2, i3);
		return i4 <= 0 ? false : i4 == Block.doorWood.blockID;
	}

	private void updateVillageRadiusAndCenter() {
		int i1 = this.villageDoorInfoList.size();
		if(i1 == 0) {
			this.center.set(0, 0, 0);
			this.villageRadius = 0;
		} else {
			this.center.set(this.centerHelper.posX / i1, this.centerHelper.posY / i1, this.centerHelper.posZ / i1);
			int i2 = 0;

			VillageDoorInfo villageDoorInfo4;
			for(Iterator<VillageDoorInfo> iterator3 = this.villageDoorInfoList.iterator(); iterator3.hasNext(); i2 = Math.max(villageDoorInfo4.getDistanceSquared(this.center.posX, this.center.posY, this.center.posZ), i2)) {
				villageDoorInfo4 = (VillageDoorInfo)iterator3.next();
			}

			this.villageRadius = Math.max(32, (int)Math.sqrt((double)i2) + 1);
		}
	}
}
