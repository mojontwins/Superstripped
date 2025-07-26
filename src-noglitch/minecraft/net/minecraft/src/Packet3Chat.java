package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet3Chat extends Packet {
	public static int field_52010_b = 119;
	public String message;

	public Packet3Chat() {
	}

	public Packet3Chat(String string1) {
		if(string1.length() > field_52010_b) {
			string1 = string1.substring(0, field_52010_b);
		}

		this.message = string1;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.message = readString(dataInputStream1, field_52010_b);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		writeString(this.message, dataOutputStream1);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleChat(this);
	}

	public int getPacketSize() {
		return 2 + this.message.length() * 2;
	}
}
