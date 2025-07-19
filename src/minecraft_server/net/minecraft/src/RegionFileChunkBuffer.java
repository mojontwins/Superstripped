package net.minecraft.src;

import java.io.ByteArrayOutputStream;

class RegionFileChunkBuffer extends ByteArrayOutputStream {
	private int chunkX;
	private int chunkZ;
	final RegionFile regionFile;

	public RegionFileChunkBuffer(RegionFile regionFile1, int i2, int i3) {
		super(8096);
		this.regionFile = regionFile1;
		this.chunkX = i2;
		this.chunkZ = i3;
	}

	public void close() {
		this.regionFile.write(this.chunkX, this.chunkZ, this.buf, this.count);
	}
}
