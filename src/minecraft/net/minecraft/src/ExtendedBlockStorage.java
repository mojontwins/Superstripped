package net.minecraft.src;

public class ExtendedBlockStorage {
	private int yBase;
	private int blockRefCount;
	private int tickRefCount;
	private byte[] blockLSBArray;
	private NibbleArray blockMSBArray;
	private NibbleArray blockMetadataArray;
	private NibbleArray blocklightArray;
	private NibbleArray skylightArray;

	public ExtendedBlockStorage(int i1) {
		this.yBase = i1;
		this.blockLSBArray = new byte[4096];
		this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
		this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
		this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);
	}
	
	public void resetSkyLightArray() {
		this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
	}

	public int getExtBlockID(int i1, int i2, int i3) {
		int i4 = this.blockLSBArray[i2 << 8 | i3 << 4 | i1] & 255;
		return this.blockMSBArray != null ? this.blockMSBArray.get(i1, i2, i3) << 8 | i4 : i4;
	}

	public void setExtBlockID(int i1, int i2, int i3, int i4) {
		int i5 = this.blockLSBArray[i2 << 8 | i3 << 4 | i1] & 255;
		if(this.blockMSBArray != null) {
			i5 |= this.blockMSBArray.get(i1, i2, i3) << 8;
		}

		if(i5 == 0 && i4 != 0) {
			++this.blockRefCount;
			if(Block.blocksList[i4] != null && Block.blocksList[i4].getTickRandomly()) {
				++this.tickRefCount;
			}
		} else if(i5 != 0 && i4 == 0) {
			--this.blockRefCount;
			if(Block.blocksList[i5] != null && Block.blocksList[i5].getTickRandomly()) {
				--this.tickRefCount;
			}
		} else if(Block.blocksList[i5] != null && Block.blocksList[i5].getTickRandomly() && (Block.blocksList[i4] == null || !Block.blocksList[i4].getTickRandomly())) {
			--this.tickRefCount;
		} else if((Block.blocksList[i5] == null || !Block.blocksList[i5].getTickRandomly()) && Block.blocksList[i4] != null && Block.blocksList[i4].getTickRandomly()) {
			++this.tickRefCount;
		}

		this.blockLSBArray[i2 << 8 | i3 << 4 | i1] = (byte)(i4 & 255);
		if(i4 > 255) {
			if(this.blockMSBArray == null) {
				this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
			}

			this.blockMSBArray.set(i1, i2, i3, (i4 & 3840) >> 8);
		} else if(this.blockMSBArray != null) {
			this.blockMSBArray.set(i1, i2, i3, 0);
		}

	}

	public int getExtBlockMetadata(int i1, int i2, int i3) {
		return this.blockMetadataArray.get(i1, i2, i3);
	}

	public void setExtBlockMetadata(int i1, int i2, int i3, int i4) {
		this.blockMetadataArray.set(i1, i2, i3, i4);
	}

	public boolean getIsEmpty() {
		return this.blockRefCount == 0;
	}

	public boolean getNeedsRandomTick() {
		return this.tickRefCount > 0;
	}

	public int getYLocation() {
		return this.yBase;
	}

	public void setExtSkylightValue(int i1, int i2, int i3, int i4) {
		this.skylightArray.set(i1, i2, i3, i4);
	}

	public int getExtSkylightValue(int i1, int i2, int i3) {
		return this.skylightArray.get(i1, i2, i3);
	}

	public void setExtBlocklightValue(int i1, int i2, int i3, int i4) {
		this.blocklightArray.set(i1, i2, i3, i4);
	}

	public int getExtBlocklightValue(int i1, int i2, int i3) {
		return this.blocklightArray.get(i1, i2, i3);
	}

	public void func_48708_d() {
		this.blockRefCount = 0;
		this.tickRefCount = 0;

		for(int i1 = 0; i1 < 16; ++i1) {
			for(int i2 = 0; i2 < 16; ++i2) {
				for(int i3 = 0; i3 < 16; ++i3) {
					int i4 = this.getExtBlockID(i1, i2, i3);
					if(i4 > 0) {
						if(Block.blocksList[i4] == null) {
							this.blockLSBArray[i2 << 8 | i3 << 4 | i1] = 0;
							if(this.blockMSBArray != null) {
								this.blockMSBArray.set(i1, i2, i3, 0);
							}
						} else {
							++this.blockRefCount;
							if(Block.blocksList[i4].getTickRandomly()) {
								++this.tickRefCount;
							}
						}
					}
				}
			}
		}

	}

	public void func_48711_e() {
	}

	public int func_48700_f() {
		return this.blockRefCount;
	}

	public byte[] func_48692_g() {
		return this.blockLSBArray;
	}

	public void func_48715_h() {
		this.blockMSBArray = null;
	}

	public NibbleArray getBlockMSBArray() {
		return this.blockMSBArray;
	}

	public NibbleArray func_48697_j() {
		return this.blockMetadataArray;
	}

	public NibbleArray getBlocklightArray() {
		return this.blocklightArray;
	}

	public NibbleArray getSkylightArray() {
		return this.skylightArray;
	}

	public void setBlockLSBArray(byte[] b1) {
		this.blockLSBArray = b1;
	}

	public void setBlockMSBArray(NibbleArray nibbleArray1) {
		this.blockMSBArray = nibbleArray1;
	}

	public void setBlockMetadataArray(NibbleArray nibbleArray1) {
		this.blockMetadataArray = nibbleArray1;
	}

	public void setBlocklightArray(NibbleArray nibbleArray1) {
		this.blocklightArray = nibbleArray1;
	}

	public void setSkylightArray(NibbleArray nibbleArray1) {
		this.skylightArray = nibbleArray1;
	}

	public NibbleArray createBlockMSBArray() {
		this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
		return this.blockMSBArray;
	}
}
