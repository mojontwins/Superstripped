package net.minecraft.src;

import java.util.StringTokenizer;

import net.minecraft.client.Minecraft;

public class SinglePlayerCommands {
	
	/*
	 * Implements a simple command parser launched from the chat console, easily hookable:
	 * 
	 * 1. In Minecraft.java, there's code which prevents the chat console from appearing
	 *    if you are not playing SMP, remove it and change for something else like I did,
	 *    (I have PlayerEntity.enableCheats). Also pass an instance of the Minecraft object
	 *    to GuiChat, for example:
	 *    
	 *    if((this.isMultiplayerWorld() || this.thePlayer.enableCheats) && Keyboard.getEventKey() == this.options.keyBindChat.keyCode) {
	 *        this.displayGuiScreen(new GuiChat(this));
	 *    }
	 *    
	 *    This will make the chat console pop when the bound key is pressed.
	 *    
	 * 2. In GuiChat.java, change the constructor to get the Minecraft instance & store it.
	 *    Also create an instance of this class:
	 * 
	 *    public GuiChat(Minecraft mc) {
	 *          this.mc = mc;
	 *          this.singlePlayerCommands = new SinglePlayerCommands(this.mc);
	 *    }
	 *    
	 * 3. Also in GuiChat.java, add the hook to this code in the keyTyped method when newline
	 *    is detected, running the actual chat if in SMP, or calling this parser:
	 *    
	 *    protected void keyTyped(char character, int key) {
	 *    [...]
	 *    if(key == 28) {
	 *        String string3 = this.message.trim();
	 *        if(string3.length() > 0) {
	 *            if (this.mc.isMultiplayerWorld()) {
	 *                this.mc.thePlayer.sendChatMessage(this.message.trim());
	 *            } else {
	 *                this.singlePlayerCommands.executeCommand(this.message.trim());
	 *            }
	 *        }
	 *        
	 * This code includes a minimal parser which understands gamemode / time set / tp.
	 * It should be easy to adapt it to fit your needs.
	 * 
	 * Enjoy!
	 * 
	 * by na_th_an
	 * Use freely, credit if you wish. Make mods.
	 */
	
	private Minecraft mc;
	
	public SinglePlayerCommands(Minecraft mc) {
		this.mc = mc;
	}
	
	public void executeCommand(String command) {
		StringTokenizer tokenizer = new StringTokenizer(command);
		
		int numTokens = tokenizer.countTokens();
		if (numTokens == 0) return;
		
		String[] tokens = new String [numTokens];
		int idx = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens [idx++] = tokenizer.nextToken();
		}
		
		if (idx > 0) {
			String cmd = tokens [0];
			if ("/gamemode".equals(cmd)) {
				if (idx > 1) {
					String gameMode = tokens [1];
					if ("0".equals(gameMode) || "survival".equals(gameMode)) {
						if (this.mc.playerController.isInCreativeMode()) this.mc.ingameGUI.addChatMessage("Game mode changed to survival");
						mc.playerController = new PlayerControllerSP(mc);
						PlayerControllerCreative.disableAbilities(mc.thePlayer);
						mc.theWorld.worldInfo.setGameType(0);
					} else if ("1".equals(gameMode) || "creative".equals(gameMode)) {
						if (!this.mc.playerController.isInCreativeMode()) this.mc.ingameGUI.addChatMessage("Game mode changed to creative");
						mc.playerController = new PlayerControllerCreative(mc);
						PlayerControllerCreative.enableAbilities(mc.thePlayer);
						mc.thePlayer.setAir(300);
						mc.thePlayer.setHealth(20);
						mc.thePlayer.getFoodStats().setFoodLevel(20);
						mc.thePlayer.getFoodStats().setFoodSaturationLevel(5F);
						mc.theWorld.worldInfo.setGameType(1);
					}
				}
			} else if ("/time".equals(cmd)) {
				if (idx > 2 && "set".equals(tokens [1])) {
					int timeSet = -1;
					if ("night".equals(tokens [2])) {
						timeSet = 14000;
					} else if ("day".equals(tokens [2])) {
						timeSet = 1000;
					} else {
						try {
							timeSet = Integer.parseInt(tokens [2]);
						} catch (Exception e) { }
					}
					long timeBaseDay = this.mc.theWorld.getWorldTime() / 24000L * 24000L;
					long elapsedDay = this.mc.theWorld.getWorldTime() % 24000L;
					if (timeSet > elapsedDay) timeBaseDay += 24000L;
					this.mc.theWorld.setWorldTime(timeBaseDay + timeSet);
					this.mc.ingameGUI.addChatMessage("Time set to " + timeSet);
				}
			} else if ("/tp".equals(cmd)) {
				if (idx > 3) {
					double x = this.mc.thePlayer.posX;
					double y = this.mc.thePlayer.posY;
					double z = this.mc.thePlayer.posZ;
					
					try {
						x = Double.parseDouble(tokens [1]);
					} catch (Exception e) { }
					
					try {
						y = Double.parseDouble(tokens [2]);
					} catch (Exception e) { }
					
					try {
						z = Double.parseDouble(tokens [3]);
					} catch (Exception e) { }
					
					this.mc.thePlayer.setPosition(x, y, z);
					this.mc.ingameGUI.addChatMessage("Teleporting to " + x + " " + y + " " + z);
				}
			} else if ("/summon".equals(cmd)) {
				if (idx > 1) {
					boolean spawned = false;
					EntityLiving entity = (EntityLiving) EntityList.createEntityByName(tokens [1], this.mc.theWorld);
					System.out.println (">" + entity);
					if (entity != null) {
						int x = (int)this.mc.thePlayer.posX + this.mc.theWorld.rand.nextInt(8) - 4;
						int y = (int)this.mc.thePlayer.posY + this.mc.theWorld.rand.nextInt(4) + 1;
						int z = (int)this.mc.thePlayer.posZ + this.mc.theWorld.rand.nextInt(8) - 4;
						System.out.println ("Attempting to spawn @ " + x + " " + y + " " + z);
						entity.setLocationAndAngles((double)x, (double)y, (double)z, this.mc.theWorld.rand.nextFloat() * 360.0F, 0.0F);
												
						SpawnerAnimals.creatureSpecificInit(entity, this.mc.theWorld, x, y, z);
						
						if(idx == 3 && entity instanceof IMobWithLevel) {
							try {
								int level = Integer.parseInt(tokens[2]);
								if(level < ((IMobWithLevel)entity).getMaxLevel()) ((IMobWithLevel)entity).setLevel(level);
							} catch (Exception e) { }
						}
						
						this.mc.ingameGUI.addChatMessage("Spawned " + tokens [1] + " @ " + x + " " + y + " " + z);
						this.mc.theWorld.spawnEntityInWorld(entity);
						spawned = true;
					}
					
					if (!spawned) {
						this.mc.ingameGUI.addChatMessage("Could not spawn " + tokens [1] + ".");
					} 
				}
			} else if ("/rain".equals(cmd)) {
				this.mc.theWorld.worldInfo.setRainTime(20);
			} else if ("/sandstorm".equals(cmd)) {
				this.mc.theWorld.worldInfo.setSandstormingTime(20);
			} else if ("/thunder".equals(cmd)) {
				this.mc.theWorld.worldInfo.setThunderTime(20);
			} else if ("/setDay".equals(cmd)) {


			} else if ("/give".equals(cmd)) {
				// /give id quantity
				if(idx > 2) {
					// Just an id or id:damage ?
					int dotdot = tokens[1].indexOf(':');
					int blockID = 0; 
					int metadata = 0;
					int quantity = 0;
					
					try {
						if(dotdot >= 0) {
							blockID = Integer.parseInt(tokens[1].substring(0, dotdot));
							metadata = Integer.parseInt(tokens[1].substring(dotdot + 1));
						} else {
							blockID = Integer.parseInt(tokens[1]);
						}
						
						quantity = Integer.parseInt(tokens[2]);
											
						float f6 = 0.7F;
						double d7 = (double)(this.mc.theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
						double d9 = (double)(this.mc.theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
						double d11 = (double)(this.mc.theWorld.rand.nextFloat() * f6) + (double)(1.0F - f6) * 0.5D;
						EntityItem entityItem13 = new EntityItem(this.mc.theWorld, this.mc.thePlayer.posX + d7, this.mc.thePlayer.posY + d9, this.mc.thePlayer.posZ + d11, new ItemStack(blockID, quantity, metadata));
						entityItem13.delayBeforeCanPickup = 10;
						this.mc.theWorld.spawnEntityInWorld(entityItem13);
					} catch (Exception e) { }

				}
			}
		}
	}
}
