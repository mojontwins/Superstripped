package net.minecraft.client.gui;

import java.util.Random;

import net.minecraft.client.player.PlayerControllerCreative;
import net.minecraft.client.player.PlayerControllerSP;
import net.minecraft.world.GameRules;
import net.minecraft.world.level.WorldSettings;

public class GuiCreateWorldAlpha extends GuiScreen {
    private GuiScreen parentGuiScreen;
    private GuiTextField textboxWorldName;
    private GuiTextField textboxSeed;
    
    private String seed = "";
    private String levelName;
    
    private int slot; 
    
    protected String screenTitle = "Create new world";
	private boolean selected = false;
	private boolean enableCheats;
	private boolean isCreative;
	private boolean snowCovered;
	private boolean enableSeasons = true;
	private int worldTypeId = 0;
	
	private String worldTypeStrings[] = new String[] {
			"Alpha", "Alpha cold", "Infdev", "Sky", "Ocean"
	};
	
    public GuiCreateWorldAlpha(GuiScreen parentGuiScreen, int slot) {
    	this.parentGuiScreen = parentGuiScreen;
    	this.slot = slot;
    	this.enableCheats = false;
    	this.isCreative = false;
    	this.levelName = "World " + slot;
	}
    
    public void updateScreen() {
        textboxWorldName.updateCursorCounter();
        textboxSeed.updateCursorCounter();
    }
    
    public void initGui() {
    	this.controlList.clear();
    	
    	textboxWorldName = new GuiTextField(this, this.fontRenderer, width / 2 - 144, 50, 140, 20);
    	textboxWorldName.setFocused(true);
    	textboxWorldName.setText(this.levelName);
    	textboxWorldName.setMaxStringLength(16);
    	
        textboxSeed = new GuiTextField(this, this.fontRenderer, width / 2 + 4, 50, 140, 20);
        textboxSeed.setText(this.seed);
        textboxSeed.drawTextBox();
        
        this.controlList.add(new GuiButton(100, this.width / 2 - 104, 80, 208, 20, this.getOptionDisplayStringAlpha(100)));
        this.controlList.add(new GuiButton(101, this.width / 2 - 104, 102, 208, 20, this.getOptionDisplayStringAlpha(101)));
        this.controlList.add(new GuiButton(102, this.width / 2 - 104, 124, 208, 20, this.getOptionDisplayStringAlpha(102)));
        this.controlList.add(new GuiButton(103, this.width / 2 - 104, 146, 208, 20, this.getOptionDisplayStringAlpha(103)));
        this.controlList.add(new GuiButton(200, this.width / 2 - 104, 168, 208, 20, this.getOptionDisplayStringAlpha(200)));
                
        this.controlList.add(new GuiButton(0, this.width / 2 - 104, 198, 102, 20, "Create"));
        this.controlList.add(new GuiButton(1, this.width / 2 + 2, 198, 102, 20, "Cancel"));
    }

	protected void keyTyped(char var1, int var2) {
		if(this.textboxWorldName.getIsFocused()) {
			this.textboxWorldName.textboxKeyTyped(var1, var2);
		} else {
			this.textboxSeed.textboxKeyTyped(var1, var2);
		}
		
		if(var1 == 9) {
			if (this.textboxWorldName.getIsFocused()) {
				this.textboxWorldName.setFocused(false);
				this.textboxSeed.setFocused(true);
			} else if(this.textboxSeed.getIsFocused()) {
				this.textboxSeed.setFocused(false);
				this.textboxWorldName.setFocused(true);
			}
		}

		if(var1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}
	}
	
	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		this.textboxWorldName.mouseClicked(var1, var2, var3);
		this.textboxSeed.mouseClicked(var1, var2, var3);
	}
   
	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			switch (var1.id) {
				case 0:
					if (this.selected == false) {
						this.mc.displayGuiScreen((GuiScreen)null);
						this.selected = true;
						
						long actualSeed = (new Random ()).nextLong ();
						this.seed = this.textboxSeed.getText();
						if (this.seed != null && !"".equals(this.seed)) {
							try {
								long aux = Long.parseLong(this.seed);
								if (aux != 0) actualSeed = aux;
							} catch (Exception e) {
								actualSeed = (long)this.seed.hashCode();
							}
						}
						
						byte gameMode = 0;
						if(this.isCreative) {
							gameMode = 1;
							this.mc.playerController = new PlayerControllerCreative(this.mc);
						} else {
							this.mc.playerController = new PlayerControllerSP(this.mc);
						}
						
						this.mc.startWorld("World" + this.slot, this.sanitizedWorldName(), 
								new WorldSettings(actualSeed, gameMode, this.enableCheats, this.snowCovered, this.enableSeasons, GameRules.defaultWorldType(this.worldTypeId)));
						this.mc.displayGuiScreen((GuiScreen)null);
					}
					break;
				case 1:
					this.mc.displayGuiScreen(this.parentGuiScreen);
					break;
				case 100:
				case 101:
				case 102:
				case 103:
				case 104:
					this.setOptionValueAlpha(var1.id, 0);
					var1.displayString = this.getOptionDisplayStringAlpha(var1.id);
					break;
				case 200:
					this.mc.displayGuiScreen(new GuiGameRules(this, false));
					break;
			}
		}
	}
	
	private String sanitizedWorldName() {
		if ("".equals(this.textboxWorldName.getText())) return "World " + this.slot;
		return this.textboxWorldName.getText().trim();
	}
	
	public String getOptionDisplayStringAlpha(int i) {
		switch (i) {
			case 100: return "Game mode: " + (this.isCreative ? "Creative" : "Survival");
			case 101: return "Enable cheats: " + (this.enableCheats ? "ON" : "OFF");
			case 102: return "Type: " + this.worldTypeStrings[this.worldTypeId];
			case 103: return "Enable seasons: " + (this.enableSeasons ? "ON" : "OFF");
			case 200: return "Important game rules";
		}
		return null;
	}
	
	public void setOptionValueAlpha(int i1, int i2) {
		if (i1 == 100) {
			this.isCreative = !this.isCreative;
		}
		
		if (i1 == 101) {
			this.enableCheats = !this.enableCheats;
		}
		
		if (i1 == 102) {
			this.worldTypeId = (this.worldTypeId + 1) % (this.worldTypeStrings.length);
			this.snowCovered = (this.worldTypeId == 1);
		}
		
		if (i1 == 103) {
			this.enableSeasons = !this.enableSeasons;
		}
	}
    
	public void drawScreen(int i, int j, float f) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 16777215);
		//this.drawCenteredString(this.fontRenderer, "Enter a name for your world and a seed!", this.width/2, 50, 0xEEEEEE);
		//this.drawCenteredString(this.fontRenderer, "If left blank, default values will be used.", this.width/2, 60, 0xEEEEEE);
    	
    	this.drawString(this.fontRenderer, "Name", this.width / 2 - 144, 40, 0xCCCCCC);
    	this.drawString(this.fontRenderer, "Seed:", this.width / 2 + 4, 40, 0xCCCCCC);
    	
    	this.textboxWorldName.drawTextBox();
		this.textboxSeed.drawTextBox();
    	
		super.drawScreen(i, j, f);
	}    
	
	
}
