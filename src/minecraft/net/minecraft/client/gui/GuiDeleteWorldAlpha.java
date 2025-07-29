package net.minecraft.client.gui;

import net.minecraft.world.level.chunk.storage.ISaveFormat;

public class GuiDeleteWorldAlpha extends GuiSelectWorldAlpha {
	public GuiDeleteWorldAlpha(GuiScreen guiScreen1) {
		super(guiScreen1);
		this.screenTitle = "Delete world";
	}

	public void initButtons() {
		this.controlList.add(new GuiButton(6, this.width / 2 - 100, this.height / 6 + 168, "Cancel"));
	}

	public void selectWorld(int i1) {
		try {
			String string2 = this.getDisplayName(i1);
			if(string2 != null) {
				this.mc.displayGuiScreen(new GuiYesNoAlpha(this, "Are you sure you want to delete this world?", "\'" + string2 + "\' will be lost forever!", i1));
			}
		} catch (Exception e) {
			System.out.println ("Non existing - do nothing");
			this.mc.displayGuiScreen(this.parentScreen);
		}

	}

	public void deleteWorld(boolean z1, int i2) {
		if(z1) {
			ISaveFormat iSaveFormat3 = this.mc.getSaveLoader();
			iSaveFormat3.flushCache();
			iSaveFormat3.deleteWorldDirectory(this.getSaveName(i2));
			
		}

		this.mc.displayGuiScreen(this.parentScreen);
	}
}
