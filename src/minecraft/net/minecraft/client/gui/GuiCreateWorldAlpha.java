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
	
	private GuiButton btnWorldOptions;
	private GuiButton btnGameRules;
	
	private GuiButton btnGameMode;
	private GuiButton btnEnableCheats;
	private GuiButton btnWorldType;
	private GuiButton btnEnableSeasons;
	
	private boolean moreOptions = false;
	
	private String worldTypeStrings[] = new String[] {
			"Alpha", "Alpha cold", "Infdev", "Sky", "Ocean"
	};
	
    public GuiCreateWorldAlpha(GuiScreen parentGuiScreen, int slot) {
    	this.parentGuiScreen = parentGuiScreen;
    	this.slot = slot;
    	this.enableCheats = false;
    	this.isCreative = false;
    	this.enableSeasons = true;
    	this.levelName = "World " + slot;
	}
    
    public void updateScreen() {
        textboxWorldName.updateCursorCounter();
        textboxSeed.updateCursorCounter();
    }
    
    public void initGui() {
    	//this.seed = "" + new Random().nextLong();
    	this.controlList.clear();
    	int baseBtn = 160;
    	
    	// PRINCIPAL SCREEN
    	
    	// World name
    	textboxWorldName = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 82, 200, 20);
    	textboxWorldName.setFocused(true);
    	textboxWorldName.setText(this.levelName);
    	textboxWorldName.setMaxStringLength(40);
    	
    	// Button: World options
    	this.controlList.add(this.btnWorldOptions = new GuiButton(50, this.width / 2 - 75, baseBtn+10, 150, 20, "World options"));
    	
    	// Button: Important game rules
    	this.controlList.add(this.btnGameRules = new GuiButton(200, this.width / 2 - 75, baseBtn - 24, 150, 20, "Important game rules!"));
    	
    	// WORLD OPTIONS
    	
    	// World seed
        textboxSeed = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 82, 200, 20);
        textboxSeed.setText(this.seed);
        textboxSeed.drawTextBox();
        
    	this.controlList.add(this.btnGameMode = new GuiButton(100, this.width / 2 - 122, baseBtn - 48, 120, 20, ""));    
    	this.controlList.add(this.btnEnableCheats = new GuiCheckButton(101, this.width / 2 + 2, baseBtn - 48, 120, 20, ""));
        this.controlList.add(this.btnWorldType = new GuiButton(102, this.width / 2 - 122, baseBtn - 24, 120, 20, ""));
        this.controlList.add(this.btnEnableSeasons = new GuiCheckButton(103, this.width / 2 + 2, baseBtn - 24, 120, 20, ""));
        
        this.btnGameMode.drawButton = false;
        this.btnEnableCheats.drawButton = false;
        this.btnWorldType.drawButton = false;
        this.btnEnableSeasons.drawButton = false;
        
        this.updateOptionButtons(); 
        
        this.controlList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, "Create"));
        this.controlList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, "Cancel"));
    }

	protected void keyTyped(char var1, int var2) {
		if(this.textboxWorldName.getIsFocused() && !this.moreOptions) {
			this.textboxWorldName.textboxKeyTyped(var1, var2);
		} else if(this.moreOptions){
			this.textboxSeed.textboxKeyTyped(var1, var2);
		}

		if(var1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

		((GuiButton)this.controlList.get(0)).enabled = this.textboxWorldName.getText().length() > 0;
	}
	
	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		if(!this.moreOptions) {
			this.textboxWorldName.mouseClicked(var1, var2, var3);
		} else {
			this.textboxSeed.mouseClicked(var1, var2, var3);
		}
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
				case 50:
					this.toggleOptions();
					break;
				case 100:
				case 101:
				case 102:
				case 103:
					this.setOptionValueAlpha(var1.id, 0);
					updateOptionButtons();
					break;
				case 200:
					this.mc.displayGuiScreen(new GuiGameRules(this, false));
					break;
			}
		}
	}
	
	private void toggleOptions() {
		this.moreOptions = !this.moreOptions;

		this.btnGameRules.drawButton = !this.moreOptions;
		this.btnEnableCheats.drawButton = this.moreOptions;
		this.btnEnableSeasons.drawButton = this.moreOptions;
		this.btnGameMode.drawButton = this.moreOptions;
		this.btnWorldType.drawButton = this.moreOptions;

		if(this.moreOptions) {
			this.btnWorldOptions.displayString = "Done!";
		} else {
			this.btnWorldOptions.displayString = "World options";
		}
	}
	
	private String sanitizedWorldName() {
		if ("".equals(this.textboxWorldName.getText())) return "World " + this.slot;
		return this.textboxWorldName.getText().trim();
	}
	
	public void updateOptionButtons() {
		this.btnGameMode.displayString = "Game mode: " + (this.isCreative ? "Creative" : "Survival");
		this.btnWorldType.displayString = "World type: " + this.worldTypeStrings[this.worldTypeId];
		this.btnEnableCheats.displayString = "Enable cheats";
		this.btnEnableCheats.forcedOn = this.enableCheats;
		this.btnEnableSeasons.displayString = "Enable seasons";
		this.btnEnableSeasons.forcedOn = this.enableSeasons;
	}
	
	public void setOptionValueAlpha(int i1, int i2) {
		if (i1 == 100) {
			this.isCreative = !this.isCreative;
			if(this.isCreative) {
				this.enableCheats = true;
				this.btnEnableCheats.enabled = false;
			} else {
				this.btnEnableCheats.enabled = true;
			}
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
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 40, 16777215);
		
    	if(!this.moreOptions) {
    		this.drawString(this.fontRenderer, "World name:", this.width / 2 - 100, 70, 0xCCCCCC);
        	this.textboxWorldName.drawTextBox();
        	this.drawString(this.fontRenderer, "Will be saved in: " + "saves/World" + this.slot, this.width / 2 - 100, 108, 10526880);
			
    	} else {
    		this.drawString(this.fontRenderer, "Seed:", this.width / 2 - 100, 70, 0xCCCCCC);
        	this.textboxSeed.drawTextBox();
    	}
    	
		super.drawScreen(i, j, f);
	}    
	
	
}
