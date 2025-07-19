package net.minecraft.src;

public class ServerNBTStorage {
	public String name;
	public String host;
	public String playerCount;
	public String motd;
	public long lag;
	public boolean polled = false;

	public ServerNBTStorage(String string1, String string2) {
		this.name = string1;
		this.host = string2;
	}

	public NBTTagCompound getCompoundTag() {
		NBTTagCompound nBTTagCompound1 = new NBTTagCompound();
		nBTTagCompound1.setString("name", this.name);
		nBTTagCompound1.setString("ip", this.host);
		return nBTTagCompound1;
	}

	public static ServerNBTStorage createServerNBTStorage(NBTTagCompound nBTTagCompound0) {
		return new ServerNBTStorage(nBTTagCompound0.getString("name"), nBTTagCompound0.getString("ip"));
	}
}
