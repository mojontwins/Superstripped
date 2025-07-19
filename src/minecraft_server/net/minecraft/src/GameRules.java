package net.minecraft.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

public class GameRules {
	// You should be able to change all this from a special menu in options.
	// This menu should be accessible when creating a new world or ingame.

	protected static File optionsFile;
	
	// Fixed (soft locked)
	public static boolean oldSelectWorldScreen = true;
	public static boolean genlayerWorldChunkManager = false;
		
	// Modifiable
	public static boolean connectFences = false; 			// Fences connect to all cube blocks.
	public static boolean generateLapislazuli = true;		// Generate lapislazuli ore.
	public static boolean noiseTreeDensity = true;			// Tree density based on noise.
	public static boolean smarterMobs = true; 				// Use new AI based zombies & skeletons.
	public static boolean enableSquids = true; 				// Spawn squids.
	public static boolean oldFences = false;				// Fences have full block collision.
	public static boolean colouredWater = false; 			// Use T/H from the biome gen to get color from ramp.
	public static boolean edibleChicken = true; 			// Chicken sometimes drop chicken meat.
	public static boolean edibleCows = true; 				// Cows sometimes drop cow meat.
	public static boolean canBreedAnimals = true; 			// You can make animals mate & reproduce
	public static boolean skeletonsWithBows = true;			// Skeletons have visible bows.
	public static boolean classicHurtSound = true; 			// Use the classic "HUH" when hurt.
	public static boolean enableHunger = false; 			// Enable sprinting mechanic.
	public static boolean enableSprinting = false; 			// Enable hunger mechanic.
	public static boolean hasSunriseSunset = false; 		// Enable sunrise / sunset colors.
	public static boolean classicBow = true; 				// Classic shotgun bow
	public static boolean stackableFood = false;			// Food is stackable
	public static boolean colouredFog = false;				// Coloured fog

	public static final boolean debug = true;

	public static void withMcDataDir(File mcDataDir) {
		optionsFile = new File(mcDataDir, "rules.txt");
		System.out.println ("Gamerules file " + optionsFile.toString());
		
		// Create from default if it does not exist or add missing options
		loadRulesFromOptions();
		saveRulesAsOptions();
	}
	
	public static void loadRules(NBTTagCompound nBTWorldInfo) {
		if(nBTWorldInfo.hasKey("gameRules")) {
			NBTTagCompound nBTGameRules = nBTWorldInfo.getCompoundTag("gameRules");
			
			connectFences = nBTGameRules.getBoolean("connectFences");
			generateLapislazuli = nBTGameRules.getBoolean("generateLapislazuli");
			noiseTreeDensity = nBTGameRules.getBoolean("noiseTreeDensity");
			smarterMobs = nBTGameRules.getBoolean("smarterMobs");
			enableSquids = nBTGameRules.getBoolean("enableSquids");
			oldFences = nBTGameRules.getBoolean("oldFences");
			colouredWater = nBTGameRules.getBoolean("colouredWater");
			edibleChicken = nBTGameRules.getBoolean("edibleChicken");
			edibleCows = nBTGameRules.getBoolean("edibleCows");
			canBreedAnimals = nBTGameRules.getBoolean("canBreedAnimals");
			skeletonsWithBows = nBTGameRules.getBoolean("skeletonsWithBows");
			classicHurtSound = nBTGameRules.getBoolean("classicHurtSound");
			enableHunger = nBTGameRules.getBoolean("enableHunger");
			enableSprinting = nBTGameRules.getBoolean("enableSprinting");
			hasSunriseSunset = nBTGameRules.getBoolean("hasSunriseSunset");
			classicBow = nBTGameRules.getBoolean("classicBow");
			stackableFood = nBTGameRules.getBoolean("stackableFood");
			colouredFog = nBTGameRules.getBoolean("colouredFog");
		}
	}

	public static void saveRules(NBTTagCompound nBTWorldInfo) {
		NBTTagCompound nBTGameRules = new NBTTagCompound();
		
		nBTGameRules.setBoolean("connectFences", connectFences);
		nBTGameRules.setBoolean("generateLapislazuli", generateLapislazuli);
		nBTGameRules.setBoolean("noiseTreeDensity", noiseTreeDensity);
		nBTGameRules.setBoolean("smarterMobs", smarterMobs);
		nBTGameRules.setBoolean("enableSquids", enableSquids);
		nBTGameRules.setBoolean("oldFences", oldFences);
		nBTGameRules.setBoolean("colouredWater", colouredWater);
		nBTGameRules.setBoolean("edibleChicken", edibleChicken);
		nBTGameRules.setBoolean("edibleCows", edibleCows);
		nBTGameRules.setBoolean("canBreedAnimals", canBreedAnimals);
		nBTGameRules.setBoolean("skeletonsWithBows", skeletonsWithBows);
		nBTGameRules.setBoolean("classicHurtSound", classicHurtSound);
		nBTGameRules.setBoolean("enableHunger", enableHunger);
		nBTGameRules.setBoolean("enableSprinting", enableSprinting);
		nBTGameRules.setBoolean("hasSunriseSunset", hasSunriseSunset);
		nBTGameRules.setBoolean("classicBow", classicBow);
		nBTGameRules.setBoolean("stackableFood", stackableFood);
		nBTGameRules.setBoolean("colouredFog", colouredFog);
		
		nBTWorldInfo.setCompoundTag("gameRules", nBTGameRules);
	}
	
	public static void saveRulesAsOptions() {
		Properties props = new Properties();
		
		// Get options to an NBT
		NBTTagCompound compound = new NBTTagCompound();
		saveRules(compound);
		NBTTagCompound nBTGameRules = compound.getCompoundTag("gameRules");
		
		// Now convert NBT in Properties
		Collection<NBTBase> ruleNames = nBTGameRules.getTags();
		Iterator<NBTBase> it = ruleNames.iterator();
		while(it.hasNext()) {
			NBTBase tag = it.next();
			String tagName = tag.getName();
			
			if(tag instanceof NBTTagByte) {
				props.setProperty(tagName, nBTGameRules.getBoolean(tagName) ? "true" : "false");
			} else if(tag instanceof NBTTagInt) {
				props.setProperty(tagName, "" + nBTGameRules.getInteger(tagName));
			}
		}
		
		// Save to file
		try {
			props.store(new FileOutputStream(optionsFile), "Minecraft Game Rules");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void loadRulesFromOptions() {
		if(!optionsFile.isFile()) {
			System.out.println ("Generating default Gamerules file");
			return;
		}
		
		// Load from file
		Properties prop = new Properties();
		
		try {
			prop.load(new FileInputStream(optionsFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Generate a NBT from properties
		NBTTagCompound nBTGameRules = new NBTTagCompound();
		
		prop.forEach((k, v) -> {
			if("true".equals(v) || "false".equals(v)) {
				nBTGameRules.setBoolean((String)k, "true".equals(v));
			} else {
				try {
					nBTGameRules.setInteger((String)k, Integer.parseInt((String) v));
				} catch (NumberFormatException nfe) {
					nBTGameRules.setString((String)k, (String)v);
				}
			}
		});
		
		// Get options from NBT
		NBTTagCompound compound =(new NBTTagCompound());
		compound.setCompoundTag("gameRules", nBTGameRules);
		loadRules(compound);
	}
	
	// Change these at a later time
	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType worldType) {
		if(genlayerWorldChunkManager) {
			return GenLayer.initializeAllBiomeGenerators(seed, worldType);
		} else {
			return GenLayerAlpha.initializeAllBiomeGenerators(seed, worldType);
		}
	}
	
	public static WorldType defaultWorldType() {
		if(genlayerWorldChunkManager) {
			return WorldType.DEFAULT;
		} else {
			return WorldType.ALPHA;
		}
	}
	
	public static WorldType defaultWorldType(boolean snowCovered) {
		if(genlayerWorldChunkManager) {
			return WorldType.DEFAULT;
		} else {
			return snowCovered ? WorldType.ALPHA_SNOW : WorldType.ALPHA;
		}
	}
	
	public static WorldProvider getWorldProviderForSurface() {
		if(genlayerWorldChunkManager) {
			return new WorldProviderSurface();
		} else {
			return new WorldProviderSurfaceClassic();
		}
	}

}
