package net.minecraft.network.packet;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;

public class Packet52MultiBlockChange extends Packet {
	public int xPosition;
	public int zPosition;
	public byte[] encodedData;
	public int size;

	public Packet52MultiBlockChange() {
		this.isChunkDataPacket = true;
	}

	public Packet52MultiBlockChange(int chunkX, int chunkZ, short[] encodedCoords, int size, World world) {
		this.isChunkDataPacket = true;
		this.xPosition = chunkX;
		this.zPosition = chunkZ;
		this.size = size;
		
		// Expected byte size has changed: 2 bytes coord, 2 bytes id, 1 byte meta.
		int byteSize = 5 * size;
		
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);

		try {
			if(size >= 64) {
				System.out.println("You should be here! " + size);
				
			} else {
				// Serialice all data in a byte stream
				ByteArrayOutputStream byteStream = new ByteArrayOutputStream(byteSize);
				DataOutputStream stream = new DataOutputStream(byteStream);

				for(int i = 0; i < size; ++i) {
					
					// Decode coordinates
					int x = encodedCoords[i] >> 12 & 15;
					int z = encodedCoords[i] >> 8 & 15;
					int y = encodedCoords[i] & 255;

					// Write coordinates to the stream
					stream.writeShort(encodedCoords[i]);

					// Get & encode block & meta, write to the stream
					// This has been changed: first 16 bit ID, then 8 bit Meta
					stream.writeShort((short)chunk.getBlockID(x, y, z) & 4095);
					stream.writeByte((byte)chunk.getBlockMetadata(x, y, z) & 255);
				}

				this.encodedData = byteStream.toByteArray();
				if(this.encodedData.length != byteSize) {
					throw new RuntimeException("Expected length " + byteSize + " doesn\'t match received length " + this.encodedData.length);
				}
			}
		} catch (IOException e) {
			System.err.println(e.getMessage());
			this.encodedData = null;
		}

	}

	public void readPacketData(DataInputStream dis) throws IOException {
		// Read all data from the packet byte stream
		this.xPosition = dis.readInt();
		this.zPosition = dis.readInt();
		
		// This is size in "block change items" (5 byte each)
		this.size = dis.readShort() & 65535;
		
		// Real byte size will be 5 * size.
		int byteSize = dis.readInt();
		if(byteSize > 0) {
			this.encodedData = new byte[byteSize];
			dis.readFully(this.encodedData);
		}

	}

	public void writePacketData(DataOutputStream dos) throws IOException {
		// Write all data to the packet byte stream
		dos.writeInt(this.xPosition);
		dos.writeInt(this.zPosition);
		dos.writeShort((short)this.size);
		
		if(this.encodedData != null) {
			// This will be 5*size.
			dos.writeInt(this.encodedData.length);
			
			// Now the data
			dos.write(this.encodedData);
		} else {
			dos.writeInt(0);
		}

	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleMultiBlockChange(this);
	}

	public int getPacketSize() {
		// Changed: 10 bytes header plus 5 * size.
		return 10 + this.size * 5;
	}
}
