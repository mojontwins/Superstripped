package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet91UpdateCommandBlock extends Packet {
	int x, y, z;
	public String command;

	public Packet91UpdateCommandBlock() {
		this.isChunkDataPacket = true;
	}
	
	public Packet91UpdateCommandBlock(int x, int y, int z, String command) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.isChunkDataPacket = true;
		this.command = command;
	}
	
	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		this.x = dataInputStream.readInt();
		this.y = dataInputStream.readShort();
		this.z = dataInputStream.readInt();
		this.command = Packet.readString(dataInputStream, 32767);
	}

	@Override
	public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.writeInt(x);
		dataOutputStream.writeShort(y);
		dataOutputStream.writeInt(z);
		Packet.writeString(this.command, dataOutputStream);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		netHandler.handleUpdateCommandBlock(this);
	}

	@Override
	public int getPacketSize() {
		return 4 + 2 + 4 + 2 + this.command.length();
	}

}
