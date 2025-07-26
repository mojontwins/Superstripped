package net.minecraft.src;

public class GuiYesNoAlpha extends GuiScreen {
	private GuiDeleteWorldAlpha parentScreen;
	private String message1;
	private String message2;
	private int worldNumber;

	public GuiYesNoAlpha(GuiDeleteWorldAlpha guiScreen1, String string2, String string3, int i4) {
		this.parentScreen = guiScreen1;
		this.message1 = string2;
		this.message2 = string3;
		this.worldNumber = i4;
	}

	public void initGui() {
		this.controlList.add(new GuiSmallButton(0, this.width / 2 - 155 + 0, this.height / 6 + 96, "Yes"));
		this.controlList.add(new GuiSmallButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, "No"));
	}

	protected void actionPerformed(GuiButton button) {
		this.parentScreen.deleteWorld(button.id == 0, this.worldNumber);
	}

	public void drawScreen(int mouseX, int mouseY, float renderPartialTick) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.message1, this.width / 2, 70, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, this.message2, this.width / 2, 90, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, renderPartialTick);
	}
}
