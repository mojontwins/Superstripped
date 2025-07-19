package net.minecraft.src;

class PlayerUsageSnooperThread extends Thread {
	final PlayerUsageSnooper field_52012_a;

	PlayerUsageSnooperThread(PlayerUsageSnooper playerUsageSnooper1, String string2) {
		super(string2);
		this.field_52012_a = playerUsageSnooper1;
	}

	public void run() {
		PostHttp.func_52018_a(PlayerUsageSnooper.func_52023_a(this.field_52012_a), PlayerUsageSnooper.func_52020_b(this.field_52012_a), true);
	}
}
