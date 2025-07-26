package net.minecraft.src;

public class VillageDoorInfo {
	public final int posX;
	public final int posY;
	public final int posZ;
	public final int insideDirectionX;
	public final int insideDirectionZ;
	public int lastActivityTimestamp;
	public boolean isDetachedFromVillageFlag = false;
	private int doorOpeningRestrictionCounter = 0;

	public VillageDoorInfo(int i1, int i2, int i3, int i4, int i5, int i6) {
		this.posX = i1;
		this.posY = i2;
		this.posZ = i3;
		this.insideDirectionX = i4;
		this.insideDirectionZ = i5;
		this.lastActivityTimestamp = i6;
	}

	public int getDistanceSquared(int i1, int i2, int i3) {
		int i4 = i1 - this.posX;
		int i5 = i2 - this.posY;
		int i6 = i3 - this.posZ;
		return i4 * i4 + i5 * i5 + i6 * i6;
	}

	public int getInsideDistanceSquare(int i1, int i2, int i3) {
		int i4 = i1 - this.posX - this.insideDirectionX;
		int i5 = i2 - this.posY;
		int i6 = i3 - this.posZ - this.insideDirectionZ;
		return i4 * i4 + i5 * i5 + i6 * i6;
	}

	public int getInsidePosX() {
		return this.posX + this.insideDirectionX;
	}

	public int getInsidePosY() {
		return this.posY;
	}

	public int getInsidePosZ() {
		return this.posZ + this.insideDirectionZ;
	}

	public boolean isInside(int i1, int i2) {
		int i3 = i1 - this.posX;
		int i4 = i2 - this.posZ;
		return i3 * this.insideDirectionX + i4 * this.insideDirectionZ >= 0;
	}

	public void resetDoorOpeningRestrictionCounter() {
		this.doorOpeningRestrictionCounter = 0;
	}

	public void incrementDoorOpeningRestrictionCounter() {
		++this.doorOpeningRestrictionCounter;
	}

	public int getDoorOpeningRestrictionCounter() {
		return this.doorOpeningRestrictionCounter;
	}
}
