package net.minecraft.src;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnvilSaveConverter extends SaveFormatOld {
	public AnvilSaveConverter(File file1) {
		super(file1);
	}

	public String getFormatName() {
		return "Anvil";
	}

	public List<SaveFormatComparator> getSaveList() {
		ArrayList<SaveFormatComparator> arrayList1 = new ArrayList<SaveFormatComparator>();
		File[] file2 = this.savesDirectory.listFiles();
		File[] file3 = file2;
		int i4 = file2.length;

		for(int i5 = 0; i5 < i4; ++i5) {
			File file6 = file3[i5];
			if(file6.isDirectory()) {
				String string7 = file6.getName();
				WorldInfo worldInfo8 = this.getWorldInfo(string7);
				if(worldInfo8 != null && (worldInfo8.getSaveVersion() == 19132 || worldInfo8.getSaveVersion() == 19133)) {
					boolean z9 = worldInfo8.getSaveVersion() != this.func_48431_c();
					String string10 = worldInfo8.getWorldName();
					if(string10 == null || MathHelper.stringNullOrLengthZero(string10)) {
						string10 = string7;
					}

					long j11 = 0L;
					arrayList1.add(new SaveFormatComparator(string7, string10, worldInfo8.getLastTimePlayed(), j11, worldInfo8.getGameType(), z9, worldInfo8.isHardcoreModeEnabled()));
				}
			}
		}

		return arrayList1;
	}

	protected int func_48431_c() {
		return 19133;
	}

	public void flushCache() {
		RegionFileCache.clearRegionFileReferences();
	}

	public ISaveHandler getSaveLoader(String string1, boolean z2) {
		return new AnvilSaveHandler(this.savesDirectory, string1, z2);
	}

	public boolean isOldMapFormat(String string1) {
		WorldInfo worldInfo2 = this.getWorldInfo(string1);
		return worldInfo2 != null && worldInfo2.getSaveVersion() != this.func_48431_c();
	}

	public boolean convertMapFormat(String string1, IProgressUpdate iProgressUpdate2) {
		iProgressUpdate2.setLoadingProgress(0);
		ArrayList<File> arrayList3 = new ArrayList<File>();
		ArrayList<File> arrayList4 = new ArrayList<File>();
		ArrayList<File> arrayList5 = new ArrayList<File>();
		File file6 = new File(this.savesDirectory, string1);
		File file7 = new File(file6, "DIM-1");
		File file8 = new File(file6, "DIM1");
		System.out.println("Scanning folders...");
		this.func_48432_a(file6, arrayList3);
		if(file7.exists()) {
			this.func_48432_a(file7, arrayList4);
		}

		if(file8.exists()) {
			this.func_48432_a(file8, arrayList5);
		}

		int i9 = arrayList3.size() + arrayList4.size() + arrayList5.size();
		System.out.println("Total conversion count is " + i9);
		WorldInfo worldInfo10 = this.getWorldInfo(string1);
		Object object11 = null;
		if(worldInfo10.getTerrainType() == WorldType.FLAT) {
			object11 = new WorldChunkManagerHell(BiomeGenBase.alpha, 0.5F, 0.5F);
		} else {
			object11 = new WorldChunkManager(worldInfo10.getSeed(), worldInfo10.getTerrainType());
		}

		this.func_48428_a(new File(file6, "region"), arrayList3, (WorldChunkManager)object11, 0, i9, iProgressUpdate2);
		this.func_48428_a(new File(file7, "region"), arrayList4, new WorldChunkManagerHell(BiomeGenBase.alpha, 1.0F, 0.0F), arrayList3.size(), i9, iProgressUpdate2);
		this.func_48428_a(new File(file8, "region"), arrayList5, new WorldChunkManagerHell(BiomeGenBase.alpha, 0.5F, 0.0F), arrayList3.size() + arrayList4.size(), i9, iProgressUpdate2);
		worldInfo10.setSaveVersion(19133);
		if(worldInfo10.getTerrainType() == WorldType.DEFAULT_1_1) {
			worldInfo10.setTerrainType(WorldType.DEFAULT);
		}

		this.func_48429_d(string1);
		ISaveHandler iSaveHandler12 = this.getSaveLoader(string1, false);
		iSaveHandler12.saveWorldInfo(worldInfo10);
		return true;
	}

	private void func_48429_d(String string1) {
		File file2 = new File(this.savesDirectory, string1);
		if(!file2.exists()) {
			System.out.println("Warning: Unable to create level.dat_mcr backup");
		} else {
			File file3 = new File(file2, "level.dat");
			if(!file3.exists()) {
				System.out.println("Warning: Unable to create level.dat_mcr backup");
			} else {
				File file4 = new File(file2, "level.dat_mcr");
				if(!file3.renameTo(file4)) {
					System.out.println("Warning: Unable to create level.dat_mcr backup");
				}

			}
		}
	}

	private void func_48428_a(File file1, ArrayList<File> arrayList2, WorldChunkManager worldChunkManager3, int i4, int i5, IProgressUpdate iProgressUpdate6) {
		Iterator<File> iterator7 = arrayList2.iterator();

		while(iterator7.hasNext()) {
			File file8 = (File)iterator7.next();
			this.func_48430_a(file1, file8, worldChunkManager3, i4, i5, iProgressUpdate6);
			++i4;
			int i9 = (int)Math.round(100.0D * (double)i4 / (double)i5);
			iProgressUpdate6.setLoadingProgress(i9);
		}

	}

	private void func_48430_a(File file1, File file2, WorldChunkManager worldChunkManager3, int i4, int i5, IProgressUpdate iProgressUpdate6) {
		try {
			String string7 = file2.getName();
			RegionFile regionFile8 = new RegionFile(file2);
			RegionFile regionFile9 = new RegionFile(new File(file1, string7.substring(0, string7.length() - ".mcr".length()) + ".mca"));

			for(int i10 = 0; i10 < 32; ++i10) {
				int i11;
				for(i11 = 0; i11 < 32; ++i11) {
					if(regionFile8.isChunkSaved(i10, i11) && !regionFile9.isChunkSaved(i10, i11)) {
						DataInputStream dataInputStream12 = regionFile8.getChunkDataInputStream(i10, i11);
						if(dataInputStream12 == null) {
							System.out.println("Failed to fetch input stream");
						} else {
							NBTTagCompound compoundTag3 = CompressedStreamTools.read((DataInput)dataInputStream12);
							dataInputStream12.close();
							NBTTagCompound compoundTag4 = compoundTag3.getCompoundTag("Level");
							AnvilConverterData anvilConverterData15 = ChunkLoader.load(compoundTag4);
							NBTTagCompound compoundTag6 = new NBTTagCompound();
							NBTTagCompound compoundTag7 = new NBTTagCompound();
							compoundTag6.setTag("Level", compoundTag7);
							ChunkLoader.convertToAnvilFormat(anvilConverterData15, compoundTag7, worldChunkManager3);
							DataOutputStream dataOutputStream18 = regionFile9.getChunkDataOutputStream(i10, i11);
							CompressedStreamTools.write(compoundTag6, (DataOutput)dataOutputStream18);
							dataOutputStream18.close();
						}
					}
				}

				i11 = (int)Math.round(100.0D * (double)(i4 * 1024) / (double)(i5 * 1024));
				int i20 = (int)Math.round(100.0D * (double)((i10 + 1) * 32 + i4 * 1024) / (double)(i5 * 1024));
				if(i20 > i11) {
					iProgressUpdate6.setLoadingProgress(i20);
				}
			}

			regionFile8.close();
			regionFile9.close();
		} catch (IOException iOException19) {
			iOException19.printStackTrace();
		}

	}

	private void func_48432_a(File file1, ArrayList<File> arrayList2) {
		File file3 = new File(file1, "region");
		File[] file4 = file3.listFiles(new AnvilSaveConverterFileFilter(this));
		if(file4 != null) {
			File[] file5 = file4;
			int i6 = file4.length;

			for(int i7 = 0; i7 < i6; ++i7) {
				File file8 = file5[i7];
				arrayList2.add(file8);
			}
		}

	}
}
