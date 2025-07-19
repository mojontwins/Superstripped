package net.minecraft.src;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PlayerUsageSnooper {
	private Map<String, Object> field_52025_a = new HashMap<String, Object>();
	private final URL field_52024_b;

	public PlayerUsageSnooper(String string1) {
		try {
			this.field_52024_b = new URL("http://snoop.minecraft.net/" + string1);
		} catch (MalformedURLException malformedURLException3) {
			throw new IllegalArgumentException();
		}
	}

	public void func_52022_a(String string1, Object object2) {
		this.field_52025_a.put(string1, object2);
	}

	public void func_52021_a() {
		PlayerUsageSnooperThread playerUsageSnooperThread1 = new PlayerUsageSnooperThread(this, "reporter");
		playerUsageSnooperThread1.setDaemon(true);
		playerUsageSnooperThread1.start();
	}

	static URL func_52023_a(PlayerUsageSnooper playerUsageSnooper0) {
		return playerUsageSnooper0.field_52024_b;
	}

	static Map<String, Object> func_52020_b(PlayerUsageSnooper playerUsageSnooper0) {
		return playerUsageSnooper0.field_52025_a;
	}
}
