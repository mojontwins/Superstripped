package net.minecraft.src;

public class RConConsoleSource implements ICommandListener {
	public static final RConConsoleSource instance = new RConConsoleSource();
	private StringBuffer buffer = new StringBuffer();

	public void resetLog() {
		this.buffer.setLength(0);
	}

	public String getLogContents() {
		return this.buffer.toString();
	}

	public void log(String string1) {
		this.buffer.append(string1);
	}

	public String getUsername() {
		return "Rcon";
	}
}
