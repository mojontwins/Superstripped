package net.minecraft.src;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

public class ConsoleCommandHandler {
	private static Logger minecraftLogger = Logger.getLogger("Minecraft");
	private MinecraftServer minecraftServer;

	public ConsoleCommandHandler(MinecraftServer minecraftServer1) {
		this.minecraftServer = minecraftServer1;
	}

	public synchronized void handleCommand(ServerCommand serverCommand1) {
		String commandLine = serverCommand1.command;
		String[] tokens = commandLine.split(" ");
		String command = tokens[0];
		String parameters = commandLine.substring(command.length()).trim();
		ICommandListener commandListener = serverCommand1.commandListener;
		String username = commandListener.getUsername();
		ServerConfigurationManager serverConfigurationManager = this.minecraftServer.configManager;
		if(!command.equalsIgnoreCase("help") && !command.equalsIgnoreCase("?")) {
			if(command.equalsIgnoreCase("list")) {
				commandListener.log("Connected players: " + serverConfigurationManager.getPlayerList());
			} else if(command.equalsIgnoreCase("stop")) {
				this.sendNoticeToOps(username, "Stopping the server..");
				this.minecraftServer.initiateShutdown();
			} else {
				int i9;
				WorldServer worldServer;
				if(command.equalsIgnoreCase("save-all")) {
					this.sendNoticeToOps(username, "Forcing save..");
					if(serverConfigurationManager != null) {
						serverConfigurationManager.savePlayerStates();
					}

					for(i9 = 0; i9 < this.minecraftServer.worldMngr.length; ++i9) {
						worldServer = this.minecraftServer.worldMngr[i9];
						boolean z11 = worldServer.levelSaving;
						worldServer.levelSaving = false;
						worldServer.saveWorld(true, (IProgressUpdate)null);
						worldServer.levelSaving = z11;
					}

					this.sendNoticeToOps(username, "Save complete.");
				} else if(command.equalsIgnoreCase("save-off")) {
					this.sendNoticeToOps(username, "Disabling level saving..");

					for(i9 = 0; i9 < this.minecraftServer.worldMngr.length; ++i9) {
						worldServer = this.minecraftServer.worldMngr[i9];
						worldServer.levelSaving = true;
					}
				} else if(command.equalsIgnoreCase("save-on")) {
					this.sendNoticeToOps(username, "Enabling level saving..");

					for(i9 = 0; i9 < this.minecraftServer.worldMngr.length; ++i9) {
						worldServer = this.minecraftServer.worldMngr[i9];
						worldServer.levelSaving = false;
					}
				} else if(command.equalsIgnoreCase("op")) {
					serverConfigurationManager.addOp(parameters);
					this.sendNoticeToOps(username, "Opping " + parameters);
					serverConfigurationManager.sendChatMessageToPlayer(parameters, "\u00a7eYou are now op!");
				} else if(command.equalsIgnoreCase("deop")) {
					serverConfigurationManager.removeOp(parameters);
					serverConfigurationManager.sendChatMessageToPlayer(parameters, "\u00a7eYou are no longer op!");
					this.sendNoticeToOps(username, "De-opping " + parameters);
				} else if(command.equalsIgnoreCase("ban-ip")) {
					serverConfigurationManager.banIP(parameters);
					this.sendNoticeToOps(username, "Banning ip " + parameters);
				} else if(command.equalsIgnoreCase("pardon-ip")) {
					serverConfigurationManager.pardonIP(parameters);
					this.sendNoticeToOps(username, "Pardoning ip " + parameters);
				} else {
					EntityPlayerMP entityPlayerMP18;
					if(command.equalsIgnoreCase("ban")) {
						serverConfigurationManager.banPlayer(parameters);
						this.sendNoticeToOps(username, "Banning " + parameters);
						entityPlayerMP18 = serverConfigurationManager.getPlayerEntity(parameters);
						if(entityPlayerMP18 != null) {
							entityPlayerMP18.playerNetServerHandler.kickPlayer("Banned by admin");
						}
					} else if(command.equalsIgnoreCase("pardon")) {
						serverConfigurationManager.pardonPlayer(parameters);
						this.sendNoticeToOps(username, "Pardoning " + parameters);
					} else {
						String string19;
						int i20;
						if(command.equalsIgnoreCase("kick")) {
							string19 = parameters;
							entityPlayerMP18 = null;

							for(i20 = 0; i20 < serverConfigurationManager.playerEntities.size(); ++i20) {
								EntityPlayerMP entityPlayerMP12 = (EntityPlayerMP)serverConfigurationManager.playerEntities.get(i20);
								if(entityPlayerMP12.username.equalsIgnoreCase(string19)) {
									entityPlayerMP18 = entityPlayerMP12;
								}
							}

							if(entityPlayerMP18 != null) {
								entityPlayerMP18.playerNetServerHandler.kickPlayer("Kicked by admin");
								this.sendNoticeToOps(username, "Kicking " + entityPlayerMP18.username);
							} else {
								commandListener.log("Can\'t find user " + string19 + ". No kick.");
							}
						} else if(command.equalsIgnoreCase("tp")) {
							if(tokens.length == 3) {
								EntityPlayerMP entityPlayerMP21 = serverConfigurationManager.getPlayerEntity(tokens[1]);
								entityPlayerMP18 = serverConfigurationManager.getPlayerEntity(tokens[2]);
								if(entityPlayerMP21 == null) {
									commandListener.log("Can\'t find user " + tokens[1] + ". No tp.");
								} else if(entityPlayerMP18 == null) {
									commandListener.log("Can\'t find user " + tokens[2] + ". No tp.");
								} else if(entityPlayerMP21.dimension != entityPlayerMP18.dimension) {
									commandListener.log("User " + tokens[1] + " and " + tokens[2] + " are in different dimensions. No tp.");
								} else {
									entityPlayerMP21.playerNetServerHandler.teleportTo(entityPlayerMP18.posX, entityPlayerMP18.posY, entityPlayerMP18.posZ, entityPlayerMP18.rotationYaw, entityPlayerMP18.rotationPitch);
									this.sendNoticeToOps(username, "Teleporting " + tokens[1] + " to " + tokens[2] + ".");
								}
							} else {
								commandListener.log("Syntax error, please provide a source and a target.");
							}
						} else if(command.equalsIgnoreCase("give")) {
							if(tokens.length != 3 && tokens.length != 4 && tokens.length != 5) {
								return;
							}

							string19 = tokens[1];
							entityPlayerMP18 = serverConfigurationManager.getPlayerEntity(string19);
							if(entityPlayerMP18 != null) {
								try {
									i20 = Integer.parseInt(tokens[2]);
									if(Item.itemsList[i20] != null) {
										this.sendNoticeToOps(username, "Giving " + entityPlayerMP18.username + " some " + i20);
										int i22 = 1;
										int i13 = 0;
										if(tokens.length > 3) {
											i22 = this.tryParse(tokens[3], 1);
										}

										if(tokens.length > 4) {
											i13 = this.tryParse(tokens[4], 1);
										}

										if(i22 < 1) {
											i22 = 1;
										}

										if(i22 > 64) {
											i22 = 64;
										}

										entityPlayerMP18.dropPlayerItem(new ItemStack(i20, i22, i13));
									} else {
										commandListener.log("There\'s no item with id " + i20);
									}
								} catch (NumberFormatException numberFormatException16) {
									commandListener.log("There\'s no item with id " + tokens[2]);
								}
							} else {
								commandListener.log("Can\'t find user " + string19);
							}
						} else if(command.equalsIgnoreCase("xp")) {
							if(tokens.length != 3) {
								return;
							}

							string19 = tokens[1];
							entityPlayerMP18 = serverConfigurationManager.getPlayerEntity(string19);
							if(entityPlayerMP18 != null) {
								try {
									i20 = Integer.parseInt(tokens[2]);
									i20 = i20 > 5000 ? 5000 : i20;
									this.sendNoticeToOps(username, "Giving " + i20 + " orbs to " + entityPlayerMP18.username);
									entityPlayerMP18.addExperience(i20);
								} catch (NumberFormatException numberFormatException15) {
									commandListener.log("Invalid orb count: " + tokens[2]);
								}
							} else {
								commandListener.log("Can\'t find user " + string19);
							}
						} else if(command.equalsIgnoreCase("gamemode")) {
							String who = username, gameMode = "0";
							if(tokens.length == 2) {
								gameMode = tokens[1];
							} else if(tokens.length == 3) {
								who = tokens[1];
								gameMode = tokens[2];
							} else {
								return;
							}

							entityPlayerMP18 = serverConfigurationManager.getPlayerEntity(who);
							if(entityPlayerMP18 != null) {
								try {
									i20 = Integer.parseInt(gameMode);
									i20 = WorldSettings.validGameType(i20);
									if(entityPlayerMP18.itemInWorldManager.getGameType() != i20) {
										this.sendNoticeToOps(username, "Setting " + entityPlayerMP18.username + " to game mode " + i20);
										entityPlayerMP18.itemInWorldManager.toggleGameType(i20);
										entityPlayerMP18.playerNetServerHandler.sendPacket(new Packet70Bed(3, i20));
									} else {
										this.sendNoticeToOps(username, entityPlayerMP18.username + " already has game mode " + i20);
									}
								} catch (NumberFormatException numberFormatException14) {
									commandListener.log("There\'s no game mode with id " + gameMode);
								}
							} else {
								commandListener.log("Can\'t find user " + who);
							}
						} else if(command.equalsIgnoreCase("time")) {
							if(tokens.length != 3) {
								return;
							}

							string19 = tokens[1];

							try {
								int i23 = Integer.parseInt(tokens[2]);
								WorldServer worldServer24;
								if("add".equalsIgnoreCase(string19)) {
									for(i20 = 0; i20 < this.minecraftServer.worldMngr.length; ++i20) {
										worldServer24 = this.minecraftServer.worldMngr[i20];
										worldServer24.advanceTime(worldServer24.getWorldTime() + (long)i23);
									}

									this.sendNoticeToOps(username, "Added " + i23 + " to time");
								} else if("set".equalsIgnoreCase(string19)) {
									for(i20 = 0; i20 < this.minecraftServer.worldMngr.length; ++i20) {
										worldServer24 = this.minecraftServer.worldMngr[i20];
										worldServer24.advanceTime((long)i23);
									}

									this.sendNoticeToOps(username, "Set time to " + i23);
								} else {
									commandListener.log("Unknown method, use either \"add\" or \"set\"");
								}
							} catch (NumberFormatException numberFormatException17) {
								commandListener.log("Unable to convert time value, " + tokens[2]);
							}
						} else if (command.toLowerCase().startsWith("summon")) {
							worldServer = this.minecraftServer.worldMngr[0];
							boolean spawned = false;
							EntityLiving entity = (EntityLiving) EntityList.createEntityByName(parameters, worldServer);
							System.out.println (">" + entity);
							if (entity != null) {
								EntityPlayerMP playerEntity = serverConfigurationManager.getPlayerEntity(username);
								int x = (int)playerEntity.posX + worldServer.rand.nextInt(8) - 4;
								int y = (int)playerEntity.posY + worldServer.rand.nextInt(4) + 1;
								int z = (int)playerEntity.posZ + worldServer.rand.nextInt(8) - 4;
								commandListener.log ("Attempting to spawn @ " + x + " " + y + " " + z);
								entity.setLocationAndAngles((double)x, (double)y, (double)z, worldServer.rand.nextFloat() * 360.0F, 0.0F);

								// If entity supports levels, set level with metadata
								if(entity instanceof IMobWithLevel) {
									((IMobWithLevel)entity).setLevel(worldServer.rand.nextInt(4));
								}
								
								commandListener.log("Spawned " + parameters + " @ " + x + " " + y + " " + z);
								worldServer.spawnEntityInWorld(entity);
								spawned = true;
							}
							
							if (!spawned) {
								commandListener.log("Could not spawn " + parameters + ".");
							} 
						} else if(command.equalsIgnoreCase("say") && parameters.length() > 0) {
							minecraftLogger.info("[" + username + "] " + parameters);
							serverConfigurationManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7d[Server] " + parameters));
						} else if(command.equalsIgnoreCase("tell")) {
							if(tokens.length >= 3) {
								commandLine = commandLine.substring(commandLine.indexOf(" ")).trim();
								commandLine = commandLine.substring(commandLine.indexOf(" ")).trim();
								minecraftLogger.info("[" + username + "->" + tokens[1] + "] " + commandLine);
								commandLine = "\u00a77" + username + " whispers " + commandLine;
								minecraftLogger.info(commandLine);
								if(!serverConfigurationManager.sendPacketToPlayer(tokens[1], new Packet3Chat(commandLine))) {
									commandListener.log("There\'s no player by that name online.");
								}
							}
						} else if(command.equalsIgnoreCase("whitelist")) {
							this.handleWhitelist(username, commandLine, commandListener);
						} else if(command.equalsIgnoreCase("toggledownfall")) {
							this.minecraftServer.worldMngr[0].commandToggleDownfall();
							commandListener.log("Toggling rain and snow, hold on...");
						} else if(command.equalsIgnoreCase("banlist")) {
							if(tokens.length == 2) {
								if(tokens[1].equals("ips")) {
									commandListener.log("IP Ban list:" + this.s_func_40648_a(this.minecraftServer.getBannedIPsList(), ", "));
								}
							} else {
								commandListener.log("Ban list:" + this.s_func_40648_a(this.minecraftServer.getBannedPlayersList(), ", "));
							}
						} else {
							minecraftLogger.info("Unknown console command. Type \"help\" for help.");
						}
					}
				}
			}
		} else {
			this.printHelp(commandListener);
		}

	}

	private void handleWhitelist(String string1, String string2, ICommandListener iCommandListener3) {
		String[] string4 = string2.split(" ");
		if(string4.length >= 2) {
			String string5 = string4[1].toLowerCase();
			if("on".equals(string5)) {
				this.sendNoticeToOps(string1, "Turned on white-listing");
				this.minecraftServer.propertyManagerObj.setProperty("white-list", true);
			} else if("off".equals(string5)) {
				this.sendNoticeToOps(string1, "Turned off white-listing");
				this.minecraftServer.propertyManagerObj.setProperty("white-list", false);
			} else if("list".equals(string5)) {
				Set<String> set6 = this.minecraftServer.configManager.getWhiteListedIPs();
				String string7 = "";

				String string9;
				for(Iterator<String> iterator8 = set6.iterator(); iterator8.hasNext(); string7 = string7 + string9 + " ") {
					string9 = (String)iterator8.next();
				}

				iCommandListener3.log("White-listed players: " + string7);
			} else {
				String string10;
				if("add".equals(string5) && string4.length == 3) {
					string10 = string4[2].toLowerCase();
					this.minecraftServer.configManager.addToWhiteList(string10);
					this.sendNoticeToOps(string1, "Added " + string10 + " to white-list");
				} else if("remove".equals(string5) && string4.length == 3) {
					string10 = string4[2].toLowerCase();
					this.minecraftServer.configManager.removeFromWhiteList(string10);
					this.sendNoticeToOps(string1, "Removed " + string10 + " from white-list");
				} else if("reload".equals(string5)) {
					this.minecraftServer.configManager.reloadWhiteList();
					this.sendNoticeToOps(string1, "Reloaded white-list from file");
				}
			}

		}
	}

	private void printHelp(ICommandListener iCommandListener1) {
		iCommandListener1.log("To run the server without a gui, start it like this:");
		iCommandListener1.log("   java -Xmx1024M -Xms1024M -jar minecraft_server.jar nogui");
		iCommandListener1.log("Console commands:");
		iCommandListener1.log("   help  or  ?               shows this message");
		iCommandListener1.log("   kick <player>             removes a player from the server");
		iCommandListener1.log("   ban <player>              bans a player from the server");
		iCommandListener1.log("   pardon <player>           pardons a banned player so that they can connect again");
		iCommandListener1.log("   ban-ip <ip>               bans an IP address from the server");
		iCommandListener1.log("   pardon-ip <ip>            pardons a banned IP address so that they can connect again");
		iCommandListener1.log("   op <player>               turns a player into an op");
		iCommandListener1.log("   deop <player>             removes op status from a player");
		iCommandListener1.log("   tp <player1> <player2>    moves one player to the same location as another player");
		iCommandListener1.log("   give <player> <id> [num]  gives a player a resource");
		iCommandListener1.log("   tell <player> <message>   sends a private message to a player");
		iCommandListener1.log("   stop                      gracefully stops the server");
		iCommandListener1.log("   save-all                  forces a server-wide level save");
		iCommandListener1.log("   save-off                  disables terrain saving (useful for backup scripts)");
		iCommandListener1.log("   save-on                   re-enables terrain saving");
		iCommandListener1.log("   list                      lists all currently connected players");
		iCommandListener1.log("   say <message>             broadcasts a message to all players");
		iCommandListener1.log("   time <add|set> <amount>   adds to or sets the world time (0-24000)");
		iCommandListener1.log("   gamemode <player> <mode>  sets player\'s game mode (0 or 1)");
		iCommandListener1.log("   toggledownfall            toggles rain on or off");
		iCommandListener1.log("   xp <player> <amount>      gives the player the amount of xp (0-5000)");
	}

	private void sendNoticeToOps(String string1, String string2) {
		String string3 = string1 + ": " + string2;
		this.minecraftServer.configManager.sendChatMessageToAllOps("\u00a77(" + string3 + ")");
		minecraftLogger.info(string3);
	}

	private int tryParse(String string1, int i2) {
		try {
			return Integer.parseInt(string1);
		} catch (NumberFormatException numberFormatException4) {
			return i2;
		}
	}

	private String s_func_40648_a(String[] string1, String string2) {
		int i3 = string1.length;
		if(0 == i3) {
			return "";
		} else {
			StringBuilder stringBuilder4 = new StringBuilder();
			stringBuilder4.append(string1[0]);

			for(int i5 = 1; i5 < i3; ++i5) {
				stringBuilder4.append(string2).append(string1[i5]);
			}

			return stringBuilder4.toString();
		}
	}
}
