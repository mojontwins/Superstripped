package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet70Bed extends Packet {
	public static final String[] bedChat = new String[]{"tile.bed.notValid", null, null, "gameMode.changed"};
	public int bedState;
	public int gameMode;

	public Packet70Bed() {
	}

	public Packet70Bed(int i1, int i2) {
		this.bedState = i1;
		this.gameMode = i2;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.bedState = dataInputStream1.readByte();
		this.gameMode = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.bedState);
		dataOutputStream1.writeByte(this.gameMode);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleBed(this);
	}

	public int getPacketSize() {
		return 2;
	}
}
