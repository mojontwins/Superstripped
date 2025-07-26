package net.minecraft.src;

public class GameRuleBoolean extends GameRule {
	private boolean value;
	
	public void setValue(boolean value) {
		this.value = value;
	}
	
	public boolean getValue(boolean value) {
		return this.value;
	}
}
