package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet1Login extends Packet {
	public int protocolVersion;
	public String username;
	public WorldType terrainType;
	public int serverMode;
	public int dimension;
	public byte difficultySetting;
	public byte worldHeight;
	public byte maxPlayers;

	public Packet1Login() {
	}

	public Packet1Login(String string1, int i2) {
		this.username = string1;
		this.protocolVersion = i2;
	}

	public Packet1Login(String string1, int i2, WorldType worldType3, int i4, int i5, byte b6, byte b7, byte b8) {
		this.username = string1;
		this.protocolVersion = i2;
		this.terrainType = worldType3;
		this.dimension = i5;
		this.difficultySetting = b6;
		this.serverMode = i4;
		this.worldHeight = b7; 
		this.maxPlayers = b8;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.protocolVersion = dataInputStream1.readInt();
		this.username = readString(dataInputStream1, 16);
		String string2 = readString(dataInputStream1, 16);
		this.terrainType = WorldType.parseWorldType(string2);
		if(this.terrainType == null) {
			this.terrainType = WorldType.DEFAULT;
		}

		this.serverMode = dataInputStream1.readInt();
		this.dimension = dataInputStream1.readInt();
		this.difficultySetting = dataInputStream1.readByte();
		this.worldHeight = dataInputStream1.readByte();
		this.maxPlayers = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.protocolVersion);
		writeString(this.username, dataOutputStream1);
		if(this.terrainType == null) {
			writeString("", dataOutputStream1);
		} else {
			writeString(this.terrainType.getWorldTypeName(), dataOutputStream1);
		}

		dataOutputStream1.writeInt(this.serverMode);
		dataOutputStream1.writeInt(this.dimension);
		dataOutputStream1.writeByte(this.difficultySetting);
		dataOutputStream1.writeByte(this.worldHeight);
		dataOutputStream1.writeByte(this.maxPlayers);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleLogin(this);
	}

	public int getPacketSize() {
		int i1 = 0;
		if(this.terrainType != null) {
			i1 = this.terrainType.getWorldTypeName().length();
		}

		return 4 + this.username.length() + 4 + 7 + 7 + i1;
	}
}
