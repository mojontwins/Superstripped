package net.minecraft.src;

import java.util.Collections;
import java.util.List;

public class GuiSelectWorldAlpha extends GuiScreen {
	protected GuiScreen parentScreen;
	protected String screenTitle = "Select world";
	private boolean selected = false;
	private boolean worldExists[] = new boolean[10];
	private int whichWorld[] = new int[10];
	private List<SaveFormatComparator> saveList;

	public GuiSelectWorldAlpha(GuiScreen guiScreen1) {
		this.parentScreen = guiScreen1;
	}

	public void initGui() {
		this.loadSaves();
		
		for(int i2 = 0; i2 < 5; ++i2) {
			boolean empty = true;
			if(this.saveList != null) {
				for(int j = 0; j < this.saveList.size(); j ++) {
					SaveFormatComparator save = this.saveList.get(j);
					if(save.getFileName().equals("World" + (i2 + 1))) {
						String name = save.getDisplayName();
						long size = save.getSizeOnDisk();
						name += " (" + (float)(size / 1024L * 100L / 1024L) / 100.0F + " MB)";
						this.controlList.add(new GuiButton(i2, this.width / 2 - 100, this.height / 6 + 24 * i2, name));
						worldExists[i2] = true;
						whichWorld[i2] = j;
						empty = false;
					}
				}
			}
			
			if (empty) {
				this.controlList.add(new GuiButton(i2, this.width / 2 - 100, this.height / 6 + 24 * i2, "- empty -"));
				worldExists[i2] = false;
			}
		}

		this.initButtons();
	}
	
	private void loadSaves() {
		ISaveFormat iSaveFormat1 = this.mc.getSaveLoader();
		this.saveList = iSaveFormat1.getSaveList();
		Collections.sort(this.saveList);
	}

	protected String getSaveName(int i1) {
		/*
		File file2 = Minecraft.getMinecraftDir();
		return World.getLevelData(file2, "World" + i1) != null ? "World" + i1 : null;
		*/
		return this.worldExists[i1 - 1] ? "World" + i1 : null; 
	}
	
	protected String getDisplayName(int i1) {
		return this.saveList.get(this.whichWorld[i1 - 1]).getDisplayName();
	}
	
	protected String getSaveName(int i1, String name) {
		if(name == null || MathHelper.stringNullOrLengthZero(name)) {
			StringTranslate stringTranslate3 = StringTranslate.getInstance();
			name = stringTranslate3.translateKey("selectWorld.world") + " " + (i1 + 1);
		}

		return name;
	}

	public void initButtons() {
		this.controlList.add(new GuiButton(5, this.width / 2 - 100, this.height / 6 + 120 + 12, "Delete world..."));
		this.controlList.add(new GuiButton(6, this.width / 2 - 100, this.height / 6 + 168, "Cancel"));
	}

	protected void actionPerformed(GuiButton button) {
		if(button.enabled) {
			if(button.id < 5) {
				this.selectWorld(button.id + 1);
			} else if(button.id == 5) {
				this.mc.displayGuiScreen(new GuiDeleteWorldAlpha(this));
			} else if(button.id == 6) {
				this.mc.displayGuiScreen(this.parentScreen);
			}

		}
	}

	public void selectWorld(int i1) {
		if (worldExists[i1-1]) {
			this.mc.displayGuiScreen((GuiScreen)null);
			if(!this.selected) {
				this.selected = true;
				int i2 = ((SaveFormatComparator)this.saveList.get(i1-1)).getGameType();
				if(i2 == 0) {
					this.mc.playerController = new PlayerControllerSP(this.mc);
				} else {
					this.mc.playerController = new PlayerControllerCreative(this.mc);
				}
				SaveFormatComparator save = this.saveList.get(this.whichWorld[i1-1]);
				String displayName = save.getDisplayName();
				String saveName = save.getFileName();
				this.mc.startWorld(saveName, displayName, (WorldSettings) null);
				this.mc.displayGuiScreen((GuiScreen)null);
			}
		} else {
			this.mc.displayGuiScreen(new GuiCreateWorldAlpha(this, i1));
		}
	}

	public void drawScreen(int mouseX, int mouseY, float renderPartialTick) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 0xFFFFFF);
		super.drawScreen(mouseX, mouseY, renderPartialTick);
	}
}
