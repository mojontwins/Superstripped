package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class VillageSiege {
	private World theWorld;
	private boolean field_48580_b = false;
	private int field_48581_c = -1;
	private int field_48578_d;
	private int field_48579_e;
	private Village field_48576_f;
	private int field_48577_g;
	private int field_48583_h;
	private int field_48584_i;

	public VillageSiege(World world1) {
		this.theWorld = world1;
	}

	public void tick() {
		boolean z1 = false;
		if(z1) {
			if(this.field_48581_c == 2) {
				this.field_48578_d = 100;
				return;
			}
		} else {
			if(this.theWorld.isDaytime()) {
				this.field_48581_c = 0;
				return;
			}

			if(this.field_48581_c == 2) {
				return;
			}

			if(this.field_48581_c == 0) {
				float f2 = this.theWorld.getCelestialAngle(0.0F);
				if((double)f2 < 0.5D || (double)f2 > 0.501D) {
					return;
				}

				this.field_48581_c = this.theWorld.rand.nextInt(10) == 0 ? 1 : 2;
				this.field_48580_b = false;
				if(this.field_48581_c == 2) {
					return;
				}
			}
		}

		if(!this.field_48580_b) {
			if(!this.func_48574_b()) {
				return;
			}

			this.field_48580_b = true;
		}

		if(this.field_48579_e > 0) {
			--this.field_48579_e;
		} else {
			this.field_48579_e = 2;
			if(this.field_48578_d > 0) {
				this.func_48575_c();
				--this.field_48578_d;
			} else {
				this.field_48581_c = 2;
			}

		}
	}

	private boolean func_48574_b() {
		List<EntityPlayer> list1 = this.theWorld.playerEntities;
		Iterator<EntityPlayer> iterator2 = list1.iterator();

		Vec3D vec3D10;
		do {
			do {
				do {
					do {
						do {
							if(!iterator2.hasNext()) {
								return false;
							}

							EntityPlayer entityPlayer3 = (EntityPlayer)iterator2.next();
							this.field_48576_f = this.theWorld.villageCollectionObj.findNearestVillage((int)entityPlayer3.posX, (int)entityPlayer3.posY, (int)entityPlayer3.posZ, 1);
						} while(this.field_48576_f == null);
					} while(this.field_48576_f.getNumVillageDoors() < 10);
				} while(this.field_48576_f.getTicksSinceLastDoorAdding() < 20);
			} while(this.field_48576_f.getNumVillagers() < 20);

			ChunkCoordinates chunkCoordinates4 = this.field_48576_f.getCenter();
			float f5 = (float)this.field_48576_f.getVillageRadius();
			boolean z6 = false;

			for(int i7 = 0; i7 < 10; ++i7) {
				this.field_48577_g = chunkCoordinates4.posX + (int)((double)(MathHelper.cos(this.theWorld.rand.nextFloat() * (float)Math.PI * 2.0F) * f5) * 0.9D);
				this.field_48583_h = chunkCoordinates4.posY;
				this.field_48584_i = chunkCoordinates4.posZ + (int)((double)(MathHelper.sin(this.theWorld.rand.nextFloat() * (float)Math.PI * 2.0F) * f5) * 0.9D);
				z6 = false;
				Iterator<Village> iterator8 = this.theWorld.villageCollectionObj.getVillageList().iterator();

				while(iterator8.hasNext()) {
					Village village9 = (Village)iterator8.next();
					if(village9 != this.field_48576_f && village9.isInRange(this.field_48577_g, this.field_48583_h, this.field_48584_i)) {
						z6 = true;
						break;
					}
				}

				if(!z6) {
					break;
				}
			}

			if(z6) {
				return false;
			}

			vec3D10 = this.func_48572_a(this.field_48577_g, this.field_48583_h, this.field_48584_i);
		} while(vec3D10 == null);

		this.field_48579_e = 0;
		this.field_48578_d = 20;
		return true;
	}

	private boolean func_48575_c() {
		Vec3D vec3D1 = this.func_48572_a(this.field_48577_g, this.field_48583_h, this.field_48584_i);
		if(vec3D1 == null) {
			return false;
		} else {
			EntityZombie entityZombie2;
			try {
				entityZombie2 = new EntityZombie(this.theWorld);
			} catch (Exception exception4) {
				exception4.printStackTrace();
				return false;
			}

			entityZombie2.setLocationAndAngles(vec3D1.xCoord, vec3D1.yCoord, vec3D1.zCoord, this.theWorld.rand.nextFloat() * 360.0F, 0.0F);
			this.theWorld.spawnEntityInWorld(entityZombie2);
			ChunkCoordinates chunkCoordinates3 = this.field_48576_f.getCenter();
			entityZombie2.setHomeArea(chunkCoordinates3.posX, chunkCoordinates3.posY, chunkCoordinates3.posZ, this.field_48576_f.getVillageRadius());
			return true;
		}
	}

	private Vec3D func_48572_a(int i1, int i2, int i3) {
		for(int i4 = 0; i4 < 10; ++i4) {
			int i5 = i1 + this.theWorld.rand.nextInt(16) - 8;
			int i6 = i2 + this.theWorld.rand.nextInt(6) - 3;
			int i7 = i3 + this.theWorld.rand.nextInt(16) - 8;
			if(this.field_48576_f.isInRange(i5, i6, i7) && SpawnerAnimals.canCreatureTypeSpawnAtLocation(EnumCreatureType.monster, this.theWorld, i5, i6, i7)) {
				return Vec3D.createVector((double)i5, (double)i6, (double)i7);
			}
		}

		return null;
	}
}
