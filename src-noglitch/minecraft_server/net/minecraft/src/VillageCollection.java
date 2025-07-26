package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VillageCollection {
	private World worldObj;
	private final List<ChunkCoordinates> villagerPositionsList = new ArrayList<ChunkCoordinates>();
	private final List<VillageDoorInfo> newDoors = new ArrayList<VillageDoorInfo>();
	private final List<Village> villageList = new ArrayList<Village>();
	private int tickCounter = 0;

	public VillageCollection(World world1) {
		this.worldObj = world1;
	}

	public void addVillagerPosition(int i1, int i2, int i3) {
		if(this.villagerPositionsList.size() <= 64) {
			if(!this.isVillagerPositionPresent(i1, i2, i3)) {
				this.villagerPositionsList.add(new ChunkCoordinates(i1, i2, i3));
			}

		}
	}

	public void tick() {
		++this.tickCounter;
		Iterator<?> iterator1 = this.villageList.iterator();

		while(iterator1.hasNext()) {
			Village village2 = (Village)iterator1.next();
			village2.tick(this.tickCounter);
		}

		this.removeAnnihilatedVillages();
		this.dropOldestVillagerPosition();
		this.addNewDoorsToVillageOrCreateVillage();
	}

	private void removeAnnihilatedVillages() {
		Iterator<?> iterator1 = this.villageList.iterator();

		while(iterator1.hasNext()) {
			Village village2 = (Village)iterator1.next();
			if(village2.isAnnihilated()) {
				iterator1.remove();
			}
		}

	}

	public List<Village> getVillageList() {
		return this.villageList;
	}

	public Village findNearestVillage(int i1, int i2, int i3, int i4) {
		Village village5 = null;
		float f6 = Float.MAX_VALUE;
		Iterator<?> iterator7 = this.villageList.iterator();

		while(iterator7.hasNext()) {
			Village village8 = (Village)iterator7.next();
			float f9 = village8.getCenter().getDistanceSquared(i1, i2, i3);
			if(f9 < f6) {
				int i10 = i4 + village8.getVillageRadius();
				if(f9 <= (float)(i10 * i10)) {
					village5 = village8;
					f6 = f9;
				}
			}
		}

		return village5;
	}

	private void dropOldestVillagerPosition() {
		if(!this.villagerPositionsList.isEmpty()) {
			this.addUnassignedWoodenDoorsAroundToNewDoorsList((ChunkCoordinates)this.villagerPositionsList.remove(0));
		}
	}

	private void addNewDoorsToVillageOrCreateVillage() {
		for(int i1 = 0; i1 < this.newDoors.size(); ++i1) {
			VillageDoorInfo villageDoorInfo2 = (VillageDoorInfo)this.newDoors.get(i1);
			boolean z3 = false;
			Iterator<?> iterator4 = this.villageList.iterator();

			while(iterator4.hasNext()) {
				Village village5 = (Village)iterator4.next();
				int i6 = (int)village5.getCenter().getEuclideanDistanceTo(villageDoorInfo2.posX, villageDoorInfo2.posY, villageDoorInfo2.posZ);
				if(i6 <= 32 + village5.getVillageRadius()) {
					village5.addVillageDoorInfo(villageDoorInfo2);
					z3 = true;
					break;
				}
			}

			if(!z3) {
				Village village7 = new Village(this.worldObj);
				village7.addVillageDoorInfo(villageDoorInfo2);
				this.villageList.add(village7);
			}
		}

		this.newDoors.clear();
	}

	private void addUnassignedWoodenDoorsAroundToNewDoorsList(ChunkCoordinates chunkCoordinates1) {
		byte b2 = 16;
		byte b3 = 4;
		byte b4 = 16;

		for(int i5 = chunkCoordinates1.posX - b2; i5 < chunkCoordinates1.posX + b2; ++i5) {
			for(int i6 = chunkCoordinates1.posY - b3; i6 < chunkCoordinates1.posY + b3; ++i6) {
				for(int i7 = chunkCoordinates1.posZ - b4; i7 < chunkCoordinates1.posZ + b4; ++i7) {
					if(this.isWoodenDoorAt(i5, i6, i7)) {
						VillageDoorInfo villageDoorInfo8 = this.getVillageDoorAt(i5, i6, i7);
						if(villageDoorInfo8 == null) {
							this.addDoorToNewListIfAppropriate(i5, i6, i7);
						} else {
							villageDoorInfo8.lastActivityTimestamp = this.tickCounter;
						}
					}
				}
			}
		}

	}

	private VillageDoorInfo getVillageDoorAt(int i1, int i2, int i3) {
		Iterator<?> iterator4 = this.newDoors.iterator();

		VillageDoorInfo villageDoorInfo5;
		do {
			if(!iterator4.hasNext()) {
				iterator4 = this.villageList.iterator();

				VillageDoorInfo villageDoorInfo6;
				do {
					if(!iterator4.hasNext()) {
						return null;
					}

					Village village7 = (Village)iterator4.next();
					villageDoorInfo6 = village7.getVillageDoorAt(i1, i2, i3);
				} while(villageDoorInfo6 == null);

				return villageDoorInfo6;
			}

			villageDoorInfo5 = (VillageDoorInfo)iterator4.next();
		} while(villageDoorInfo5.posX != i1 || villageDoorInfo5.posZ != i3 || Math.abs(villageDoorInfo5.posY - i2) > 1);

		return villageDoorInfo5;
	}

	private void addDoorToNewListIfAppropriate(int i1, int i2, int i3) {
		int i4 = ((BlockDoor)Block.doorWood).getDoorOrientation(this.worldObj, i1, i2, i3);
		int i5;
		int i6;
		if(i4 != 0 && i4 != 2) {
			i5 = 0;

			for(i6 = -5; i6 < 0; ++i6) {
				if(this.worldObj.canBlockSeeTheSky(i1, i2, i3 + i6)) {
					--i5;
				}
			}

			for(i6 = 1; i6 <= 5; ++i6) {
				if(this.worldObj.canBlockSeeTheSky(i1, i2, i3 + i6)) {
					++i5;
				}
			}

			if(i5 != 0) {
				this.newDoors.add(new VillageDoorInfo(i1, i2, i3, 0, i5 > 0 ? -2 : 2, this.tickCounter));
			}
		} else {
			i5 = 0;

			for(i6 = -5; i6 < 0; ++i6) {
				if(this.worldObj.canBlockSeeTheSky(i1 + i6, i2, i3)) {
					--i5;
				}
			}

			for(i6 = 1; i6 <= 5; ++i6) {
				if(this.worldObj.canBlockSeeTheSky(i1 + i6, i2, i3)) {
					++i5;
				}
			}

			if(i5 != 0) {
				this.newDoors.add(new VillageDoorInfo(i1, i2, i3, i5 > 0 ? -2 : 2, 0, this.tickCounter));
			}
		}

	}

	private boolean isVillagerPositionPresent(int i1, int i2, int i3) {
		Iterator<ChunkCoordinates> iterator4 = this.villagerPositionsList.iterator();

		ChunkCoordinates chunkCoordinates5;
		do {
			if(!iterator4.hasNext()) {
				return false;
			}

			chunkCoordinates5 = (ChunkCoordinates)iterator4.next();
		} while(chunkCoordinates5.posX != i1 || chunkCoordinates5.posY != i2 || chunkCoordinates5.posZ != i3);

		return true;
	}

	private boolean isWoodenDoorAt(int i1, int i2, int i3) {
		int i4 = this.worldObj.getBlockId(i1, i2, i3);
		return i4 == Block.doorWood.blockID;
	}
}
